package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.payments.PaymentRequest;
import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderItem;
import com.uade.tpo.ecommerce.entity.OrderStatus;
import com.uade.tpo.ecommerce.entity.Payment;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.repository.OrderRepository;
import com.uade.tpo.ecommerce.repository.PaymentRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired private OrderRepository orders;
    @Autowired private PaymentRepository payments;
    @Autowired private ProductRepository products;

    @Override
    public Payment pay(PaymentRequest request) {
        // Trae la orden con items y productos
        Order order = orders.findByIdWithItemsAndProducts(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden inexistente"));

        if (order.getStatus() == OrderStatus.PAID)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden ya está pagada");
        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede pagar una orden cancelada");

        // Descontar stock (con lock pesimista)
        for (OrderItem item : order.getItems()) {
            Product prodInItem = item.getProduct();
            if (prodInItem == null || prodInItem.getId() == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Item sin producto asociado");
            }
            Long productId = prodInItem.getId();
            int qty = item.getQuantity();

            Product p = products.lockById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto " + productId + " inexistente"));

            if (Boolean.FALSE.equals(p.getActive())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Producto inactivo: " + p.getName());
            }
            if (p.getStock() < qty) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para " + p.getName() + " (disp " + p.getStock() + ", req " + qty + ")");
            }

            p.setStock(p.getStock() - qty);
            products.save(p);
        }

        // Registrar pago y marcar PAID
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotal())
                .method(request.getMethod())
                .build();

        order.setStatus(OrderStatus.PAID);
        orders.save(order);

        return payments.save(payment);
    }

    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        return payments.findByUserId(userId);
    }
}
