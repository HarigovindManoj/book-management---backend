package com.valoriz.bms.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.valoriz.bms.entity.Book;
import com.valoriz.bms.repository.BookRepository;

import jakarta.validation.Valid;

@Service
public class BookServiceImpl implements BookService {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${file.upload-dir:uploads/images}")
	private String uploadDir;

	private static int counter = 1; // Counter to generate unique sequence-based ID

	/**
	 * Creates a new book entry with validation and a unique ID.
	 */
	public Book createBook(@Valid Book book, MultipartFile imageFile) {
		if (bookRepository.existsByIsbn(book.getIsbn())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book with this ISBN already exists.");
		}

		String uniqueId = createUniqueId();
		book.setUniqueId(uniqueId);

		// Handle image upload if provided
		if (imageFile != null && !imageFile.isEmpty()) {
			String imagePath = saveImage(imageFile, uniqueId);
			book.setImagePath(imagePath);
		}

		return bookRepository.save(book);
	}

	/**
	 * Creates a book without an image
	 */
	public Book createBook(@Valid Book book) {
		return createBook(book, null);
	}

	/**
	 * Saves an image file to the file system and returns the path
	 */
	private String saveImage(MultipartFile imageFile, String uniqueId) {
		try {
			// Create directory if it doesn't exist
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Generate a unique filename
			String originalFilename = imageFile.getOriginalFilename();
			String fileExtension = "";
			if (originalFilename != null && originalFilename.contains(".")) {
				fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}
			String filename = uniqueId + "_" + UUID.randomUUID().toString() + fileExtension;

			// Save the file
			Path filePath = uploadPath.resolve(filename);
			Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// Return the relative path to be stored in the database
			return uploadDir + "/" + filename;

		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Could not save image file: " + e.getMessage());
		}
	}

	/**
	 * Retrieves all books from the database.
	 */
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	/**
	 * Retrieves a book by its unique ID.
	 */
	public Book getBookByUniqueId(String uniqueId) {
		System.out.println("Searching for book with ID: " + uniqueId);
		return bookRepository.findByUniqueId(uniqueId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with uniqueId: " + uniqueId));
	}

	/**
	 * Deletes a book by its unique ID.
	 */
	public void deleteBook(String uniqueId) {
		Book book = getBookByUniqueId(uniqueId);

		// Delete the image file if exists
		if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
			try {
				Path imagePath = Paths.get(book.getImagePath());
				Files.deleteIfExists(imagePath);
			} catch (IOException e) {
				System.err.println("Failed to delete image file: " + e.getMessage());
			}
		}

		bookRepository.delete(book);
	}

	/**
	 * Generates a unique book ID in the format B-001, B-002, etc.
	 */
	private String createUniqueId() {
		String uniqueId;
		do {
			uniqueId = String.format("B-%03d", counter++);
		} while (bookRepository.existsByUniqueId(uniqueId)); // Ensures no duplicate IDs
		return uniqueId;
	}

	/**
	 * Fetches book details from the Google Books API using an ISBN.
	 */
	public Map<String, Object> getExternalBookDetails(String isbn) {
		try {
			String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
			Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

			if (response != null && response.containsKey("items") && ((List<?>) response.get("items")).size() > 0) {
				Map<String, Object> item = (Map<String, Object>) ((List<?>) response.get("items")).get(0);
				Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");

				// Extract relevant information
				return Map.of("title", volumeInfo.getOrDefault("title", ""), "authors",
						volumeInfo.getOrDefault("authors", List.of()), "publisher",
						volumeInfo.getOrDefault("publisher", ""), "description",
						volumeInfo.getOrDefault("description", ""), "pageCount",
						volumeInfo.getOrDefault("pageCount", 0), "categories",
						volumeInfo.getOrDefault("categories", List.of()), "language",
						volumeInfo.getOrDefault("language", ""), "imageUrl", getImageUrl(volumeInfo));
			}
			return Map.of();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching external book details",
					e);
		}
	}

	/**
	 * Retrieves the book's thumbnail image URL from Google Books API response.
	 */
	private String getImageUrl(Map<String, Object> volumeInfo) {
		if (volumeInfo.containsKey("imageLinks")) {
			Map<String, String> imageLinks = (Map<String, String>) volumeInfo.get("imageLinks");
			return imageLinks.getOrDefault("thumbnail", "");
		}
		return "";
	}
}