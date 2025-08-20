package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;

import java.util.List;
import java.util.Optional;


public interface CategoryService {

    public List<Category> getCategories();

    public Optional<Category> getCategoryById(Long categoryId);

    public Category createCategory(String description) throws CategoryDuplicateException;

}
