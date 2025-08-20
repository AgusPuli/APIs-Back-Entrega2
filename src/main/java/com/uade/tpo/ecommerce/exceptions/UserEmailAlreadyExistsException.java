package com.uade.tpo.ecommerce.exceptions;

public class UserEmailAlreadyExistsException extends RuntimeException {
    public UserEmailAlreadyExistsException(String email) {
        super("Ya existe un usuario con email=" + email);
    }
}