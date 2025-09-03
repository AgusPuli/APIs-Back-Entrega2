package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.products.ProductRequest;
import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.entity.CategoryType;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product create(ProductRequest request) {
        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoría no encontrada: " + request.getCategory()
                ));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();

        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
    }

    @Override
    public Page<Product> listProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product update(Long id, ProductRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));

        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoría no encontrada: " + request.getCategory()
                ));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setCategory(category);

        return productRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> findByCategory(CategoryType categoryType) {
        return productRepository.findByCategory_Name(categoryType);
    }
}
