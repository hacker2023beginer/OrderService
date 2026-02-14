package com.study.orderservice.mapper;

import com.study.orderservice.dto.OrderItemDto;
import com.study.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);

    OrderItem toEntity(OrderItemDto orerItemDto);
}
