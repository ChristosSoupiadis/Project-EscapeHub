package escapehub;
/** Ένα βιβλίο από το Open Library. */
public class Book {
    private final String title;
    private final String author; // μπορεί να είναι null
    private final String year; // μπορεί να είναι null
    public Book(String title, String author, String year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getYear() {
        return year;
    }
    @Override
    public String toString() {
        return title + " - " + (author == null ? "άγνωστος συγγραφέας" : author) + (year == null ? "" : " (" + year + ")");
    }
}
