package vitalii.serdiuk.test.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import vitalii.serdiuk.test.model.Book;
@Slf4j
@Component
@StepScope
public class BookProcessor implements ItemProcessor<Book, Book> {

    @Override
    public Book process(final Book book) throws Exception {
        final String title = book.getTitle().toUpperCase();
        final String author = book.getAuthor().toUpperCase();
        final String genre = book.getGenre().toUpperCase();
        final int height = book.getHeight();
        final String publisher = book.getPublisher().toUpperCase();

        final Book transformedBook = Book.builder()
                .title(title)
                .author(author)
                .genre(genre)
                .height(height)
                .publisher(publisher)
                .build();

        log.info("Converting (" + book + ") into (" + transformedBook + ")");

        return transformedBook;
    }
}
