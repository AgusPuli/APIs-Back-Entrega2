package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("""
        select o from Order o
        left join fetch o.items i
        left join fetch i.product
        where o.id = :id
    """)
    Optional<Order> findByIdWithItemsAndProducts(@Param("id") Long id);
}
