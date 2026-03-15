package com.study.orderservice.service.impl;

import com.study.orderservice.dto.OrderItemDto;
import com.study.orderservice.entity.Item;
import com.study.orderservice.entity.Order;
import com.study.orderservice.entity.OrderItem;
import com.study.orderservice.exception.ItemServiceException;
import com.study.orderservice.exception.OrderItemServiceException;
import com.study.orderservice.exception.OrderServiceException;
import com.study.orderservice.repository.ItemRepository;
import com.study.orderservice.repository.OrderItemRepository;
import com.study.orderservice.repository.OrderRepository;
import com.study.orderservice.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, ItemRepository itemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public OrderItem create(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemServiceException("OrderItem not found"));
    }

    @Override
    @Transactional
    public OrderItem updateOrderItemById(Long id, OrderItemDto dto) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemServiceException("OrderItem not found"));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ItemServiceException("Item not found"));
        orderItem.setItem(item);
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new OrderServiceException("Order not found"));
        orderItem.setOrder(order);
        orderItem.setQuantity(dto.getQuantity());
        return orderItemRepository.save(orderItem);
    }

    @Override
    @Transactional
    public void deleteOrderItemById(Long id) {
        orderItemRepository.deleteById(id);
    }
}
