package com.example.springbootsimpleexample;

import com.example.springbootsimpleexample.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class MockDataLoader implements CommandLineRunner {

	@Autowired
	private BookService bookService;

	@Override
	public void run(String... args) throws Exception {
//		Book book1 = new Book();
//		book1.setTitle("title1");
//		book1.setAuthor("author1");
//		bookService.create(book1);
	}
}
