package com.clozex.shop.repository.book;

import com.clozex.shop.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query("SELECT b FROM Book b JOIN FETCH b.categories c WHERE c.id = :categoryId")
    Page<Book> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
}
