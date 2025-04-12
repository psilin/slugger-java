package src.model;

public class Slug {
    private final int id;
    private final String slug;
    private final String title;
    private final String url;
    private final String locale;
    private final String[] products;
    private final String[] topics;
    private final String summary;

    public Slug(int id, String slug, String title, String url, String locale, String[] products, String[] topics, String summary) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.url = url;
        this.locale = locale;
        this.products = products;
        this.topics = topics;
        this.summary = summary;
    }

    public String toString() {
        String productsString = "";
        if (products != null)
            productsString = String.join("|", products);
        String topicsString = "";
        if (topics != null)
            topicsString = String.join("|", topics);
        return String.format("(%d %s, %s, %s, %s, %s, %s, %s)", id, slug, title, url, locale, productsString, topicsString, summary);
    }

    public int getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getLocale() {
        return locale;
    }

    public String[] getProducts() {
        return products;
    }

    public String[] getTopics() {
        return topics;
    }

    public String getSummary() {
        return summary;
    }
}
