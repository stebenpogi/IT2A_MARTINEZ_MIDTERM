package com.scc.dao;

import com.scc.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDao {

    public static class DaoException extends RuntimeException {
        public DaoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS books (
                id     INTEGER PRIMARY KEY AUTOINCREMENT,
                title  TEXT NOT NULL,
                author TEXT NOT NULL,
                isbn   TEXT NOT NULL,
                status TEXT NOT NULL CHECK (status IN ('AVAILABLE', 'BORROWED'))
            )
            """;

    private static final String INSERT_SQL =
            "INSERT INTO books (title, author, isbn, status) VALUES (?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
            "SELECT id, title, author, isbn, status FROM books ORDER BY id";

    public void createTableIfNotExists() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new DaoException("Could not create table: " + e.getMessage(), e);
        }
    }

    public void insert(Book book) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, book.title());
            ps.setString(2, book.author());
            ps.setString(3, book.isbn());
            ps.setString(4, book.status());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Could not save book: " + e.getMessage(), e);
        }
    }

    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            throw new DaoException("Could not load catalog: " + e.getMessage(), e);
        }
        return books;
    }
}