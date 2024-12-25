package com.clozex.shop.mapper;

import com.clozex.shop.config.MapperConfig;
import com.clozex.shop.dto.order.OrderItemDto;
import com.clozex.shop.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    OrderItemDto toDto(OrderItem orderItem);
}
