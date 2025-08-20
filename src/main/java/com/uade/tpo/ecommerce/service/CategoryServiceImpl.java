package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
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

//    public Category createCategory(int newCategoryId, String name, String description) throws CategoryDuplicateException {
//        ArrayList<Category> categories = categoryRepository.getCategories();
//        if (categories.stream().anyMatch(
//                category -> category.getId() == newCategoryId && category.getDescription().equals(description) && category.getName().equals(name)))
//            throw new CategoryDuplicateException();
//        return categoryRepository.createCategory(newCategoryId, name, description);
//    }
}