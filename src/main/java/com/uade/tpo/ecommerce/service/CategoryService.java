package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;

import java.util.ArrayList;
import java.util.Optional;

public interface CategoryService {

    public ArrayList<Category> getCategories();

    public Optional<Category> getCategoryById(int categoryId);

    public Category createCategory(int newCategoryId, String name, String description) throws CategoryDuplicateException;

}
