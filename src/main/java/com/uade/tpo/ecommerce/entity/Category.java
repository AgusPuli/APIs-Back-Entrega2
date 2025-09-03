package com.uade.tpo.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // para que no loopee
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // guarda el nombre del enum (APPLE, SAMSUNG, etc.)
    @Column(nullable = false, unique = true)
    private CategoryType name;

    @Column(nullable = false)
    private String description;

    public Category() {}

    public Category(CategoryType name, String description) {
        this.name = name;
        this.description = description;
    }
}
