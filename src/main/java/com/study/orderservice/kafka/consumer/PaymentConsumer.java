package com.study.orderservice.kafka.consumer;

import com.study.orderservice.dto.OrderDto;
import com.study.orderservice.kafka.event.PaymentEvent;
import com.study.orderservice.mapper.OrderMapper;
import com.study.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentConsumer {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    public void consume(PaymentEvent event) {

        log.info("Received payment event: {}", event);

        Long orderId = Long.valueOf(event.getOrderId());
        OrderDto orderDto = orderMapper.toDto(orderService.getOrderById(orderId));
        orderDto.setStatus(event.getStatus());
        orderService.updateOrderById(orderId, orderDto);

        log.info("Order " + event.getOrderId() + " is set status " + event.getStatus());
    }
}
