package org.example.service;

//Håller bibliotekets data i vanliga arrayer och innehåller all logik
//för böcker, medlemmar och utlåning.
//Utlåningen hålls i en parallell array: borrowers[i] är medlemmen som
//lånat books[i], eller null om boken är tillgänglig.

import org.example.model.Book;
import org.example.model.Member;

public class Library {

    private Book[] books;
    private Member[] borrowers;
    private int bookCount;

    private Member[] members;
    private int memberCount;
    private int nextMemberId = 1;

    public Library(int bookCapacity, int memberCapacity) {
        books = new Book[bookCapacity];
        borrowers = new Member[bookCapacity];
        members = new Member[memberCapacity];
    }

    public void addBook(Book book) {
        if (findBookIndex(book.isbn()) != -1) {
            throw new IllegalArgumentException("En bok med ISBN " + book.isbn() + " finns redan.");
        }
        if (bookCount == books.length) {
            throw new IllegalStateException("Biblioteket är fullt. Kan inte lägga till fler böcker.");
        }
        books[bookCount] = book;
        borrowers[bookCount] = null;
        bookCount++;
    }

    public int getBookCount() {
        return bookCount;
    }

    public Book getBook(int index) {
        return books[index];
    }

    // Returnerar medlemmen som lånat boken.
    public Member getBorrower(int index) {
        return borrowers[index];
    }

    // Linjär sökning på ISBN. Returnerar index eller -1 om boken inte finns.
    private int findBookIndex(String isbn) {
        String wanted = isbn.trim();
        for (int i = 0; i < bookCount; i++) {
            if (books[i].isbn().equalsIgnoreCase(wanted)) {
                return i;
            }
        }
        return -1;
    }

    // Söker efter böcker som matchar en del av titel eller författare, utan hänsyn till stora och små bokstäver.
    // Först räknas antalet träffar, sedan skapas en array och fylls med de böcker som matchar.
    public Book[] searchBooks(String query) {
        String q = query.trim().toLowerCase();

        // Räknar först hur många böcker som matchar sökningen.
        int matches = 0;
        for (int i = 0; i < bookCount; i++) {
            if (matches(books[i], q)) {
                matches++;
            }
        }

        // Skapar en array med exakt rätt storlek för alla träffar.
        Book[] result = new Book[matches];
        int pos = 0;
        for (int i = 0; i < bookCount; i++) {
            if (matches(books[i], q)) {
                result[pos] = books[i];
                pos++;
            }
        }
        return result;
    }

    private boolean matches(Book book, String lowerCaseQuery) {
        return book.title().toLowerCase().contains(lowerCaseQuery)
                || book.author().toLowerCase().contains(lowerCaseQuery);
    }

    // Delen som ansvarar för registrering och sökning medlemar efter id.
    public Member registerMember(String name) {
        if (memberCount == members.length) {
            throw new IllegalStateException("Medlemsregistret är fullt - kan inte registrera fler medlemmar.");
        }
        Member member = new Member(nextMemberId, name);
        nextMemberId++;
        members[memberCount] = member;
        memberCount++;
        return member;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public Member getMember(int index) {
        return members[index];
    }

    // Linjär sökning på medlems id. Returnerar medlemmen eller null.
    private Member findMember(int id) {
        for (int i = 0; i < memberCount; i++) {
            if (members[i].getId() == id) {
                return members[i];
            }
        }
        return null;
    }

    // Delen som ansvarar för utlåning och återlämning av böcker.
    // Lånar ut en bok. Returnerar boken som lånades ut.
    public Book borrowBook(String isbn, int memberId) {
        int index = findBookIndex(isbn);
        if (index == -1) {
            throw new IllegalArgumentException("Ingen bok med ISBN " + isbn + " hittades.");
        }
        Member member = findMember(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Ingen medlem med id " + memberId + " hittades.");
        }
        if (borrowers[index] != null) {
            throw new IllegalStateException("Boken är redan utlånad till " + borrowers[index].getName() + ".");
        }
        member.registerLoan(); // Här kastar fel om medlemmen har max antal lån.
        borrowers[index] = member;
        return books[index];
    }

    // Den delen ansvarar för återlämning + returnerar medlemmen som hade lånat den.
    public Member returnBook(String isbn) {
        int index = findBookIndex(isbn);
        if (index == -1) {
            throw new IllegalArgumentException("Ingen bok med ISBN " + isbn + " hittades.");
        }
        Member borrower = borrowers[index];
        if (borrower == null) {
            throw new IllegalStateException("Boken är inte utlånad.");
        }
        borrower.registerReturn();
        borrowers[index] = null;
        return borrower;
    }
}