package com.uade.tpo.ecommerce.exceptions;

public class ProductDuplicateException extends RuntimeException {
    public ProductDuplicateException(String name) {
        super("Ya existe un producto con name=" + name);
    }
}
