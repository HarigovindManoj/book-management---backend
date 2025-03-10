package com.valoriz.bms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.valoriz.bms.entity.Book;
import com.valoriz.bms.service.BookService;

@RestController
@RequestMapping("/book")
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {

	@Autowired
	private BookService bookService;

	// Create a new book with image upload support
	@PostMapping(value = "/createBook", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Book> createBookWithImage(@RequestPart(value = "book") String bookJson,
			@RequestPart(value = "image", required = false) MultipartFile image) {

		try {
			// Configure ObjectMapper with JavaTimeModule for LocalDate support
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			Book book = mapper.readValue(bookJson, Book.class);

			// Create the book with image
			Book createdBook = bookService.createBook(book, image);
			return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
		} catch (JsonProcessingException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid book data format", e);
		}
	}

	// Endpoint for JSON-only requests
	@PostMapping("/createBookJson")
	public ResponseEntity<Book> createBook(@RequestBody Book book) {
		Book createdBook = bookService.createBook(book);
		return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
	}

	// Get all books
	@GetMapping("/allBooks")
	public ResponseEntity<List<Book>> getAllBooks() {
		List<Book> listOfBooks = bookService.getAllBooks();
		return new ResponseEntity<>(listOfBooks, HttpStatus.OK);
	}

	// Get a single book by id
	@GetMapping("/getbook/{id}")
	public ResponseEntity<Book> getBookById(@PathVariable("id") String id) {
		Book bookRetrieved = bookService.getBookByUniqueId(id);
		return new ResponseEntity<>(bookRetrieved, HttpStatus.OK);
	}

	// Delete a book
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteBookById(@PathVariable("id") String id) {
		bookService.deleteBook(id);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	// get data from external api
	@GetMapping("/external/{isbn}")
	public ResponseEntity<Map<String, Object>> getExternalBookDetails(@PathVariable String isbn) {
		return ResponseEntity.ok(bookService.getExternalBookDetails(isbn));
	}
}