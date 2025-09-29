package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeUpdateRequestDTO; // NOVO IMPORT
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.DiaSemana;
import com.psicoagenda.psicoagendaapi.repository.DisponibilidadeRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import com.psicoagenda.psicoagendaapi.security.SecurityService; // NOVO IMPORT
import org.springframework.security.access.AccessDeniedException; // NOVO IMPORT
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DisponibilidadeService {


    private final DisponibilidadeRepository disponibilidadeRepository;
    private final PsicologoService psicologoService;
    private final SecurityService securityService; // NOVO CAMPO INJETADO

    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository, PsicologoService psicologoService, SecurityService securityService) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.psicologoService = psicologoService;
        this.securityService = securityService; // INJEÇÃO
    }

    // Método original de save (usado internamente no saveAndAuthorize)
    public Disponibilidade save(DisponibilidadeRequestDTO disponibilidadeDto) {
        Psicologo psicologo = psicologoService.findById(disponibilidadeDto.getPsicologoId());

        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade.setPsicologo(psicologo);
        disponibilidade.setStartAt(disponibilidadeDto.getStartAt());
        disponibilidade.setEndAt(disponibilidadeDto.getEndAt());
        disponibilidade.setRecorrente(disponibilidadeDto.isRecorrente());
        disponibilidade.setDiaSemana(DiaSemana.valueOf(disponibilidadeDto.getDiaSemana()));

        return disponibilidadeRepository.save(disponibilidade);
    }

    /**
     * Cria uma nova disponibilidade após checar se o psicólogo ID corresponde ao usuário autenticado.
     */
    public Disponibilidade saveAndAuthorize(DisponibilidadeRequestDTO disponibilidadeDto) {
        Long authenticatedUserId = securityService.getAuthenticatedUserId();

        // A Disponibilidade deve ser para o próprio psicólogo autenticado.
        if (!disponibilidadeDto.getPsicologoId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Você só pode criar disponibilidades para o seu próprio perfil.");
        }

        return save(disponibilidadeDto);
    }

    public Disponibilidade save(Disponibilidade disponibilidade) {
        return disponibilidadeRepository.save(disponibilidade);
    }

    public DisponibilidadeResponseDTO toResponseDTO(Disponibilidade disponibilidade) {
        DisponibilidadeResponseDTO dto = new DisponibilidadeResponseDTO();
        dto.setId(disponibilidade.getId());
        dto.setPsicologoId(disponibilidade.getPsicologo().getId());
        dto.setPsicologoNome(disponibilidade.getPsicologo().getNome());
        dto.setStartAt(disponibilidade.getStartAt());
        dto.setEndAt(disponibilidade.getEndAt());
        dto.setRecorrente(disponibilidade.isRecorrente());
        dto.setDiaSemana(disponibilidade.getDiaSemana().toString());
        return dto;
    }

    public List<Disponibilidade> findAll() {
        return disponibilidadeRepository.findAll();
    }

    public Disponibilidade findById(Long id) {
        return disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade com o ID " + id + " não encontrada."));
    }

    /**
     * Atualiza uma disponibilidade após checar se ela pertence ao usuário autenticado.
     */
    public Disponibilidade updateAndAuthorize(Long id, DisponibilidadeUpdateRequestDTO disponibilidadeDto) {
        Disponibilidade disponibilidadeExistente = findById(id);

        // 1. Checagem de propriedade
        Long authenticatedUserId = securityService.getAuthenticatedUserId();
        if (!disponibilidadeExistente.getPsicologo().getId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Você só pode atualizar suas próprias disponibilidades.");
        }

        // 2. Aplicação do Patch
        if (disponibilidadeDto.getStartAt() != null) {
            disponibilidadeExistente.setStartAt(disponibilidadeDto.getStartAt());
        }
        if (disponibilidadeDto.getEndAt() != null) {
            disponibilidadeExistente.setEndAt(disponibilidadeDto.getEndAt());
        }
        if (disponibilidadeDto.getDiaSemana() != null) {
            disponibilidadeExistente.setDiaSemana(DiaSemana.valueOf(disponibilidadeDto.getDiaSemana()));
        }
        // Usa o wrapper Boolean, que permite a checagem de null
        if (disponibilidadeDto.getRecorrente() != null) {
            disponibilidadeExistente.setRecorrente(disponibilidadeDto.getRecorrente());
        }

        return disponibilidadeRepository.save(disponibilidadeExistente);
    }

    /**
     * Deleta uma disponibilidade após checar se ela pertence ao usuário autenticado.
     */
    public void deleteAndAuthorize(Long id) {
        Disponibilidade disponibilidadeExistente = findById(id);

        // Checagem de propriedade
        Long authenticatedUserId = securityService.getAuthenticatedUserId();
        if (!disponibilidadeExistente.getPsicologo().getId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Você só pode deletar suas próprias disponibilidades.");
        }

        disponibilidadeRepository.deleteById(id);
    }

    public void delete(Long id) {
        disponibilidadeRepository.deleteById(id);
    }
}