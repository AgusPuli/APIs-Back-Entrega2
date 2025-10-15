package com.uade.tpo.ecommerce.controllers.payments;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderStatus;
import com.uade.tpo.ecommerce.entity.Payment;
import com.uade.tpo.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    @Autowired
    private PaymentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        Payment p = service.pay(request); // tu lógica actual
        Order o = p.getOrder();           // asumiendo relación Payment -> Order

        String method = p.getMethod();
        OrderStatus status = (o != null) ? o.getStatus() : null;
        Long orderId = (o != null) ? o.getId() : null;

        return new PaymentResponse(method, status, orderId);
    }
}
