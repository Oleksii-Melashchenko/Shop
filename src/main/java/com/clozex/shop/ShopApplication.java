package com.clozex.shop;

import com.clozex.shop.model.Book;
import com.clozex.shop.service.BookService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class ShopApplication {
    private final BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setAuthor("aaa");
            book.setPrice(BigDecimal.valueOf(12));
            book.setTitle("sdasd");
            book.setIsbn("111");

            bookService.save(book);

            System.out.println(bookService.findAll());
        };
    }
}
