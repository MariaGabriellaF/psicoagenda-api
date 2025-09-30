package com.psicoagenda.psicoagendaapi.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // O HandlerExceptionResolver é a ponte entre o Filter Chain (onde está o Spring Security)
    // e o DispatcherServlet (onde estão os @ExceptionHandler).
    private final HandlerExceptionResolver resolver;

    // Injetamos o resolver padrão do Spring, que é nomeado 'handlerExceptionResolver'.
    public CustomAccessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        // Em vez de manipular a resposta diretamente (como no AuthenticationEntryPoint),
        // delegamos a exceção AccessDeniedException de volta para o sistema MVC.
        // Isso força o acionamento do @ExceptionHandler(AccessDeniedException.class)
        // que está no GlobalExceptionHandler, retornando o ErrorResponse em JSON.
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}