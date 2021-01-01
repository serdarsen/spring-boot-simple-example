package com.example.springbootsimpleexample.controller;

import com.example.springbootsimpleexample.dto.BookDTO;
import com.example.springbootsimpleexample.model.Book;
import com.example.springbootsimpleexample.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<BookDTO> findAll() {
        return bookService.findAll().stream()
                .map(book -> convertToDTO(book)).collect(Collectors.toList());
    }

    @GetMapping("/title/{title}")
    public List<BookDTO> findByTitle(@PathVariable String title) {
        return bookService.findByTitle(title).stream()
                .map(book -> convertToDTO(book)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BookDTO findById(@PathVariable Long id) {
        return convertToDTO(bookService.findById(id));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        book = bookService.create(book);
        return convertToDTO(book);
    }

    @PutMapping("/update/{id}")
    public BookDTO update(@RequestBody BookDTO bookDTO, @PathVariable Long id) {
        Book book = convertToEntity(bookDTO);
        book = bookService.update(book, id);
        return convertToDTO(book);
    }

    @PatchMapping("/patch/{id}")
    public BookDTO patch(@RequestBody Map<String, String> updates, @PathVariable Long id) {
        return convertToDTO(bookService.update(updates, id));
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        bookService.findById(id);
        bookService.deleteById(id);
    }

    private BookDTO convertToDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }

    private Book convertToEntity(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }
}