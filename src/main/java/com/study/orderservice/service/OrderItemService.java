package com.study.orderservice.service;

import com.study.orderservice.dto.OrderItemDto;
import com.study.orderservice.entity.OrderItem;

public interface OrderItemService {
    OrderItem create(OrderItem orderItem);

    OrderItem getOrderItemById(Long id);

    OrderItem updateOrderItemById(Long id, OrderItemDto dto);

    void deleteOrderItemById(Long id);
}
