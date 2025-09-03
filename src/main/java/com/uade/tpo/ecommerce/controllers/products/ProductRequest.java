package com.uade.tpo.ecommerce.controllers.products;

import com.uade.tpo.ecommerce.entity.CategoryType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private CategoryType category;
}
