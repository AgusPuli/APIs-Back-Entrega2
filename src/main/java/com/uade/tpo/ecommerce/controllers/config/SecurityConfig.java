package com.uade.tpo.ecommerce.controllers.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final com.uade.tpo.ecommerce.controllers.config.JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        // ==== Público (login y registro) ====
                        .requestMatchers("/auth/**").permitAll()

                        // ==== Público (lectura de catálogo) ====
                        .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").permitAll()

                        // ==== USER (carrito, pedidos, pagos) ====
                        .requestMatchers("/cart/**", "/orders/**", "/payments/**").hasRole("USER")

                        // ==== ADMIN (gestión de catálogo) ====
                        .requestMatchers(HttpMethod.POST,   "/products/**", "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/products/**", "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**", "/categories/**").hasRole("ADMIN")

                        // lo demas
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

