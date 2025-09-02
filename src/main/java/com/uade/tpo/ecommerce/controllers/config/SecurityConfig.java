package com.uade.tpo.ecommerce.controllers.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS opcional: .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            // 401 prolijo cuando no hay token o está vencido
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Necesitás iniciar sesión.\"}");
                        })
                )
                .authorizeHttpRequests(req -> req
                        // === Público (auth & error & preflight) ===
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // === Público (lectura de catálogo) ===
                        .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").permitAll()

                        // === USER (carrito, pedidos, pagos) ===
                        .requestMatchers("/cart/**", "/orders/**", "/payments/**").hasAuthority("USER")
                        // Si querés que ADMIN también pueda:
                        // .requestMatchers("/cart/**", "/orders/**", "/payments/**").hasAnyAuthority("USER","ADMIN")

                        // === ADMIN (gestión de catálogo) ===
                        .requestMatchers(HttpMethod.POST,   "/products/**", "/categories/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/products/**", "/categories/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**", "/categories/**").hasAuthority("ADMIN")

                        // Resto: autenticado
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
