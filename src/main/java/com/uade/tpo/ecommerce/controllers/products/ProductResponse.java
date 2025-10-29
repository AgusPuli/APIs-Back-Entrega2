// ProductResponse.java
package com.uade.tpo.ecommerce.controllers.products;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Boolean active;          // <- nuevo
    private CategoryDTO category;    // o lo que ya tengas

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDTO {
        private String name;
    }
}
