package org.example;

import org.example.service.Library;
import org.example.ui.ConsoleMenu;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final int BOOK_CAPACITY = 5;
    private static final int MEMBER_CAPACITY = 5;

    static void main() {
        Library library = new Library(BOOK_CAPACITY, MEMBER_CAPACITY);
        Scanner scanner = new Scanner(System.in);
        ConsoleMenu menu = new ConsoleMenu(library, scanner);
        menu.run();
    }
}
