package com.study.orderservice.exception;

public class OrderServiceException extends RuntimeException{
    public OrderServiceException(String message) {
        super(message);
    }
}
