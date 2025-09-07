package com.uade.tpo.ecommerce.controllers.carts;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyDiscountRequest {
    private String code;
    private BigDecimal percentage;
}
