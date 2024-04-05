package com.example.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DemoRepository {
    private final JdbcClient jdbcClient;

    public DemoRepository(JdbcTemplate template, JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Book> getAllBooks() {
        return jdbcClient.sql("SELECT * FROM book").query(
                (rs, rowNum) -> new Book(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getLong("author_id"),
                    rs.getString("genre"))
        ).list();
    }

    public void addBook(Book newBook) {
        String sql = "INSERT INTO book (name, author_id, genre) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(sql)
                .params(newBook.getName(), newBook.getAuthorId(), newBook.getGenre())
                .update(keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            newBook.setId(key.longValue());
        }
    }

    public Book getBookById(Long id) {
        return jdbcClient.sql("SELECT * FROM book WHERE id = :id")
                .params(Map.of("id", id))
                .query(
                (rs, rowNum) -> new Book(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("author_id"),
                        rs.getString("genre"))
        ).single();
    }

    public void deleteBookById(Long id) {
        jdbcClient.sql("DELETE FROM book WHERE id = :id")
                .params(Map.of("id", id))
                .update();
    }
}
