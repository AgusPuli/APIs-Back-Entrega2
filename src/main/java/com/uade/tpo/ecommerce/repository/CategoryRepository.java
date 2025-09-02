package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Búsqueda por descripción (si la necesitás)
    @Query("SELECT c FROM Category c WHERE c.description = :description")
    List<Category> findByDescription(@Param("description") String description);
}