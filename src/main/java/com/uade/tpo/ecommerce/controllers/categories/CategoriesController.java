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

    @Autowired private CategoryService categoryService;
    @Autowired private CategoryMapper mapper;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> body = categoryService.getCategories()
                .stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        Optional<Category> result = categoryService.getCategoryById(categoryId);
        if (result.isEmpty()) return ResponseEntity.notFound().build(); // 404 más claro que 204
        return ResponseEntity.ok(mapper.toResponse(result.get()));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest)
            throws CategoryDuplicateException {
        Category created = categoryService.createCategory(categoryRequest);
        CategoryResponse body = mapper.toResponse(created);
        return ResponseEntity.created(URI.create("/categories/" + created.getId())).body(body);
    }
}
