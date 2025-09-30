package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.AgendamentoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Agendamento;
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.StatusAgendamento;
import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.repository.AgendamentoRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import com.psicoagenda.psicoagendaapi.exception.InvalidDateRangeException;
import com.psicoagenda.psicoagendaapi.security.SecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PsicologoService psicologoService;
    private final PacienteService pacienteService;
    private final SecurityService securityService;
    private final DisponibilidadeService disponibilidadeService;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            PsicologoService psicologoService,
            PacienteService pacienteService,
            SecurityService securityService,
            DisponibilidadeService disponibilidadeService) {

        this.agendamentoRepository = agendamentoRepository;
        this.psicologoService = psicologoService;
        this.pacienteService = pacienteService;
        this.securityService = securityService;
        this.disponibilidadeService = disponibilidadeService;
    }

    public Agendamento save(AgendamentoRequestDTO agendamentoDto) {
        if (agendamentoDto.getStartAt().isAfter(agendamentoDto.getEndAt())) {
            throw new InvalidDateRangeException("A data de início deve ser anterior à data de fim.");
        }
        disponibilidadeService.validateAvailability(
                agendamentoDto.getPsicologoId(),
                agendamentoDto.getStartAt(),
                agendamentoDto.getEndAt()
        );

        Psicologo psicologo = psicologoService.findById(agendamentoDto.getPsicologoId());
        Paciente paciente = pacienteService.findById(agendamentoDto.getPacienteId());

        Agendamento agendamento = new Agendamento();
        agendamento.setPsicologo(psicologo);
        agendamento.setPaciente(paciente);
        agendamento.setStartAt(agendamentoDto.getStartAt());
        agendamento.setEndAt(agendamentoDto.getEndAt());
        agendamento.setStatus(StatusAgendamento.valueOf(agendamentoDto.getStatus()));
        agendamento.setObservacoes(agendamentoDto.getObservacoes());

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento updateAndAuthorize(
            Agendamento agendamentoExistente,
            AgendamentoUpdateRequestDTO agendamentoDto) {

        String userRoleString = securityService.getAuthenticatedUserRoleString();

        if (agendamentoDto.getStartAt() != null) {
            agendamentoExistente.setStartAt(agendamentoDto.getStartAt());
        }
        if (agendamentoDto.getEndAt() != null) {
            agendamentoExistente.setEndAt(agendamentoDto.getEndAt());
        }

        if (agendamentoDto.getStatus() != null) {

            UserRole role = UserRole.valueOf(userRoleString.substring(5));

            if (role == UserRole.PACIENTE) {
                if (agendamentoDto.getStatus().equals(StatusAgendamento.CANCELED.name())) {
                    agendamentoExistente.setStatus(StatusAgendamento.valueOf(agendamentoDto.getStatus()));
                } else {
                    throw new AccessDeniedException("Pacientes só podem alterar o status para CANCELED.");
                }
            } else if (role == UserRole.PSICOLOGO) {
                agendamentoExistente.setStatus(StatusAgendamento.valueOf(agendamentoDto.getStatus()));
            }
        }
        if (agendamentoDto.getObservacoes() != null) {
            agendamentoExistente.setObservacoes(agendamentoDto.getObservacoes());
        }

        return agendamentoRepository.save(agendamentoExistente);
    }

    public Agendamento save(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    public AgendamentoResponseDTO toResponseDTO(Agendamento agendamento) {
        AgendamentoResponseDTO dto = new AgendamentoResponseDTO();
        dto.setId(agendamento.getId());
        dto.setPsicologoId(agendamento.getPsicologo().getId());
        dto.setPsicologoNome(agendamento.getPsicologo().getNome());
        dto.setPacienteId(agendamento.getPaciente().getId());
        dto.setPacienteNome(agendamento.getPaciente().getNome());
        dto.setStartAt(agendamento.getStartAt());
        dto.setEndAt(agendamento.getEndAt());
        dto.setStatus(agendamento.getStatus().toString());
        dto.setObservacoes(agendamento.getObservacoes());
        dto.setCreatedAt(agendamento.getCreatedAt());
        return dto;
    }

    public List<Agendamento> findAll() {
        return agendamentoRepository.findAll();
    }

    public Agendamento findById(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento com o ID " + id + " não encontrado."));
    }

    public Agendamento findByIdAndAuthorize(Long id) {
        Agendamento agendamento = findById(id);

        Long userId = securityService.getAuthenticatedUserId();
        boolean isOwner = agendamento.getPsicologo().getId().equals(userId) || agendamento.getPaciente().getId().equals(userId);

        if (!isOwner) {
            throw new AccessDeniedException("Você não tem permissão para visualizar este agendamento.");
        }
        return agendamento;
    }

    public List<Agendamento> findByPsicologoId(Long psicologoId) {
        return agendamentoRepository.findByPsicologoId(psicologoId);
    }

    public List<Agendamento> listAgendamentosByPsicologoIdAndAuthorize(Long psicologoId) {
        Long authenticatedUserId = securityService.getAuthenticatedUserId();

        if (!psicologoId.equals(authenticatedUserId)) {
            throw new AccessDeniedException("Você só pode visualizar a sua própria agenda de agendamentos.");
        }

        return findByPsicologoId(psicologoId);
    }

    public List<Agendamento> findByPacienteId(Long pacienteId) {
        return agendamentoRepository.findByPacienteId(pacienteId);
    }

    public List<Agendamento> listAgendamentosByPacienteIdAndAuthorize(Long pacienteId) {
        Long authenticatedUserId = securityService.getAuthenticatedUserId();
        String role = securityService.getAuthenticatedUserRoleString();
        if (role.equals("ROLE_" + UserRole.PACIENTE.name()) && !pacienteId.equals(authenticatedUserId)) {
            throw new AccessDeniedException("Um paciente só pode visualizar a sua própria lista de agendamentos.");
        }
        return findByPacienteId(pacienteId);
    }

    public void deleteAndAuthorize(Long id) {
        Agendamento agendamentoExistente = findById(id);

        Long userId = securityService.getAuthenticatedUserId();
        boolean isOwner = agendamentoExistente.getPsicologo().getId().equals(userId) || agendamentoExistente.getPaciente().getId().equals(userId);

        if (!isOwner) {
            throw new AccessDeniedException("Você não tem permissão para deletar este agendamento.");
        }

        agendamentoRepository.deleteById(id);
    }

    public List<Agendamento> listAgendamentosForAuthenticatedUser() {
        Long userId = securityService.getAuthenticatedUserId();
        String role = securityService.getAuthenticatedUserRoleString();

        if (role.equals("ROLE_" + UserRole.PSICOLOGO.name())) {
            return findByPsicologoId(userId);
        } else if (role.equals("ROLE_" + UserRole.PACIENTE.name())) {
            return findByPacienteId(userId);
        }
        return List.of();
    }

    public Agendamento createAndAuthorize(AgendamentoRequestDTO agendamentoDto) {
        Long userId = securityService.getAuthenticatedUserId();
        String role = securityService.getAuthenticatedUserRoleString();

        if (role.equals("ROLE_" + UserRole.PSICOLOGO.name()) && !agendamentoDto.getPsicologoId().equals(userId)) {
            throw new AccessDeniedException("Um psicólogo só pode criar agendamentos para si mesmo.");
        }
        if (role.equals("ROLE_" + UserRole.PACIENTE.name()) && !agendamentoDto.getPacienteId().equals(userId)) {
            throw new AccessDeniedException("Um paciente só pode criar agendamentos para si mesmo.");
        }
        return save(agendamentoDto);
    }
}