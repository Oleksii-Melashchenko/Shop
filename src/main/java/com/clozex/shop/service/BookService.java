package com.clozex.shop.service;

import com.clozex.shop.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
