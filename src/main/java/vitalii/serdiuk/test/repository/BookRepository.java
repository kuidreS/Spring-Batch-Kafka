package vitalii.serdiuk.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vitalii.serdiuk.test.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
