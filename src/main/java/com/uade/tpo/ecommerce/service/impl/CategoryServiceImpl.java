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
    public Category createCategory(CategoryRequest request) throws CategoryDuplicateException {

        List<Category> categories = categoryRepository.findByDescription(request.getDescription());
        if (categories.isEmpty()) {
            categoryRepository.save(new Category(request.getName(), request.getDescription()));

//        Category entity = Category.builder()
//                .name(request.getName())
//                .description(request.getDescription())
//                .build();
        }
        throw new CategoryDuplicateException();
    }

}