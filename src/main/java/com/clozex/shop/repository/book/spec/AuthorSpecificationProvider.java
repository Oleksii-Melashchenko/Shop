package com.clozex.shop.repository.book.spec;

import com.clozex.shop.model.Book;
import com.clozex.shop.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "author";

    @Override
    public Specification<Book> getSpecification(String... params) {
        return (root, query, criteriaBuilder)
                -> root.get(KEY).in(Arrays.stream(params).toArray());
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
