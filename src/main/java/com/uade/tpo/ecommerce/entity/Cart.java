package com.uade.tpo.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // timestamps
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    public void onCreate() { this.lastUpdatedAt = LocalDateTime.now(); }

    @PreUpdate
    public void onUpdate() { this.lastUpdatedAt = LocalDateTime.now(); }

//     EN MySQL Workbench:

//     SET GLOBAL event_scheduler = ON; -> CADA VEZ QUE INICIAMOS EL SQL, COMPILAMOS ESTO.

//    CREATE EVENT IF NOT EXISTS purge_old_cart_items
//    ON SCHEDULE EVERY 1 DAY
//            DO
//    DELETE ci
//    FROM cart_items ci
//    JOIN carts c ON ci.cart_id = c.id
//    WHERE c.last_updated_at <= NOW() - INTERVAL 7 DAY;
//
//    CREATE EVENT IF NOT EXISTS purge_old_carts
//    ON SCHEDULE EVERY 1 DAY
//            DO
//    DELETE FROM carts
//    WHERE last_updated_at <= NOW() - INTERVAL 7 DAY;
}
