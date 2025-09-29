package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.PsicologoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoUpdateRequestDTO; // NOVO IMPORT
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.User;
import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.repository.PsicologoRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import com.psicoagenda.psicoagendaapi.security.SecurityService; // NOVO IMPORT
import org.springframework.security.access.AccessDeniedException; // NOVO IMPORT
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PsicologoService {


    private final PsicologoRepository psicologoRepository;
    private final UserService userService;
    private final SecurityService securityService; // NOVO CAMPO INJETADO

    public PsicologoService(PsicologoRepository psicologoRepository, UserService userService, SecurityService securityService) {
        this.psicologoRepository = psicologoRepository;
        this.userService = userService;
        this.securityService = securityService; // INJEÇÃO
    }

    public Psicologo save(PsicologoRequestDTO psicologoDto) {
        User user = new User();
        user.setEmail(psicologoDto.getUser().getEmail());
        user.setPasswordHash(psicologoDto.getUser().getPassword());
        user.setRole(UserRole.PSICOLOGO);

        Psicologo psicologo = new Psicologo();
        psicologo.setNome(psicologoDto.getNome());
        psicologo.setEspecialidade(psicologoDto.getEspecialidade());
        psicologo.setCrp(psicologoDto.getCrp());
        psicologo.setTeleatendimento(psicologoDto.isTeleatendimento());

        User savedUser = userService.save(user);
        psicologo.setUser(savedUser);

        return psicologoRepository.save(psicologo);
    }

    public List<Psicologo> findByNome(String nome) {
        return psicologoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public PsicologoResponseDTO toResponseDTO(Psicologo psicologo) {
        PsicologoResponseDTO dto = new PsicologoResponseDTO();
        dto.setId(psicologo.getId());
        dto.setNome(psicologo.getNome());
        dto.setEspecialidade(psicologo.getEspecialidade());
        dto.setCrp(psicologo.getCrp());
        dto.setTeleatendimento(psicologo.isTeleatendimento());
        dto.setEmail(psicologo.getUser().getEmail());
        return dto;
    }

    public List<Psicologo> findAll() {
        return psicologoRepository.findAll();
    }

    public Psicologo findById(Long id) {
        return psicologoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Psicologo com o ID " + id + " não encontrado."));
    }

    /**
     * Atualiza um psicólogo e checa se o ID do recurso é o mesmo do usuário autenticado.
     */
    public Psicologo updateAndAuthorize(Long id, PsicologoUpdateRequestDTO psicologoDto) {
        Psicologo psicologoExistente = findById(id);

        Long userId = securityService.getAuthenticatedUserId();

        // APENAS o seu próprio ID
        if (!psicologoExistente.getId().equals(userId)) {
            throw new AccessDeniedException("Um psicólogo só pode atualizar seu próprio perfil.");
        }

        if (psicologoDto.getNome() != null) {
            psicologoExistente.setNome(psicologoDto.getNome());
        }
        if (psicologoDto.getEspecialidade() != null) {
            psicologoExistente.setEspecialidade(psicologoDto.getEspecialidade());
        }
        if (psicologoDto.getCrp() != null) {
            psicologoExistente.setCrp(psicologoDto.getCrp());
        }
        // Usa o wrapper Boolean, que permite a checagem de null
        if (psicologoDto.getTeleatendimento() != null) {
            psicologoExistente.setTeleatendimento(psicologoDto.getTeleatendimento());
        }

        return psicologoRepository.save(psicologoExistente);
    }
    public void deleteAndAuthorize(Long id) {
        Psicologo psicologoExistente = findById(id);

        Long userId = securityService.getAuthenticatedUserId();

        // APENAS o seu próprio ID
        if (!psicologoExistente.getId().equals(userId)) {
            throw new AccessDeniedException("Um psicólogo só pode deletar seu próprio perfil.");
        }

        psicologoRepository.deleteById(id);
        userService.delete(id);
    }

    public void delete(Long id) {
        psicologoRepository.deleteById(id);
        userService.delete(id);
    }
}