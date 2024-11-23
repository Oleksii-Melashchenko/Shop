package com.clozex.shop.mapper;

import com.clozex.shop.config.MapperConfig;
import com.clozex.shop.dto.BookDto;
import com.clozex.shop.dto.CreateBookRequestDto;
import com.clozex.shop.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
