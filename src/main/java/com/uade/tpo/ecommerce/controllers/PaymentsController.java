package com.uade.tpo.ecommerce.controllers;

import com.uade.tpo.ecommerce.entity.Payment;
import com.uade.tpo.ecommerce.entity.dto.PaymentRequest;
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
    public Payment pay(@RequestBody PaymentRequest request) {
        return service.pay(request);
    }
}
