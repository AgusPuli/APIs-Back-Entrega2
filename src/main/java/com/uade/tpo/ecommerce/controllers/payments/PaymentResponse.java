package com.uade.tpo.ecommerce.controllers.payments;

import com.uade.tpo.ecommerce.entity.OrderStatus;

public class PaymentResponse {
    private String method;
    private OrderStatus orderStatus;
    private Long orderId;

    public PaymentResponse(String method, OrderStatus orderStatus, Long orderId) {
        this.method = method;
        this.orderStatus = orderStatus;
        this.orderId = orderId;
    }

    // Getters y Setters
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}