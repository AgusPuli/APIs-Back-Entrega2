package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.auth.AuthenticationRequest;
import com.uade.tpo.ecommerce.controllers.auth.AuthenticationResponse;
import com.uade.tpo.ecommerce.controllers.auth.RegisterRequest;
import com.uade.tpo.ecommerce.controllers.config.JwtService;
import com.uade.tpo.ecommerce.entity.Role;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // 🔹 Registro de usuario normal (rol USER)
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // 👈 siempre USER
                .build();

        repository.save(user);
        var jwtToken = jwtService.generateTokenFromUsername(user.getEmail());
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    // 🔹 Registro de administrador (rol ADMIN)
    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN) // 👈 siempre ADMIN
                .build();

        repository.save(user);
        var jwtToken = jwtService.generateTokenFromUsername(user.getEmail());
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    // 🔹 Autenticación (login)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateTokenFromUsername(user.getEmail());
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
