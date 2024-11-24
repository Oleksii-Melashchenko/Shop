package com.clozex.shop.repository.book;

import com.clozex.shop.exception.SpecificationProviderNotFoundException;
import com.clozex.shop.model.Book;
import com.clozex.shop.repository.SpecificationProvider;
import com.clozex.shop.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(
                        ()
                                -> new SpecificationProviderNotFoundException(
                                        "Can`t find specification parameter for key:" + key));
    }

}
