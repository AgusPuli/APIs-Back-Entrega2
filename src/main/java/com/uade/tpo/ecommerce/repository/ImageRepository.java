package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByProductId(Long productId);
    void deleteByProductId(Long productId);
    boolean existsByProductId(Long productId);
}
