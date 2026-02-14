package com.study.orderservice.service.impl;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.dto.UserDto;
import com.study.orderservice.entity.Order;
import com.study.orderservice.exception.OrderServiceException;
import com.study.orderservice.repository.OrderRepository;
import com.study.orderservice.service.OrderService;
import com.study.orderservice.specification.OrderSpecification;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClient = WebClient.builder() .baseUrl("http://localhost:8080") .build();


    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
    public void deleteOrderById(Long id){
        Order order = getOrderById(id);
        order.setDeleted(true);
        orderRepository.save(order);
    }

    public UserDto getUserByEmail(String email) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users/by-email")
                            .queryParam("email", email)
                            .build())
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (Exception ex) {
            System.out.println("WebClient failed: " + ex.getMessage());
            throw ex;
        }
    }



    @Override
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackValidateUser")
    public Boolean validateUser(Long userId, String email) {
        return webClient.get()
                .uri(uri -> uri.path("/users/validate")
                        .queryParam("userId", userId)
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)   // <-- теперь читаем boolean
                .block();
    }

    public Boolean fallbackValidateUser(Long userId, String email, Throwable t) {
        return false;
    }

    public UserDto fallbackGetUserByEmail(String email, Throwable t) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName("Unknown user");
        return dto;
    }
}
