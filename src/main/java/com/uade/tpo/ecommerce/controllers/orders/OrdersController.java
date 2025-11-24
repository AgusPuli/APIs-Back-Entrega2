package com.uade.tpo.ecommerce.controllers.orders;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderStatus;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.repository.UserRepository;
import com.uade.tpo.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrdersController {

    @Autowired private OrderService service;
    @Autowired private UserRepository users;
    @Autowired private OrderMapper mapper;

    // ---- helper para sacar el email del token ----
    private String currentUserEmail(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails ud)) {
            throw new IllegalArgumentException("No autenticado");
        }
        return ud.getUsername();
    }

    // ✅ UPDATED CHECKOUT METHOD
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponses.OrderDetailResponse checkout(
            Authentication auth,
            @RequestBody CheckoutRequest request // 👈 Recibe el JSON con los items
    ) {
        String email = currentUserEmail(auth);

        // 🛑 Calls the NEW service method that uses the payload
        Order o = service.createOrderFromPayload(email, request);

        return mapper.toDetail(o);
    }

    // ---- lecturas y administración (No changes needed below) ----

    @GetMapping("/{id}")
    public OrderResponses.OrderDetailResponse get(@PathVariable Long id) {
        return mapper.toDetail(service.getOrderById(id));
    }

    @GetMapping
    public Page<OrderResponses.OrderSummaryResponse> list(Pageable pageable) {
        return service.listOrders(pageable).map(mapper::toSummary);
    }

    @GetMapping("/by-user/{userId}")
    public Page<OrderResponses.OrderSummaryResponse> byUser(@PathVariable Long userId, Pageable pageable) {
        return service.listByUser(userId, pageable).map(mapper::toSummary);
    }

    @PutMapping("/{id}/status")
    public OrderResponses.OrderSummaryResponse setStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return mapper.toSummary(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}