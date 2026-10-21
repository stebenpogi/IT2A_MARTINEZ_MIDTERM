package com.scc.model;

public record Book(int id, String title, String author, String isbn, String status) {

    public static final String AVAILABLE = "AVAILABLE";
    public static final String BORROWED = "BORROWED";

    public Book(String title, String author, String isbn, String status) {
        this(0, title, author, isbn, status);
    }
}