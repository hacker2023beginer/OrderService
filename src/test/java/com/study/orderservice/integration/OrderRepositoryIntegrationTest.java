package com.study.orderservice.integration;

import com.study.orderservice.entity.Order;
import com.study.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrderRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void saveAndFindOrder() {
        Order order = new Order();
        order.setUserId(1L);
        order.setEmail("test@mail.com");
        order.setStatus("NEW");
        order.setTotalPrice(100.0);

        Order saved = orderRepository.save(order);

        Optional<Order> found = orderRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@mail.com");
    }
}

