package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
//@Table(name: "Orders") -> cambia el nombre de la tabla de sql
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long count;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
