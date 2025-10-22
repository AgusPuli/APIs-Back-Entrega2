package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.controllers.products.ProductRequest;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductService {

    Product create(ProductRequest request);

    Product getProductById(Long id);

    Page<Product> listProducts(Pageable pageable);

    Product update(Long id, ProductRequest request);

    // ✅ NUEVO: Método para soft delete inteligente
    Map<String, Object> deleteProduct(Long id);

    List<Product> findByCategory(CategoryType category);
}