package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(CategoryType name);
    Optional<Category> findByName(CategoryType name);

}