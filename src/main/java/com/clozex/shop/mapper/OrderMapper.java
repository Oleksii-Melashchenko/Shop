package com.clozex.shop.mapper;

import com.clozex.shop.config.MapperConfig;
import com.clozex.shop.dto.order.OrderDto;
import com.clozex.shop.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "createdAt", source = "orderDate")
    @Mapping(target = "orderItems", source = "orderItems")
    OrderDto toDto(Order order);

    Order toModel(OrderDto dto);
}
