package org.example.ui;

import java.util.Scanner;

import org.example.model.Book;
import org.example.model.Member;
import org.example.service.Library;

/**
 * Konsolgränssnittet. Läser användarens val med Scanner och anropar Library.
 * All inmatning läses som hela rader så att felaktig inmatning aldrig kraschar programmet.
 */
public class ConsoleMenu {

    private final Library library;
    private final Scanner scanner;

    public ConsoleMenu(Library library, Scanner scanner) {
        this.library = library;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Välj: ").toLowerCase();
            try {
                switch (choice) {
                    case "1" -> addBook();
                    case "2" -> registerMember();
                    case "3" -> borrowBook();
                    case "4" -> returnBook();
                    case "5" -> searchBooks();
                    case "6" -> showAllBooks();
                    case "7" -> showAllMembers();
                    case "e" -> running = false;
                    default -> System.out.println("Ogiltigt val: \"" + choice + "\". Försök igen.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Fel: " + e.getMessage());
            }
        }
        System.out.println("Hejdå!");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Bibliotekshanteraren");
        System.out.println("====================");
        System.out.println("1. Lägg till bok");
        System.out.println("2. Registrera medlem");
        System.out.println("3. Låna bok");
        System.out.println("4. Lämna tillbaka bok");
        System.out.println("5. Sök bok (titel eller författare)");
        System.out.println("6. Visa alla böcker och status");
        System.out.println("7. Visa alla medlemmar");
        System.out.println("e. Avsluta");
    }

    // Alla menyval
    private void addBook() {
        String isbn = readNonEmpty("ISBN: ");
        String title = readNonEmpty("Titel: ");
        String author = readNonEmpty("Författare: ");
        Book book = new Book(isbn, title, author);
        library.addBook(book);
        System.out.println("Boken lades till: " + book);
    }

    private void registerMember() {
        String name = readNonEmpty("Namn: ");
        Member member = library.registerMember(name);
        System.out.println("Medlem registrerad: " + member);
    }

    private void borrowBook() {
        String isbn = readNonEmpty("ISBN på boken: ");
        int memberId = readInt("Medlems-id: ");
        Book book = library.borrowBook(isbn, memberId);
        System.out.println("Utlånad: " + book);
    }

    private void returnBook() {
        String isbn = readNonEmpty("ISBN på boken: ");
        Member member = library.returnBook(isbn);
        System.out.println("Boken är återlämnad av " + member.getName() + ".");
    }

    private void searchBooks() {
        String query = readNonEmpty("Sökord - titel eller författare: ");
        Book[] result = library.searchBooks(query);
        if (result.length == 0) {
            System.out.println("Inga böcker matchade \"" + query + "\".");
            return;
        }
        System.out.println("Hittade " + result.length + " bok/böcker:");
        for (int i = 0; i < result.length; i++) {
            System.out.println("  - " + result[i]);
        }
    }

    private void showAllBooks() {
        if (library.getBookCount() == 0) {
            System.out.println("Biblioteket har inga böcker ännu.");
            return;
        }
        for (int i = 0; i < library.getBookCount(); i++) {
            Member borrower = library.getBorrower(i);
            String status = (borrower == null)
                    ? "Tillgänglig"
                    : "Utlånad till " + borrower.getName() + " (#" + borrower.getId() + ")";
            System.out.println((i + 1) + ". " + library.getBook(i) + " – " + status);
        }
    }

    private void showAllMembers() {
        if (library.getMemberCount() == 0) {
            System.out.println("Inga medlemmar registrerade ännu.");
            return;
        }
        for (int i = 0; i < library.getMemberCount(); i++) {
            System.out.println("  " + library.getMember(i));
        }
    }

    //Hjälpmetoder för säker inmatning
    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            // När inmatningen tog slut till exempel med ctrl+d - avsluta snyggt i stället för att krascha.
            System.out.println();
            System.out.println("Ingen mer inmatning – avslutar.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Fältet får inte vara tomt.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\"" + input + "\" är inget heltal. Försök igen.");
            }
        }
    }
}