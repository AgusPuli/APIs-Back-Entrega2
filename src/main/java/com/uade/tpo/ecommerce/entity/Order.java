package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "items_count", nullable = false)
    private Long itemsCount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
