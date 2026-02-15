package com.study.orderservice.controller;

import com.study.orderservice.dto.ItemDto;
import com.study.orderservice.dto.OrderItemDto;
import com.study.orderservice.entity.Item;
import com.study.orderservice.entity.OrderItem;
import com.study.orderservice.mapper.OrderItemMapper;
import com.study.orderservice.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderitems")
public class OrderItemController {
    private final OrderItemService orderItemService;
    private final OrderItemMapper orderItemMapper;

    public OrderItemController(OrderItemService orderService, OrderItemMapper orderMapper) {
        this.orderItemService = orderService;
        this.orderItemMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> create(@RequestBody @Valid OrderItemDto orderItemDto){
        OrderItem orderItem = orderItemService.create(orderItemMapper.toEntity(orderItemDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderItemMapper.toDto(orderItem));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getById(@PathVariable Long id){
        OrderItem item = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(orderItemMapper.toDto(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> updateItemById(@PathVariable Long id, @RequestBody OrderItemDto orderItemDto){
        OrderItem item = orderItemService.updateOrderItemById(id, orderItemDto);
        return ResponseEntity.ok(orderItemMapper.toDto(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItemById(@PathVariable Long id){
        orderItemService.deleteOrderItemById(id);
        return ResponseEntity.noContent().build();
    }
}
