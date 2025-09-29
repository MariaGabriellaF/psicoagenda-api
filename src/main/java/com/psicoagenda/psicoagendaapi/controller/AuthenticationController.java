package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.UserRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.LoginResponseDTO; // Necessário criar este DTO
import com.psicoagenda.psicoagendaapi.security.JwtUtil;
import com.psicoagenda.psicoagendaapi.security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody UserRequestDTO authenticationRequest) { // Recebe email e senha [cite: 21]

        try {
            // Tenta autenticar o usuário. O AuthenticationManager usa o UserDetailsServiceImpl e o BCryptPasswordEncoder [cite: 22, 133]
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            // Falha: Credenciais inválidas [cite: 29]
            return ResponseEntity.status(401).body("Credenciais inválidas."); // Retorna 401 Unauthorized [cite: 31]
        }

        // Sucesso: credenciais válidas [cite: 24]
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());

        final String jwt = jwtUtil.generateToken(userDetails); // Gera o token [cite: 27]

        // Retorna o token [cite: 28]
        return ResponseEntity.ok(new LoginResponseDTO(jwt));
    }
}