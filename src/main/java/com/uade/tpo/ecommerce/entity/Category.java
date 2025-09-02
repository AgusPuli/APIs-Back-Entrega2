package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "categories")

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;

    @Column(nullable = false, unique = true) // opcional: unique para que no se repitan
    private String name;

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
