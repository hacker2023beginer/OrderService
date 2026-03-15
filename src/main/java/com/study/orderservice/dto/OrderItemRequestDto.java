package com.study.orderservice.dto;

import jakarta.validation.constraints.PositiveOrZero;

public class OrderItemRequestDto {
    private Long orderId;

    private Long itemId;

    @PositiveOrZero
    private Long quantity;

    public OrderItemRequestDto(Long orderId, Long itemId, Long quantity) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
