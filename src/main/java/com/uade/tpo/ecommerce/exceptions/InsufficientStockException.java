package com.uade.tpo.ecommerce.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId) {
        super("Stock insuficiente para productId=" + productId);
    }
}
