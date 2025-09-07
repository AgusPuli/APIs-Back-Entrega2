package com.uade.tpo.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts")

public class Cart{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cart-items")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CartItem> items;

    // Para los cupones de descuento
    @Column(name = "discount_code", length = 64)
    private String discountCode;

    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;
}
