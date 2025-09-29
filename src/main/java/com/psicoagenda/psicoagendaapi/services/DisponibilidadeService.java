package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.DiaSemana;
import com.psicoagenda.psicoagendaapi.repository.DisponibilidadeRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import com.psicoagenda.psicoagendaapi.security.SecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DisponibilidadeService {


    private final DisponibilidadeRepository disponibilidadeRepository;
    private final PsicologoService psicologoService;
    private final SecurityService securityService;

    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository, PsicologoService psicologoService, SecurityService securityService) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.psicologoService = psicologoService;
        this.securityService = securityService;
    }

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

    public Disponibilidade saveAndAuthorize(DisponibilidadeRequestDTO disponibilidadeDto) {
        Long authenticatedUserId = securityService.getAuthenticatedUserId();
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

    public Disponibilidade updateAndAuthorize(Long id, DisponibilidadeUpdateRequestDTO disponibilidadeDto) {
        Disponibilidade disponibilidadeExistente = findById(id);

        Long authenticatedUserId = securityService.getAuthenticatedUserId();
        if (!disponibilidadeExistente.getPsicologo().getId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Você só pode atualizar suas próprias disponibilidades.");
        }
        if (disponibilidadeDto.getStartAt() != null) {
            disponibilidadeExistente.setStartAt(disponibilidadeDto.getStartAt());
        }
        if (disponibilidadeDto.getEndAt() != null) {
            disponibilidadeExistente.setEndAt(disponibilidadeDto.getEndAt());
        }
        if (disponibilidadeDto.getDiaSemana() != null) {
            disponibilidadeExistente.setDiaSemana(DiaSemana.valueOf(disponibilidadeDto.getDiaSemana()));
        }

        if (disponibilidadeDto.getRecorrente() != null) {
            disponibilidadeExistente.setRecorrente(disponibilidadeDto.getRecorrente());
        }

        return disponibilidadeRepository.save(disponibilidadeExistente);
    }

    public void deleteAndAuthorize(Long id) {
        Disponibilidade disponibilidadeExistente = findById(id);

        Long authenticatedUserId = securityService.getAuthenticatedUserId();
        if (!disponibilidadeExistente.getPsicologo().getId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Você só pode deletar suas próprias disponibilidades.");
        }
        disponibilidadeRepository.deleteById(id);
    }

    public void delete(Long id) {
        disponibilidadeRepository.deleteById(id);
    }

    public List<Disponibilidade> findByPsicologoId(Long psicologoId) {
        psicologoService.findById(psicologoId);
        return disponibilidadeRepository.findByPsicologoId(psicologoId);
    }
}