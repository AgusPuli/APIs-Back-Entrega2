package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Útiles si tu entidad tiene estos campos:
    boolean existsByNameIgnoreCase(String name);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    // Si tu Product mapea categoría como 'category', Spring Data entiende 'category.id'
    // Si tu campo se llama distinto, renombrar a findBy<CategoryField>Id(...)
}
