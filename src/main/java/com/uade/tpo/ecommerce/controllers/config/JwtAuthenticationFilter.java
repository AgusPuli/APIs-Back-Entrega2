package com.uade.tpo.ecommerce.controllers.config;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1) Endpoints públicos y preflight: no procesar JWT
        String path = request.getServletPath();
        if (path.startsWith("/auth/") ||
                path.startsWith("/error") ||
                "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Tomar header Authorization con logs
        final String rawAuth = request.getHeader("Authorization");
        if (rawAuth == null) {
            System.out.println("[JWT] Authorization header: <null>");
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("[JWT] Authorization header: " + rawAuth);

        // tolerante a mayúsc/minúsc y espacios
        final String lower = rawAuth.toLowerCase().trim();
        if (!lower.startsWith("bearer ")) {
            System.out.println("[JWT] Header NO empieza con 'Bearer ' (insensible a mayúsc).");
            filterChain.doFilter(request, response);
            return;
        }

        // cortar el prefijo y limpiar
        final String jwt = rawAuth.substring(rawAuth.indexOf(' ') + 1).trim();
        if (jwt.isEmpty()) {
            System.out.println("[JWT] Token vacío luego de 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3) Extraer subject (email) del JWT
            final String userEmail = jwtService.extractUsername(jwt);
            System.out.println("[DBG] token.sub = " + userEmail);
            System.out.println("[DBG] token.exp = " + jwtService.extractClaim(jwt, io.jsonwebtoken.Claims::getExpiration));

            // 4) Si no hay auth en contexto, validamos el token y seteamos SecurityContext
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // cargar detalles del usuario
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                System.out.println("[DBG] userDetails.getUsername() = " + userDetails.getUsername());

                // ✅ Extraer el rol directamente del token
                String role = jwtService.extractRole(jwt);
                System.out.println("[DBG] token.role = " + role);

                // ✅ Crear autoridad según el rol del token (con prefijo ROLE_)
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    authorities // 👈 usamos las autoridades derivadas del token
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[AUTH] Autenticado como " + role);
                } else {
                    unauthorized(response, "invalid_token", "Token inválido.");
                    return;
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "token_expired", "Tu sesión venció. Volvé a iniciar sesión.");
        } catch (JwtException ex) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "invalid_token", "Token inválido.");
        }
    }

    private void unauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }
}
