package com.uade.tpo.ecommerce.exceptions.dto;

import lombok.Data;

@Data
public class CategoryRequest {
    private int id;
    private String name;
    private String description;
}
