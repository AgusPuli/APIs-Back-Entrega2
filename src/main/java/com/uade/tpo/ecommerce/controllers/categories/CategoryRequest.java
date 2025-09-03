package com.uade.tpo.ecommerce.controllers.categories;

import com.uade.tpo.ecommerce.entity.CategoryType;
import lombok.Data;

@Data
public class CategoryRequest {

    private CategoryType name;
    private String description;

    public CategoryType getName() {
        return name;
    }

    public void setName(CategoryType name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryRequest(CategoryType name, String description) {
        this.name = name;
        this.description = description;
    }

}
