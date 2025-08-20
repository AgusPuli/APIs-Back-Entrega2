package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


public class CategoryRepository {
    private ArrayList<Category> categories;

    public CategoryRepository() {
        categories = new ArrayList<>(Arrays.asList(
                Category.builder().id(1).name("Apple").description("iPhone y accesorios").build(),
                Category.builder().id(2).name("Samsung").description("Galaxy y accesorios").build(),
                Category.builder().id(3).name("Motorola").description("Moto y accesorios").build()
        ));
    }

    public ArrayList<Category> getCategories() {
        return this.categories;
    }

    public Optional<Category> getCategoryById(int categoryId) {
        return this.categories.stream().filter(m -> m.getId() == categoryId).findAny();
    }

    public Category createCategory(int id, String name, String description) {
        Category c = Category.builder()
                .id(id).name(name).description(description)
                .build();
        categories.add(c);
        return c;
    }

}
