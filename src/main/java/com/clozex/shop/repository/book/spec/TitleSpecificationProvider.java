package com.clozex.shop.repository.book.spec;

import com.clozex.shop.model.Book;
import com.clozex.shop.repository.SpecificationProvider;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "title";

    @Override
    public Specification<Book> getSpecification(String... params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Arrays.stream(params)
                    .map(param -> criteriaBuilder.like(criteriaBuilder.lower(root.get(KEY)), "%" + param.toLowerCase() + "%"))
                    .toList();
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
