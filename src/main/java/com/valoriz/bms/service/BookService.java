package com.valoriz.bms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.valoriz.bms.entity.Book;

public interface BookService {

	public Book createBook(Book book);

	public Book createBook(Book book, MultipartFile imageFile);

	public List<Book> getAllBooks();

	public Book getBookByUniqueId(String uniqueId);

	public void deleteBook(String id);

	public Map<String, Object> getExternalBookDetails(String isbn);
}
