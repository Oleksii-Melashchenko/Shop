package com.clozex.shop.repository;

import com.clozex.shop.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
