package com.valoriz.bms.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.valoriz.bms.entity.Book;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

	// method to check whether book is present in db using isbn number
	boolean existsByIsbn(String isbn);

	// Method to check if uniqueId exists
	boolean existsByUniqueId(String uniqueId);

	Optional<Book> findByUniqueId(String uniqueId);

}
