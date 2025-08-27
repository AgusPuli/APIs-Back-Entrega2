package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.controllers.products.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    public Product create(ProductRequest request);

    public Product getProductById(Long id);

    public Page<Product> listProducts(Pageable pageable);

    public Product update(Long id, ProductRequest request);

    public void delete(Long id);

    public Page<Product> listByCategory(Long categoryId, Pageable pageable);
}
