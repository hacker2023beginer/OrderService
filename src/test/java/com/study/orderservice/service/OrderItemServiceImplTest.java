package com.study.orderservice.service;

import com.study.orderservice.dto.OrderItemDto;
import com.study.orderservice.entity.Item;
import com.study.orderservice.entity.Order;
import com.study.orderservice.entity.OrderItem;
import com.study.orderservice.exception.ItemServiceException;
import com.study.orderservice.exception.OrderItemServiceException;
import com.study.orderservice.exception.OrderServiceException;
import com.study.orderservice.repository.ItemRepository;
import com.study.orderservice.repository.OrderItemRepository;
import com.study.orderservice.repository.OrderRepository;
import com.study.orderservice.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Test
    void create_shouldSaveOrderItem() {
        OrderItem orderItem = new OrderItem();
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);

        OrderItem result = orderItemService.create(orderItem);

        assertThat(result).isSameAs(orderItem);
        verify(orderItemRepository).save(orderItem);
    }

    @Test
    void getOrderItemById_shouldReturn_whenExists() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));

        OrderItem result = orderItemService.getOrderItemById(1L);

        assertThat(result).isSameAs(orderItem);
        verify(orderItemRepository).findById(1L);
    }

    @Test
    void getOrderItemById_shouldThrow_whenNotFound() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.getOrderItemById(1L))
                .isInstanceOf(OrderItemServiceException.class)
                .hasMessage("OrderItem not found");
    }

    @Test
    void updateOrderItemById_shouldUpdateAndSave_whenAllExist() {
        OrderItem existing = new OrderItem();
        existing.setId(1L);

        Item item = new Item();
        item.setId(2L);

        Order order = new Order();
        order.setId(3L);

        OrderItemDto dto = new OrderItemDto();
        dto.setItemId(2L);
        dto.setOrderId(3L);
        dto.setQuantity(5L);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(orderRepository.findById(3L)).thenReturn(Optional.of(order));
        when(orderItemRepository.save(existing)).thenReturn(existing);

        OrderItem result = orderItemService.updateOrderItemById(1L, dto);

        assertThat(result.getItem()).isSameAs(item);
        assertThat(result.getOrder()).isSameAs(order);
        assertThat(result.getQuantity()).isEqualTo(5L);
        verify(orderItemRepository).save(existing);
    }

    @Test
    void updateOrderItemById_shouldThrow_whenOrderItemNotFound() {
        OrderItemDto dto = new OrderItemDto();
        dto.setItemId(2L);
        dto.setOrderId(3L);
        dto.setQuantity(5L);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.updateOrderItemById(1L, dto))
                .isInstanceOf(OrderItemServiceException.class)
                .hasMessage("OrderItem not found");
    }

    @Test
    void updateOrderItemById_shouldThrow_whenItemNotFound() {
        OrderItem existing = new OrderItem();
        existing.setId(1L);

        OrderItemDto dto = new OrderItemDto();
        dto.setItemId(2L);
        dto.setOrderId(3L);
        dto.setQuantity(5L);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.updateOrderItemById(1L, dto))
                .isInstanceOf(ItemServiceException.class)
                .hasMessage("Item not found");
    }

    @Test
    void updateOrderItemById_shouldThrow_whenOrderNotFound() {
        OrderItem existing = new OrderItem();
        existing.setId(1L);

        Item item = new Item();
        item.setId(2L);

        OrderItemDto dto = new OrderItemDto();
        dto.setItemId(2L);
        dto.setOrderId(3L);
        dto.setQuantity(5L);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(orderRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.updateOrderItemById(1L, dto))
                .isInstanceOf(OrderServiceException.class)
                .hasMessage("Order not found");
    }

    @Test
    void deleteOrderItemById_shouldCallRepository() {
        orderItemService.deleteOrderItemById(1L);

        verify(orderItemRepository).deleteById(1L);
    }
}

