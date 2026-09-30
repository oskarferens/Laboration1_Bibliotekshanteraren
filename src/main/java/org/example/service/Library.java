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
        // Dynamisk kapacitet: om arrayen är full skapas en större i stället för att kasta fel.
        if (bookCount == books.length) {
            growBookArrays();
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

    // Sorterar böckerna i bokstavsordning på titel med bubble sort.
    // borrowers-arrayen byter plats i samma takt så att utlåningen följer med rätt bok.
    public void sortBooksByTitle() {
        for (int pass = 0; pass < bookCount - 1; pass++) {
            boolean swapped = false;
            for (int i = 0; i < bookCount - 1 - pass; i++) {
                if (books[i].title().compareToIgnoreCase(books[i + 1].title()) > 0) {
                    swapBooks(i, i + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // Redan sorterad - inga fler varv behövs.
            }
        }
    }

    // Byter plats på två böcker och deras låntagare samtidigt.
    private void swapBooks(int a, int b) {
        Book tempBook = books[a];
        books[a] = books[b];
        books[b] = tempBook;

        Member tempBorrower = borrowers[a];
        borrowers[a] = borrowers[b];
        borrowers[b] = tempBorrower;
    }

    // Dynamisk kapacitet: skapar dubbelt så stora arrayer och kopierar över elementen för hand.
    private void growBookArrays() {
        int newCapacity = books.length * 2;
        Book[] newBooks = new Book[newCapacity];
        Member[] newBorrowers = new Member[newCapacity];
        for (int i = 0; i < bookCount; i++) {
            newBooks[i] = books[i];
            newBorrowers[i] = borrowers[i];
        }
        books = newBooks;
        borrowers = newBorrowers;
        System.out.println("(Bokarrayen var full - kapaciteten utökades till " + newCapacity + ".)");
    }

    // Delen som ansvarar för registrering och sökning medlemar efter id.
    public Member registerMember(String name) {
        // Dynamisk kapacitet: om arrayen är full skapas en större i stället för att ge fel.
        if (memberCount == members.length) {
            growMemberArray();
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

    // Statistik: hittar medlemmen med flest aktiva lån via en linjär max sökning.
    // Returnerar null om ingen medlem har något aktivt lån.
    public Member getMemberWithMostLoans() {
        Member top = null;
        for (int i = 0; i < memberCount; i++) {
            if (members[i].getActiveLoans() > 0
                    && (top == null || members[i].getActiveLoans() > top.getActiveLoans())) {
                top = members[i];
            }
        }
        return top;
    }

    // Dynamisk kapacitet för medlemmar. Dubbelt så stor array, element kopieras för hand.
    private void growMemberArray() {
        int newCapacity = members.length * 2;
        Member[] newMembers = new Member[newCapacity];
        for (int i = 0; i < memberCount; i++) {
            newMembers[i] = members[i];
        }
        members = newMembers;
        System.out.println("(Medlemsarrayen var full - kapaciteten utökades till " + newCapacity + ".)");
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
            throw new IllegalArgumentException("Ingen medlem med ID-" + memberId + " hittades.");        }
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