package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.entity.*;
import com.uade.tpo.ecommerce.controllers.payments.PaymentRequest;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.repository.*;
import com.uade.tpo.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository payments;
    @Autowired
    private OrderRepository orders;

    @Override
    public Payment pay(PaymentRequest request) {
        Order order = orders.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("El pedido ya está pagado");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(order.getTotal()) < 0) {
            throw new IllegalArgumentException("Monto insuficiente");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount().setScale(2))
                .method(request.getMethod())
                .build();

        order.setStatus(OrderStatus.PAID);
        orders.save(order);

        return payments.save(payment);
    }

    // 🔹 NUEVO: Implementación del método
    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        return payments.findByUserId(userId);
    }
}