package com.uade.tpo.ecommerce.controllers.categories;

import lombok.Data;

@Data
public class CategoryRequest {
    private Long id;
    private String name;
    private String description;
}
