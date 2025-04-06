package src.model;

import java.util.List;

public class SlugList {
    private final int count;
    private final String next;
    private final String previous;
    private final List<Slug> results;

    public SlugList(int count, String next, String previous, List<Slug> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Slug list (%d, %s, %s):\n", count, next, previous));
        for (Slug s: results) {
            sb.append(String.format("\t%s\n", s.toString()));
        }
        return sb.toString();
    }

    public List<Slug> getResults() {
        return results;
    }

    public String getNext() {
        return next;
    }
}
