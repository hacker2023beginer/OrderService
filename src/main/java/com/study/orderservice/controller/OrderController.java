package com.study.orderservice.controller;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.dto.UserDto;
import com.study.orderservice.entity.Order;
import com.study.orderservice.exception.OrderServiceException;
import com.study.orderservice.mapper.OrderMapper;
import com.study.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
        Boolean valid = orderService.validateUser(dto.getUserId(), dto.getEmail());
        if (valid == null || !valid){
            throw new OrderServiceException("User not found");
        }

        Order order = new Order();
        orderMapper.updateOrderFromDto(dto, order);
        Order savedOrder = orderService.create(order);
        OrderDto orderDto = orderMapper.toDto(savedOrder);
        UserDto userDto = orderService.getUserByEmail(order.getEmail());
        orderDto.setUser(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id){
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getOrdersByDatesAndStatus(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            @RequestParam String status,
            Pageable pageable
    ){
        Page<Order> orderPage = orderService.getOrders(from, to, status, pageable);
        Page<OrderDto> orderDtoPage = orderPage.map(orderMapper::toDto);
        return ResponseEntity
                .ok(orderDtoPage);
    }

    @GetMapping("/userid/{id}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Long id){
        List<Order> orderList = orderService.getOrdersByUserId(id);
        return ResponseEntity.ok(orderList.stream().map(orderMapper::toDto).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrderById(@PathVariable Long id, @RequestBody OrderDto orderDto){
        Order order = orderService.updateOrderById(id, orderDto);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id){
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}
