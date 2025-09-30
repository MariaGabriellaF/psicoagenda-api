package com.psicoagenda.psicoagendaapi.security;

import com.fasterxml.jackson.databind.ObjectMapper; // Adicione este import
import com.psicoagenda.psicoagendaapi.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // 1. DECLARE O CAMPO

    // 2. INJETE O OBJECTMAPPER PELO CONSTRUTOR
    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                List.of("Acesso negado. Token de autenticação ausente, inválido ou expirado.")
        );

        // 3. USE O OBJECTMAPPER INJETADO
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}