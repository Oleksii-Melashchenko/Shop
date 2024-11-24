package com.clozex.shop.repository.book;

import com.clozex.shop.dto.BookSearchParametersDto;
import com.clozex.shop.model.Book;
import com.clozex.shop.repository.SpecificationBuilder;
import com.clozex.shop.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String AUTHOR_KEY = "author";
    private static final String TITLE_KEY = "title";
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto params) {
        Specification<Book> specification = Specification.where(null);
        if (params.author() != null && params.author().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(AUTHOR_KEY)
                    .getSpecification(params.author()));
        }
        if (params.title() != null && params.title().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(TITLE_KEY)
                    .getSpecification(params.title()));
        }

        return specification;
    }
}
