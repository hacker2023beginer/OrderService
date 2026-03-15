package com.study.orderservice.mapper;

import com.study.orderservice.dto.OrderItemDto;
import com.study.orderservice.dto.OrderItemRequestDto;
import com.study.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "itemId", source = "item.id")
    OrderItemDto toDto(OrderItem orderItem);

    OrderItem toEntity(OrderItemDto orerItemDto);

    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "item.id", source = "itemId")
    OrderItem toEntity(OrderItemRequestDto orerItemRequestDto);
}
