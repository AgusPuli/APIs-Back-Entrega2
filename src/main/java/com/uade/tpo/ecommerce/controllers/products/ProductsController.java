package com.uade.tpo.ecommerce.controllers.products;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.uade.tpo.ecommerce.entity.CategoryType;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody ProductRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    @GetMapping
    public Page<Product> list(Pageable pageable) {
        return service.listProducts(pageable);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable CategoryType category) {
        return service.findByCategory(category);
    }
}
