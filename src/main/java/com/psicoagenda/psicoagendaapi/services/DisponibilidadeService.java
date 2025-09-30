package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.exception.InvalidDateRangeException;
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.DiaSemana;
import com.psicoagenda.psicoagendaapi.repository.DisponibilidadeRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import com.psicoagenda.psicoagendaapi.security.SecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
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


    public DisponibilidadeResponseDTO toResponseDTO(Disponibilidade disponibilidade) {
        DisponibilidadeResponseDTO dto = new DisponibilidadeResponseDTO();
        dto.setId(disponibilidade.getId());

        Psicologo psicologo = disponibilidade.getPsicologo(); // OBTÉM A REFERÊNCIA

        // VERIFICA SE O PSICOLOGO FOI CARREGADO (NÃO DELETADO)
        if (psicologo != null) {
            dto.setPsicologoId(psicologo.getId());
            dto.setPsicologoNome(psicologo.getNome());
        } else {
            // Caso em que o Psicólogo associado foi soft deletado (NULL no EAGER fetch)
            dto.setPsicologoId(null);
            dto.setPsicologoNome("Psicólogo (Deletado/Ausente)");
        }

        dto.setStartAt(disponibilidade.getStartAt());
        dto.setEndAt(disponibilidade.getEndAt());
        dto.setRecorrente(disponibilidade.isRecorrente());
        // Trata o caso do DiaSemana, apenas por segurança
        dto.setDiaSemana(disponibilidade.getDiaSemana() != null ? disponibilidade.getDiaSemana().toString() : null);
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

    public List<Disponibilidade> findByPsicologoId(Long psicologoId) {
        psicologoService.findById(psicologoId);
        return disponibilidadeRepository.findByPsicologoId(psicologoId);
    }

    public void validateAvailability(Long psicologoId, LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt.isBefore(LocalDateTime.now())) {
            throw new InvalidDateRangeException("Não é possível agendar consultas para o passado.");
        }
        DayOfWeek dayOfWeek = startAt.getDayOfWeek();
        DiaSemana diaSemanaAgendamento = DiaSemana.fromDayOfWeek(dayOfWeek);

        List<Disponibilidade> disponibilidades = findByPsicologoId(psicologoId);

        boolean hasAvailability = false;

        for (Disponibilidade disp : disponibilidades) {
            if (disp.isRecorrente()) {
                if (disp.getDiaSemana() == diaSemanaAgendamento) {
                    LocalTime dispStartTime = disp.getStartAt().toLocalTime();
                    LocalTime dispEndTime = disp.getEndAt().toLocalTime();
                    LocalTime aptStartTime = startAt.toLocalTime();
                    LocalTime aptEndTime = endAt.toLocalTime();
                    if ((aptStartTime.equals(dispStartTime) || aptStartTime.isAfter(dispStartTime)) &&
                            (aptEndTime.equals(dispEndTime) || aptEndTime.isBefore(dispEndTime))) {

                        hasAvailability = true;
                        break;
                    }
                }
            }
            else {
                boolean isContained = (disp.getStartAt().isBefore(startAt) || disp.getStartAt().isEqual(startAt)) &&
                        (disp.getEndAt().isAfter(endAt) || disp.getEndAt().isEqual(endAt));
                if (isContained) {
                    hasAvailability = true;
                    break;
                }
            }
        }

        if (!hasAvailability) {
            throw new InvalidDateRangeException("O psicólogo não possui disponibilidade cadastrada para o horário solicitado.");
        }
    }
}