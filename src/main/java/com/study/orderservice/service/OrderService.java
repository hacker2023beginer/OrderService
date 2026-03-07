package com.study.orderservice.service;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.dto.UserDto;
import com.study.orderservice.entity.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

       Order create(Order order);

       Order getOrderById(Long id);

       Page<Order> getOrders(LocalDateTime from, LocalDateTime to, String status, Pageable pageable);

       List<Order> getOrdersByUserId(Long id);

       Order updateOrderById(Long id, OrderDto dto);

       void deleteOrderById(Long id);

        UserDto getUserByEmail(String email, String request);

       @CircuitBreaker(name = "userService", fallbackMethod = "fallbackValidateUser")
       Boolean validateUser(Long userId, String email, String authHeader);
}
