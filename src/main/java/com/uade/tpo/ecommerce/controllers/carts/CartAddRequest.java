package com.uade.tpo.ecommerce.controllers.carts;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddRequest {
    private Long userId;
    private CartItemRequest item;
}
