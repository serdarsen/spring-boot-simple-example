package com.example.springbootsimpleexample.service;

import com.example.springbootsimpleexample.exception.BookIdMismatchException;
import com.example.springbootsimpleexample.exception.BookNotFoundException;
import com.example.springbootsimpleexample.model.Book;
import com.example.springbootsimpleexample.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    BookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Book create(Book book) {
        final Book newBook = new Book();
        newBook.setTitle(book.getTitle());
        newBook.setAuthor(book.getAuthor());

        return bookRepository.save(newBook);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Book update(Book book, Long id) {
        if (book.getId() != id) {
            throw new BookIdMismatchException();
        }

        findById(id);

        return bookRepository.save(book);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Book update(Map<String, String> updates, Long id) {
        final Book book = findById(id);
        book.setTitle(updates.get("title"));
        book.setAuthor(updates.get("author"));

        return bookRepository.save(book);
    }

    @Override
    public void deleteById(Long id) {
        findById(id);
        bookRepository.deleteById(id);
    }
}
