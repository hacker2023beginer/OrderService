package com.study.orderservice.service;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.entity.Order;
import com.study.orderservice.exception.OrderServiceException;
import com.study.orderservice.repository.OrderRepository;
import com.study.orderservice.specification.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

       Order create(Order order);

       Order getOrderById(Long id);

       Page<Order> getOrders(LocalDateTime from, LocalDateTime to, String status, Pageable pageable);

       List<Order> getOrdersByUserId(Long id);

       Order updateOrderById(Long id, OrderDto dto);

       void deleteOrderById(Long id);
}
