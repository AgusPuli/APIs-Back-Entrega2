// ProductMapper.java
package com.uade.tpo.ecommerce.controllers.products;

import com.uade.tpo.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product p) {
        if (p == null) return null;

        ProductResponse.CategoryDTO categoryDTO = null;
        if (p.getCategory() != null) {
            categoryDTO = ProductResponse.CategoryDTO.builder()
                    .name(p.getCategory().getName() != null ? p.getCategory().getName().name() : null)
                    .build();
        }

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .active(p.getActive())     // <- mapear el estado
                .category(categoryDTO)
                .build();
    }

    public Product toEntity(ProductRequest req) {
        // si ya lo tenías implementado, dejalo igual
        // este método no es necesario tocar para mostrar "active" en el response
        return null;
    }
}
