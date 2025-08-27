package com.uade.tpo.ecommerce.controllers.orders;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderStatus;
import com.uade.tpo.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody OrderRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) {
        return service.getOrderById(id);
    }

    @GetMapping
    public Page<Order> list(Pageable pageable) {
        return service.listOrders(pageable);
    }

    @GetMapping("/by-user/{userId}")
    public Page<Order> byUser(@PathVariable Long userId, Pageable pageable) {
        return service.listByUser(userId, pageable);
    }

    @PutMapping("/{id}/status")
    public Order setStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
