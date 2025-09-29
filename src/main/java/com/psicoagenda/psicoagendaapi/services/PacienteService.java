package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.PacienteRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteUpdateRequestDTO; // NOVO IMPORT
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.models.User;
import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.repository.PacienteRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import com.psicoagenda.psicoagendaapi.security.SecurityService; // NOVO IMPORT
import org.springframework.security.access.AccessDeniedException; // NOVO IMPORT
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PacienteService {


    private final PacienteRepository pacienteRepository;
    private final UserService userService;
    private final SecurityService securityService; // NOVO CAMPO INJETADO

    public PacienteService(PacienteRepository pacienteRepository, UserService userService, SecurityService securityService) {
        this.pacienteRepository = pacienteRepository;
        this.userService = userService;
        this.securityService = securityService; // INJEÇÃO
    }

    public Paciente save(PacienteRequestDTO pacienteDto) {
        User user = new User();
        user.setEmail(pacienteDto.getUser().getEmail());
        user.setPasswordHash(pacienteDto.getUser().getPassword());
        user.setRole(UserRole.PACIENTE);

        Paciente paciente = new Paciente();
        paciente.setNome(pacienteDto.getNome());
        paciente.setTelefone(pacienteDto.getTelefone());

        // O user é associado ao paciente antes de ser salvo
        // Mas a chave primária só existe após salvar o User
        User savedUser = userService.save(user);
        paciente.setUser(savedUser);

        return pacienteRepository.save(paciente);
    }

    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public PacienteResponseDTO toResponseDTO(Paciente paciente) {
        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(paciente.getId());
        dto.setNome(paciente.getNome());
        dto.setTelefone(paciente.getTelefone());
        dto.setEmail(paciente.getUser().getEmail());
        return dto;
    }

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Paciente findById(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com o ID " + id + " não encontrado."));
    }

    /**
     * Busca um paciente e autoriza a visualização baseada na role do usuário.
     * Permitido para Psicólogos ou para o próprio Paciente.
     */
    public Paciente findByIdAndAuthorize(Long id) {
        Paciente paciente = findById(id);

        String role = securityService.getAuthenticatedUserRoleString();
        Long userId = securityService.getAuthenticatedUserId();

        // Paciente só pode ver o seu perfil. Psicólogo pode ver qualquer.
        if (role.equals("ROLE_" + UserRole.PACIENTE.name()) && !paciente.getId().equals(userId)) {
            throw new AccessDeniedException("Você não tem permissão para visualizar o perfil deste paciente.");
        }
        return paciente;
    }

    /**
     * Atualiza um paciente e autoriza a operação baseada na role do usuário.
     * Permitido para Psicólogos ou para o próprio Paciente.
     */
    public Paciente updateAndAuthorize(Long id, PacienteUpdateRequestDTO pacienteDto) {
        Paciente pacienteExistente = findById(id);

        String role = securityService.getAuthenticatedUserRoleString();
        Long userId = securityService.getAuthenticatedUserId();

        // Paciente só pode atualizar o seu perfil. Psicólogo pode atualizar qualquer.
        if (role.equals("ROLE_" + UserRole.PACIENTE.name()) && !pacienteExistente.getId().equals(userId)) {
            throw new AccessDeniedException("Você não tem permissão para atualizar o perfil deste paciente.");
        }

        if (pacienteDto.getNome() != null) {
            pacienteExistente.setNome(pacienteDto.getNome());
        }
        if (pacienteDto.getTelefone() != null) {
            pacienteExistente.setTelefone(pacienteDto.getTelefone());
        }

        return pacienteRepository.save(pacienteExistente);
    }

    /**
     * Deleta um paciente e autoriza a operação baseada na role do usuário.
     * Permitido para Psicólogos ou para o próprio Paciente.
     */
    public void deleteAndAuthorize(Long id) {
        Paciente pacienteExistente = findById(id);

        String role = securityService.getAuthenticatedUserRoleString();
        Long userId = securityService.getAuthenticatedUserId();

        // Paciente só pode deletar o seu perfil. Psicólogo pode deletar qualquer.
        if (role.equals("ROLE_" + UserRole.PACIENTE.name()) && !pacienteExistente.getId().equals(userId)) {
            throw new AccessDeniedException("Você não tem permissão para deletar o perfil deste paciente.");
        }

        pacienteRepository.deleteById(id);
        userService.delete(id);
    }

    // Método original de deleção mantido, mas não usado pelo PacienteController refatorado
    public void delete(Long id) {
        pacienteRepository.deleteById(id);
        userService.delete(id);
    }
}