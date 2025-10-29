package com.uade.tpo.ecommerce.controllers.products;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uade.tpo.ecommerce.entity.CategoryType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // SOFT DELETE: Elimina lógicamente si hay órdenes, físicamente si no
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = service.deleteProduct(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public List<ProductResponse> getProductsByCategory(@PathVariable CategoryType category) {
        return service.findByCategory(category).stream()
                .map(mapper::toResponse)
                .toList();
    }

    // Nuevo: toggle de estado activo. Asi producto aparece no disponible y se puede setear como disponible
    @PatchMapping("/{id}/active")
    public ProductResponse setActive(
            @PathVariable Long id,
            @RequestParam("active") boolean active
    ) {
        Product updated = service.setActive(id, active);
        return mapper.toResponse(updated);
    }
}