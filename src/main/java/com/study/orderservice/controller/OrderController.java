package com.study.orderservice.controller;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.dto.OrderRequestDto;
import com.study.orderservice.dto.PageResponse;
import com.study.orderservice.dto.UserDto;
import com.study.orderservice.entity.Order;
import com.study.orderservice.exception.BadRequestException;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Order order = new Order();
        orderMapper.updateOrderFromRequestDto(dto, order);
        Order savedOrder = orderService.create(order);
        OrderDto orderDto = orderMapper.toDto(savedOrder);
        UserDto userDto = orderService.getUserByEmail(order.getEmail(), request.getHeader(HttpHeaders.AUTHORIZATION));
        orderDto.setUser(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id, HttpServletRequest request){
        Order order = orderService.getOrderById(id);
        Boolean valid = orderService.validateUser(order.getUserId(), order.getEmail(), request.getHeader(HttpHeaders.AUTHORIZATION));
        if (valid == null || !valid){
            throw new BadRequestException("User not found");
        }
        OrderDto orderDto = orderMapper.toDto(order);
        UserDto userDto = orderService.getUserByEmail(order.getEmail(), request.getHeader(HttpHeaders.AUTHORIZATION));
        orderDto.setUser(userDto);
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderDto>> getOrdersByDatesAndStatus(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            @RequestParam String status,
            Pageable pageable,
            HttpServletRequest request
    ) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        Page<Order> orderPage = orderService.getOrders(from, to, status, pageable);

        Page<OrderDto> orderDtoPage = orderPage.map(order -> {
            UserDto userDto = orderService.getUserByEmail(order.getEmail(), header);
            OrderDto dto = orderMapper.toDto(order);
            dto.setUser(userDto);
            return dto;
        });

        PageResponse<OrderDto> response = new PageResponse<>();
        response.setContent(orderDtoPage.getContent());
        response.setPage(orderDtoPage.getNumber());
        response.setSize(orderDtoPage.getSize());
        response.setTotalElements(orderDtoPage.getTotalElements());
        response.setTotalPages(orderDtoPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/userid/{id}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Long id, HttpServletRequest request){
        List<Order> orderList = orderService.getOrdersByUserId(id);
        List<OrderDto> orderDtoList = new ArrayList<>(orderList.size());
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        for (Order order : orderList) {
            UserDto userDto = orderService.getUserByEmail(order.getEmail(), header);
            OrderDto orderDto = orderMapper.toDto(order);
            orderDto.setUser(userDto);
            orderDtoList.add(orderDto);
        }
        return ResponseEntity.ok(orderDtoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrderById(@PathVariable Long id, @RequestBody OrderRequestDto orderRequestDto, HttpServletRequest request){
        Order newOrder = new Order();
        newOrder = orderMapper.updateOrderFromRequestDto(orderRequestDto, newOrder);
        OrderDto orderDto = orderMapper.toDto(newOrder);
        Order order = orderService.updateOrderById(id, orderDto);
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        UserDto userDto = orderService.getUserByEmail(order.getEmail(), header);
        orderDto = orderMapper.toDto(order);
        orderDto.setUser(userDto);
        return ResponseEntity.ok(orderDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id){
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}
