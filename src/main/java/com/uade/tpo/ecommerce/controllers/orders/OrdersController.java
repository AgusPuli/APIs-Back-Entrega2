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
public class OrdersController {

    @Autowired private OrderService service;
    @Autowired private UserRepository users;
    @Autowired private OrderMapper mapper;

    // ---- helper para sacar userId del token ----
    private Long currentUserId(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails ud)) {
            throw new IllegalArgumentException("No autenticado");
        }
        String username = ud.getUsername();
        User u = users.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return u.getId();
    }

    // Checkout desde el carrito del usuario autenticado
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponses.OrderDetailResponse checkout(Authentication auth) {
        Long userId = currentUserId(auth);
        Order o = service.createFromCart(userId);
        return mapper.toDetail(o);
    }

    // ---- lecturas y administración ----

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
