package com.uade.tpo.ecommerce.controllers.users;

public record UserResponseNameDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role
) {}
