package com.study.orderservice.service;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.entity.Order;
import com.study.orderservice.exception.OrderServiceException;
import com.study.orderservice.repository.OrderRepository;
import com.study.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void create_shouldSaveOrder() {
        Order order = new Order();
        order.setUserId(1L);

        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.create(order);

        assertThat(result).isSameAs(order);
        verify(orderRepository).save(order);
    }

    @Test
    void getOrderById_shouldReturn_whenExistsAndNotDeleted() {
        Order order = new Order();
        order.setId(1L);
        order.setDeleted(false);

        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertThat(result).isSameAs(order);
        verify(orderRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    void getOrderById_shouldThrow_whenNotFound() {
        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L))
                .isInstanceOf(OrderServiceException.class)
                .hasMessage("Order not found");
    }

    @Test
    void getOrders_shouldCallRepositoryWithSpecification() {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        String status = "NEW";
        Pageable pageable = PageRequest.of(0, 10);

        Order order = new Order();
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Order> result = orderService.getOrders(from, to, status, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getOrdersByUserId_shouldReturnList() {
        Order order = new Order();
        order.setUserId(1L);

        when(orderRepository.findByUserIdAndDeletedFalse(1L))
                .thenReturn(List.of(order));

        List<Order> result = orderService.getOrdersByUserId(1L);

        assertThat(result).hasSize(1);
        verify(orderRepository).findByUserIdAndDeletedFalse(1L);
    }

    @Test
    void updateOrderById_shouldUpdateAndSave_whenExists() {
        Order existing = new Order();
        existing.setId(1L);
        existing.setStatus("OLD");
        existing.setTotalPrice(10.0);
        existing.setUserId(1L);

        OrderDto dto = new OrderDto();
        dto.setStatus("NEW");
        dto.setTotalPrice(20.0);
        dto.setUserId(2L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);

        Order result = orderService.updateOrderById(1L, dto);

        assertThat(result.getStatus()).isEqualTo("NEW");
        assertThat(result.getTotalPrice()).isEqualTo(20.0);
        assertThat(result.getUserId()).isEqualTo(2L);
        verify(orderRepository).save(existing);
    }

    @Test
    void updateOrderById_shouldThrow_whenNotFound() {
        OrderDto dto = new OrderDto();
        dto.setStatus("NEW");
        dto.setTotalPrice(20.0);
        dto.setUserId(2L);

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderById(1L, dto))
                .isInstanceOf(OrderServiceException.class)
                .hasMessage("Order does not exist");
    }

    @Test
    void deleteOrderById_shouldSetDeletedTrueAndSave() {
        Order order = new Order();
        order.setId(1L);
        order.setDeleted(false);

        when(orderRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.deleteOrderById(1L);

        assertThat(order.isDeleted()).isTrue();
        verify(orderRepository).save(order);
    }
}

