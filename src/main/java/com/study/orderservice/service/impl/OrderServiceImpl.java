package com.study.orderservice.service.impl;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.entity.Order;
import com.study.orderservice.exception.OrderServiceException;
import com.study.orderservice.repository.OrderRepository;
import com.study.orderservice.service.OrderService;
import com.study.orderservice.specification.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(Order order){
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id){
        return orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new OrderServiceException("Order not found"));
    }

    @Override
    public Page<Order> getOrders(LocalDateTime from, LocalDateTime to, String status, Pageable pageable) {
        Specification<Order> spec = Specification.allOf(
                    OrderSpecification.notDeleted())
                .and(OrderSpecification.createdAfter(from))
                .and(OrderSpecification.createdBefore(to))
                .and(OrderSpecification.hasStatus(status));

        return orderRepository.findAll(spec, pageable);
    }

    @Override
    public List<Order> getOrdersByUserId(Long id){
        return orderRepository.findByUserIdAndDeletedFalse(id);
    }

    @Override
    public Order updateOrderById(Long id, OrderDto dto){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderServiceException("Order does not exist"));

        order.setStatus(dto.getStatus());
        order.setTotalPrice(dto.getTotalPrice());
        order.setUserId(dto.getUserId());
        orderRepository.save(order);
        return order;
    }

    @Override
    public void deleteOrderById(Long id){
        Order order = getOrderById(id);
        order.setDeleted(true);
        orderRepository.save(order);
    }

}
