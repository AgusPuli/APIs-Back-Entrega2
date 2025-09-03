package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.categories.CategoryRequest;
import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.entity.CategoryType;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import com.uade.tpo.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // esta clase va a tener la lógica
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Category createCategory(CategoryRequest req) throws CategoryDuplicateException {
        // Validar nombre (enum)
        CategoryType name = req.getName();
        if (name == null) {
            throw new IllegalArgumentException("El nombre (categoría) es obligatorio");
        }

        // Validar descripción
        String description = req.getDescription() == null ? null : req.getDescription().trim();
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }

        // Duplicados por enum
        if (categoryRepository.existsByName(name)) {
            throw new CategoryDuplicateException();
        }

        Category cat = new Category();
        cat.setName(name);
        cat.setDescription(description);

        try {
            return categoryRepository.save(cat);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // por si la categoría ya existe en DB
            throw new CategoryDuplicateException();
        }
    }
}
