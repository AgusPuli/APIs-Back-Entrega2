package com.uade.tpo.ecommerce.entity.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddRequest {
    private Long userId;
    private CartItemRequest item;
}
