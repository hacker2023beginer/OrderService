package com.study.orderservice.controller;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.entity.Order;
import com.study.orderservice.mapper.OrderMapper;
import com.study.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestBody @Valid OrderDto dto){
        Order order = new Order();
        orderMapper.updateOrderFromDto(dto, order);
        Order savedOrder = orderService.create(order);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderMapper.toDto(savedOrder));
    }
}
