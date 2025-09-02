package com.uade.tpo.ecommerce.controllers.categories;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;
import com.uade.tpo.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        Optional<Category> result = categoryService.getCategoryById(categoryId);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build(); // lo dejo como lo tenías
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody CategoryRequest categoryRequest)
            throws CategoryDuplicateException {

        // CAMBIO MINIMO: ahora paso el DTO completo (name + description)
        Category result = categoryService.createCategory(categoryRequest);

        return ResponseEntity.created(URI.create("/categories/" + result.getId())).body(result);
    }
}
