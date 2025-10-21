package com.uade.tpo.ecommerce.controllers.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            // 馃敀 Devolver 401 limpio cuando no hay token o est谩 vencido
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Necesit谩s iniciar sesi贸n.\"}");
                        })
                )
                .authorizeHttpRequests(req -> req

                        // 馃實 P煤blico
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 馃摝 P煤blico (lectura)
                        .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").permitAll()

                        // 馃洅 USER (carrito, pedidos, pagos)
                        .requestMatchers("/cart/**", "/orders/**", "/payments/**").hasAnyRole("USER", "ADMIN")

                        // 馃З ADMIN (gesti贸n cat谩logo)
                        .requestMatchers(HttpMethod.POST,   "/products/**", "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/products/**", "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**", "/categories/**").hasRole("ADMIN")

                        // 馃懁 ADMIN (gesti贸n de usuarios)
                        .requestMatchers(HttpMethod.POST, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                        // 馃懃 USER o ADMIN pueden leer sus datos personales
                        .requestMatchers(HttpMethod.GET, "/users/email/**").authenticated()

                        // 馃П Todo lo dem谩s requiere autenticaci贸n
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}