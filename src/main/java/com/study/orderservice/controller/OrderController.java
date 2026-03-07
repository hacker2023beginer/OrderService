package com.study.orderservice.controller;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.dto.OrderRequestDto;
import com.study.orderservice.dto.UserDto;
import com.study.orderservice.entity.Order;
import com.study.orderservice.exception.OrderServiceException;
import com.study.orderservice.mapper.OrderMapper;
import com.study.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
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

    @PostMapping("/create")
    public ResponseEntity<OrderDto> create(@RequestBody @Valid OrderRequestDto dto, HttpServletRequest request){
        Boolean valid = orderService.validateUser(dto.getUserId(), dto.getEmail(), request.getHeader(HttpHeaders.AUTHORIZATION));
        if (valid == null || !valid){
            throw new OrderServiceException("User not found");
        }
        System.out.println("Start creating");
        Order order = new Order();
        orderMapper.updateOrderFromRequestDto(dto, order);
        Order savedOrder = orderService.create(order);
        OrderDto orderDto = orderMapper.toDto(savedOrder);
        System.out.println("AUTH HEADER: " + request.getHeader(HttpHeaders.AUTHORIZATION));
        UserDto userDto = orderService.getUserByEmail(order.getEmail(), request.getHeader(HttpHeaders.AUTHORIZATION));
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
