package src.model;

public class Slug {
    private final String slug;
    private final String title;

    public Slug(String slug, String title) {
        this.slug = slug;
        this.title = title;
    }

    public String toString() {
        return String.format("(%s, %s)", slug, title);
    }

    public String getSlug() {
        return slug;
    }
}
