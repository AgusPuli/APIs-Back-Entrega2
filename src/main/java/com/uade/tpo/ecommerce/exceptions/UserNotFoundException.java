package com.uade.tpo.ecommerce.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("No se encontró el usuario con id=" + id);
    }
}
