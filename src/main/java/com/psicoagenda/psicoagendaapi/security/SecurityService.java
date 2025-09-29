package com.psicoagenda.psicoagendaapi.security;

import com.psicoagenda.psicoagendaapi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retorna o ID (PK) do User logado.
     * O 'username' no Spring Security é o email, que é o campo único na entidade User.
     */
    public Long getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            // Busca o User no banco pelo email para obter o ID (a chave primária)
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado na base de dados."))
                    .getId();
        }
        throw new IllegalStateException("Usuário não autenticado ou Principal inválido.");
    }
}