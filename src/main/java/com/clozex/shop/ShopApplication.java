package com.clozex.shop;

import com.clozex.shop.model.Book;
import com.clozex.shop.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShopApplication {

    private final BookService bookService;

    @Autowired
    public ShopApplication(BookService bookService) {
        this.bookService = bookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book book = new Book();
                book.setAuthor("aaa");
                book.setPrice(BigDecimal.valueOf(12));
                book.setTitle("sdasd");
                book.setIsbn("111");

                bookService.save(book);

                bookService.findAll();
            }
        };
    }
}
