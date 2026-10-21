package com.scc.ui;

import com.scc.dao.BookDao;
import com.scc.dao.BookDao.DaoException;
import com.scc.model.Book;

import java.util.List;
import java.util.Scanner;

public class LibraryMenu {

    private static final int MENU_WIDTH = 52;

    private final Scanner scanner = new Scanner(System.in);
    private final BookDao dao;

    public LibraryMenu(BookDao dao) {
        this.dao = dao;
    }

    public void start() {
        try {
            dao.createTableIfNotExists();
        } catch (DaoException e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter choice: ");
            String choice = readLine();

            switch (choice) {
                case "1" -> addBook();
                case "2" -> viewBooks();
                case "3" -> {
                    System.out.println("\nExiting system... Thank you!");
                    running = false;
                }
                default -> System.out.println("\nInvalid choice. Please enter 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        String line = "=".repeat(MENU_WIDTH);
        System.out.println();
        System.out.println(line);
        System.out.println(center("LIBRARY BOOK CATALOG", MENU_WIDTH));
        System.out.println(line);
        System.out.println("[1] Add New Book");
        System.out.println("[2] View Book Catalog");
        System.out.println("[3] Exit System");
        System.out.println(line);
    }

    private void addBook() {
        System.out.println("\n--- ADD NEW BOOK ---");

        String title = promptNonEmpty("Enter Title: ");
        String author = promptNonEmpty("Enter Author: ");
        String isbn = promptIsbn();
        String status = promptStatus();

        try {
            dao.insert(new Book(title, author, isbn, status));
            System.out.println("\nSUCCESS: Book cataloged into database!");
        } catch (DaoException e) {
            System.out.println("\nERROR: " + e.getMessage());
        }
    }

    private String promptNonEmpty(String label) {
        while (true) {
            System.out.print(label);
            String value = readLine();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private String promptIsbn() {
        while (true) {
            System.out.print("Enter ISBN: ");
            String isbn = readLine();
            if (isbn.matches("\\d{13}")) {
                return isbn;
            }
            System.out.println("Invalid ISBN. It must be exactly 13 digits.");
        }
    }

    private String promptStatus() {
        while (true) {
            System.out.print("Enter Status (AVAILABLE / BORROWED): ");
            String status = readLine().toUpperCase();
            if (status.equals(Book.AVAILABLE) || status.equals(Book.BORROWED)) {
                return status;
            }
            System.out.println("Invalid status. Please enter AVAILABLE or BORROWED.");
        }
    }

    private String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "3";
    }

    private void viewBooks() {
        List<Book> books;
        try {
            books = dao.getAll();
        } catch (DaoException e) {
            System.out.println("\nERROR: " + e.getMessage());
            return;
        }

        String[] headers = {"ID", "TITLE", "AUTHOR", "ISBN", "STATUS"};
        int[] w = {2, 18, 18, 13, 9};
        for (Book b : books) {
            w[0] = Math.max(w[0], String.valueOf(b.id()).length());
            w[1] = Math.max(w[1], b.title().length());
            w[2] = Math.max(w[2], b.author().length());
            w[3] = Math.max(w[3], b.isbn().length());
            w[4] = Math.max(w[4], b.status().length());
        }

        String rowFormat = "| %-" + w[0] + "s | %-" + w[1] + "s | %-" + w[2]
                + "s | %-" + w[3] + "s | %-" + w[4] + "s |%n";
        StringBuilder border = new StringBuilder("+");
        for (int width : w) {
            border.append("-".repeat(width + 2)).append("+");
        }
        String banner = "=".repeat(Math.max(MENU_WIDTH, border.length()));

        System.out.println();
        System.out.println(banner);
        System.out.println(center("LIBRARY BOOK CATALOG", banner.length()));
        System.out.println(banner);

        System.out.println(border);
        System.out.printf(rowFormat, (Object[]) headers);
        System.out.println(border);
        for (Book b : books) {
            System.out.printf(rowFormat, b.id(), b.title(), b.author(), b.isbn(), b.status());
        }
        System.out.println(border);
        System.out.println("Total Records Displayed: " + books.size());
    }

    private String center(String text, int width) {
        return " ".repeat(Math.max(0, (width - text.length()) / 2)) + text;
    }
}