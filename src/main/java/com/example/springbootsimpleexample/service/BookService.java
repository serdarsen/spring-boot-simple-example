package com.example.springbootsimpleexample.service;

import com.example.springbootsimpleexample.model.Book;

import java.util.List;
import java.util.Map;

public interface BookService {
    List<Book> findAll();
    List<Book> findByTitle(String title);
    Book findById(Long id);
    Book create(Book book);
    Book update(Map<String, String> updates, Long id);
    Book update(Book book, Long id);
    void deleteById(Long id);
}
