package com.valoriz.bms.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Document(collection = "Books")
public class Book {

	@Id
	private String id;

	private String uniqueId;// For the sequence-based ID

	@NotBlank(message = "Title is required")
	@Size(max = 100, message = "Title must be less than 100 characters")
	private String title;

	@NotBlank(message = "Author is required")
	@Size(max = 50, message = "Author must be less than 50 characters")
	private String author;

	@NotNull(message = "Publication date is required")
	private LocalDate publicationDate;

	@NotBlank(message = "ISBN is required")
	@Pattern(regexp = "\\d{13}", message = "ISBN must be a 13-digit number")
	private String isbn;

	@NotBlank(message = "Genre is required")
	private String genre;

	@Min(value = 1, message = "Rating must be at least 1")
	@Max(value = 5, message = "Rating must be at most 5")
	private int rating;

	private String imagePath; // field for image path

	public Book() {
	}

	public Book(String id, String uniqueId,
			@NotBlank(message = "Title is required") @Size(max = 100, message = "Title must be less than 100 characters") String title,
			@NotBlank(message = "Author is required") @Size(max = 50, message = "Author must be less than 50 characters") String author,
			@NotNull(message = "Publication date is required") LocalDate publicationDate,
			@NotBlank(message = "ISBN is required") @Pattern(regexp = "\\d{13}", message = "ISBN must be a 13-digit number") String isbn,
			@NotBlank(message = "Genre is required") String genre,
			@Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating must be at most 5") int rating,
			String imagePath) {
		super();
		this.id = id;
		this.uniqueId = uniqueId;
		this.title = title;
		this.author = author;
		this.publicationDate = publicationDate;
		this.isbn = isbn;
		this.genre = genre;
		this.rating = rating;
		this.imagePath = imagePath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
