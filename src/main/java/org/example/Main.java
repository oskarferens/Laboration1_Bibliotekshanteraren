package org.example;

import org.example.service.Library;
import org.example.ui.ConsoleMenu;

import java.util.Scanner;

public class Main {

    // Medvetet liten startkapacitet så att den dynamiska array växlingen syns direkt vid test.
    private static final int BOOK_CAPACITY = 2;
    private static final int MEMBER_CAPACITY = 2;

    static void main() {
        Library library = new Library(BOOK_CAPACITY, MEMBER_CAPACITY);
        Scanner scanner = new Scanner(System.in);
        ConsoleMenu menu = new ConsoleMenu(library, scanner);
        menu.run();
    }
}