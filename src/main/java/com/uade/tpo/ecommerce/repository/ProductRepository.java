package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.uade.tpo.ecommerce.entity.CategoryType;


public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCategory_Name(CategoryType name);

    List<Product> findByCategory_Name(CategoryType categoryType);}
