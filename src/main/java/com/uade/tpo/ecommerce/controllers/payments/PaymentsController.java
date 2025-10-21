package com.uade.tpo.ecommerce.controllers.payments;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderStatus;
import com.uade.tpo.ecommerce.entity.Payment;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.service.PaymentService;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    @Autowired
    private PaymentService service;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        Payment p = service.pay(request);
        Order o = p.getOrder();

        String method = p.getMethod();
        OrderStatus status = (o != null) ? o.getStatus() : null;
        Long orderId = (o != null) ? o.getId() : null;

        return new PaymentResponse(method, status, orderId);
    }

    // 🔹 NUEVO ENDPOINT: Obtener pagos del usuario autenticado
    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Obtener email del usuario autenticado desde el JWT
            String email = userDetails.getUsername();

            // Buscar el usuario en la base de datos
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener todos los pagos del usuario
            List<Payment> payments = service.getPaymentsByUserId(user.getId());

            // Transformar a PaymentResponse
            List<PaymentResponse> response = payments.stream()
                    .map(payment -> {
                        Order order = payment.getOrder();
                        return new PaymentResponse(
                                payment.getMethod(),
                                order != null ? order.getStatus() : null,
                                order != null ? order.getId() : null
                        );
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}