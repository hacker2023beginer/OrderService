package com.study.orderservice.exception;

public class ItemServiceException extends RuntimeException{
    public ItemServiceException(String message) {
        super(message);
    }
}
