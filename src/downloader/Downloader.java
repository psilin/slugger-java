package src.downloader;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import src.db.SlugsDB;
import src.model.Slug;
import src.model.SlugList;

public class Downloader {
    private static int DEFAULT_SLUGS_AMOUNT = 100;
    private static int DEFAULT_WORKERS_AMOUNT = 5;
    private static String STARTING_URL = "https://support.allizom.org/api/1/kb/";

    public static void main(String[] args) {
        int maxSlugs = processArgs(args);
        System.out.println("Starting download at most " + maxSlugs + " slug names...");

        ConcurrentLinkedQueue<String> inQueue = new ConcurrentLinkedQueue<String>();
        ConcurrentLinkedQueue<Slug> outQueue = new ConcurrentLinkedQueue<Slug>();
        int counter = 0;
        try {
            SlugList sl = downloadSlugList(STARTING_URL);
            System.out.println(sl.toString());
            for (Slug s: sl.getResults()) {
                inQueue.add(s.getSlug());
                counter += 1;
                if (counter >= maxSlugs)
                    break;
            }
            while (sl.getNext() != null && counter < maxSlugs) {
                sl = downloadSlugList(sl.getNext());
                System.out.println(sl.toString());
                for (Slug s: sl.getResults()) {
                    inQueue.add(s.getSlug());
                    counter += 1;
                    if (counter >= maxSlugs)
                        break;
                }
            }
            System.out.println("Downloaded " + counter + " slug names.");
            System.out.println(inQueue.toString());

            Thread[] workers = new Thread[DEFAULT_WORKERS_AMOUNT];
            for (int i = 0; i < DEFAULT_WORKERS_AMOUNT; i++) {
                workers[i] = Thread.startVirtualThread(new Runnable() {
                    public void run() {
                        while (true) {
                            String name = inQueue.poll();
                            if (name == null) {
                                return;
                            }
                            try {
                                Slug s = downloadSlug(STARTING_URL + name);
                                outQueue.add(s);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                });
            }
            for (int i = 0; i < DEFAULT_WORKERS_AMOUNT; i++)
                workers[i].join();

            System.out.println(outQueue.toString());
            try (SlugsDB db = new SlugsDB()) {
                System.out.println(String.format("Talking to db! %s", db.toString()));
                while (true) {
                    Slug s = outQueue.poll();
                    if (s == null) {
                        return;
                    }
                    int id = db.Save(s);
                    System.out.println(String.format("Saved slug %s with id: %d", s.toString(), id));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int processArgs(String[] args) {
        if (args.length == 0) {
            return DEFAULT_SLUGS_AMOUNT;
        }

        try {
            return Integer.parseInt(args[0]);
        } catch (Exception e) {
            return DEFAULT_SLUGS_AMOUNT;
        }
    }

    private static SlugList downloadSlugList(String urlString) throws Exception {
            URL url = new URI(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responsecode = conn.getResponseCode();
            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode + ", accessing: " + urlString);
            }

            String inline = "";
            try (Scanner scanner = new Scanner(url.openStream())) {
                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
            }

            return  new Gson().fromJson(inline, SlugList.class);
    }

    private static Slug downloadSlug(String urlString) throws Exception {
        URL url = new URI(urlString).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        //Getting the response code
        int responsecode = conn.getResponseCode();
        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode + ", accessing: " + urlString);
        }

        String inline = "";
        try (Scanner scanner = new Scanner(url.openStream())) {
            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }
        }

        return  new Gson().fromJson(inline, Slug.class);
    }
}
