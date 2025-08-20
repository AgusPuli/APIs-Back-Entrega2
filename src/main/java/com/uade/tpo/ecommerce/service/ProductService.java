package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Product create(ProductRequest request);
    Product getProductById(Long id);
    Page<Product> listProducts(Pageable pageable);
    Product update(Long id, ProductRequest request);
    void delete(Long id);

    Page<Product> listByCategory(Long categoryId, Pageable pageable);
}
