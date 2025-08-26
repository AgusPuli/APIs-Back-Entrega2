package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.dto.ProductRequest;
import com.uade.tpo.ecommerce.exceptions.ProductDuplicateException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository products;

    @Autowired
    private CategoryRepository categories;

    @Override
    public Product create(ProductRequest request) {
        String name = safe(request.getName());

        // Duplicado por nombre (opcional)
        if (name != null && products.existsByNameIgnoreCase(name)) {
            throw new ProductDuplicateException(name);
        }

        if (request.getCategoryId() == null) {
            // si tu @JoinColumn es nullable = false, la categoría es obligatoria
            throw new IllegalArgumentException("CategoryId es requerido");
        }

        Category category = categories.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category inexistente id=" + request.getCategoryId()));

        Product p = Product.builder()
                .name(name)
                .description(safe(request.getDescription()))
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)  // ya que es obligatorio
                .build();

        return products.save(p);
    }


    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> listProducts(Pageable pageable) {
        return products.findAll(pageable);
    }

    @Override
    public Product update(Long id, ProductRequest request) {
        Product p = products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        String newName = safe(request.getName());
        if (newName != null && !newName.equalsIgnoreCase(p.getName())
                && products.existsByNameIgnoreCase(newName)) {
            throw new ProductDuplicateException(newName);
        }

        p.setName(newName != null ? newName : p.getName());
        p.setDescription(safeOr(p.getDescription(), request.getDescription()));
        if (request.getPrice() != null) p.setPrice(request.getPrice());
        if (request.getStock() != null) p.setStock(request.getStock());

        if (request.getCategoryId() != null) {
            Category category = categories.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Category inexistente id=" + request.getCategoryId()));
            p.setCategory(category);
        }

        return products.save(p);
    }

    @Override
    public void delete(Long id) {
        Product p = products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        products.delete(p);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> listByCategory(Long categoryId, Pageable pageable) {
        return products.findByCategoryId(categoryId, pageable);
    }

    private String safe(String s) { return s == null ? null : s.trim(); }
    private String safeOr(String current, String incoming) {
        return incoming == null ? current : safe(incoming);
    }
}
