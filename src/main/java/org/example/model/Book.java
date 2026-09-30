package org.example.model;

//En bok i biblioteket. Record eftersom en bok är ett oföränderligt värde.
//ISBN, titel och författare ändras aldrig efter att boken skapats.
public record Book(String isbn, String title, String author) {

    // Kompakt konstruktor-validerar indata innan fälten sätts.
    public Book {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN får inte vara tomt.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Titeln får inte vara tom.");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Författaren får inte vara tom.");
        }
        isbn = isbn.trim();
        title = title.trim();
        author = author.trim();
    }

    @Override
    public String toString() {
        return "\"" + title + "\" av " + author + " (ISBN: " + isbn + ")";
    }
}