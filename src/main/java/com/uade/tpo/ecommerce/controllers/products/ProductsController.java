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

    @Autowired private ProductService service;
    @Autowired private ProductMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody ProductRequest request) {
        Product p = service.create(request);
        return mapper.toResponse(p);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return mapper.toResponse(service.getProductById(id));
    }

    @GetMapping
    public Page<ProductResponse> list(Pageable pageable) {
        return service.listProducts(pageable).map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody ProductRequest request) {
        Product p = service.update(id, request);
        return mapper.toResponse(p);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/category/{category}")
    public List<ProductResponse> getProductsByCategory(@PathVariable CategoryType category) {
        return service.findByCategory(category).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
