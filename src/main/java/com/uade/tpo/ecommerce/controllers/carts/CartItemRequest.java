package com.uade.tpo.ecommerce.controllers.carts;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private Long productId;
    private Integer quantity;
}

// EN JSON
//
// {
// "userId": 4
// "item":{"productId": "", "quantity":5}
//}