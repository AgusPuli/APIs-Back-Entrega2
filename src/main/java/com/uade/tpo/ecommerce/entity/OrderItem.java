package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

// Clase que relaciona orden y productos.

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    //@Positive
    @Column(nullable = false)
    private double unitPrice;

    //@PositiveOrZero
    @Column(nullable = false)
    private double subtotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}
