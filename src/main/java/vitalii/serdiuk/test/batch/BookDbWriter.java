package vitalii.serdiuk.test.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vitalii.serdiuk.test.model.Book;
import vitalii.serdiuk.test.repository.BookRepository;

import java.util.List;

@Component
@Slf4j
public class BookDbWriter implements ItemWriter<Book> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void write(List<? extends Book> books) throws Exception {
        log.info("Batch Write Started");
        bookRepository.saveAll(books);
    }
}
