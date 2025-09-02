package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.categories.CategoryRequest;
import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import com.uade.tpo.ecommerce.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service // esta clase va a tener la logica.
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Category createCategory(CategoryRequest req) throws CategoryDuplicateException {
        String name = req.getName() == null ? null : req.getName().trim();
        String description = req.getDescription() == null ? null : req.getDescription().trim();

        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (description == null || description.isEmpty())
            throw new IllegalArgumentException("La descripción es obligatoria");

        // validacion x nombre
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new CategoryDuplicateException();
        }

        Category cat = new Category();
        cat.setName(name);
        cat.setDescription(description);
        try {
            return categoryRepository.save(cat);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // por si la carrera llega a la DB primero
            throw new CategoryDuplicateException();
        }
    }

}