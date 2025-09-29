package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.AgendamentoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Agendamento;
import com.psicoagenda.psicoagendaapi.models.StatusAgendamento;
import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.services.AgendamentoService;
import com.psicoagenda.psicoagendaapi.security.SecurityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agendamentos")
@Validated
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final SecurityService securityService;

    // Injeção via construtor
    public AgendamentoController(AgendamentoService agendamentoService, SecurityService securityService) {
        this.agendamentoService = agendamentoService;
        this.securityService = securityService;
    }

    // Autorização: Retorna agendamentos do PSICOLOGO ou PACIENTE logado
    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @GetMapping
    public List<AgendamentoResponseDTO> listarAgendamentos() {
        Long userId = securityService.getAuthenticatedUserId();
        // O papel é buscado aqui no runtime
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        List<Agendamento> agendamentos;

        if (role.equals("ROLE_" + UserRole.PSICOLOGO.name())) {
            // Psicólogo vê apenas os seus
            agendamentos = agendamentoService.findByPsicologoId(userId);
        } else if (role.equals("ROLE_" + UserRole.PACIENTE.name())) {
            // Paciente vê apenas os seus
            agendamentos = agendamentoService.findByPacienteId(userId);
        } else {
            agendamentos = List.of();
        }

        return agendamentos.stream()
                .map(agendamento -> agendamentoService.toResponseDTO(agendamento))
                .collect(Collectors.toList());
    }

    // Autorização: PSICOLOGO cria para si. PACIENTE cria para si.
    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @PostMapping
    public AgendamentoResponseDTO criarAgendamento(@Valid @RequestBody AgendamentoRequestDTO agendamentoDto) {

        Long userId = securityService.getAuthenticatedUserId();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        // 1. Regra do Psicólogo: Se for Psicólogo, só pode agendar se o PsicologoId for o seu.
        if (role.equals("ROLE_" + UserRole.PSICOLOGO.name()) && !agendamentoDto.getPsicologoId().equals(userId)) {
            throw new AccessDeniedException("Um psicólogo só pode criar agendamentos para si mesmo.");
        }

        // 2. Regra do Paciente: Se for Paciente, só pode agendar se o PacienteId for o seu.
        if (role.equals("ROLE_" + UserRole.PACIENTE.name()) && !agendamentoDto.getPacienteId().equals(userId)) {
            throw new AccessDeniedException("Um paciente só pode criar agendamentos para si mesmo.");
        }

        Agendamento agendamentoCriado = agendamentoService.save(agendamentoDto);
        return agendamentoService.toResponseDTO(agendamentoCriado);
    }

    // GET /{id}: Permite Psicólogo ou Paciente (Lógica de propriedade já existente)
    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> listarAgendamentoPorId(@PathVariable @Min(1) Long id) {
        Agendamento agendamento = agendamentoService.findById(id);

        // Lógica de Autorização: Se não é o dono (Psicólogo OU Paciente), nega.
        Long userId = securityService.getAuthenticatedUserId();
        boolean isOwner = agendamento.getPsicologo().getId().equals(userId) || agendamento.getPaciente().getId().equals(userId);

        if (!isOwner) {
            throw new AccessDeniedException("Você não tem permissão para visualizar este agendamento.");
        }

        AgendamentoResponseDTO dto = agendamentoService.toResponseDTO(agendamento);
        return ResponseEntity.ok(dto);
    }

    // PATCH /{id}: Permite Psicólogo ou Paciente (Lógica de propriedade já existente)
    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @PatchMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizarAgendamento(@PathVariable @Min(1) Long id, @RequestBody AgendamentoUpdateRequestDTO agendamentoDto) {
        Agendamento agendamentoExistente = agendamentoService.findById(id);

        // Lógica de autorização de PATCH
        Long userId = securityService.getAuthenticatedUserId();
        boolean isOwner = agendamentoExistente.getPsicologo().getId().equals(userId) || agendamentoExistente.getPaciente().getId().equals(userId);

        if (!isOwner) {
            throw new AccessDeniedException("Você não tem permissão para atualizar este agendamento.");
        }

        if (agendamentoDto.getStartAt() != null) {
            agendamentoExistente.setStartAt(agendamentoDto.getStartAt());
        }
        if (agendamentoDto.getEndAt() != null) {
            agendamentoExistente.setEndAt(agendamentoDto.getEndAt());
        }

        // Regra de Negócios: Pacientes não podem mudar o status para CANCELED, apenas Psicólogos.
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        if (agendamentoDto.getStatus() != null) {
            if (role.equals("ROLE_" + UserRole.PACIENTE.name()) && agendamentoDto.getStatus().equals(StatusAgendamento.CANCELED.name())) {
                // Paciente pode alterar status para CANCELED
                agendamentoExistente.setStatus(StatusAgendamento.valueOf(agendamentoDto.getStatus()));
            } else if (role.equals("ROLE_" + UserRole.PSICOLOGO.name())) {
                // Psicólogo pode alterar qualquer status
                agendamentoExistente.setStatus(StatusAgendamento.valueOf(agendamentoDto.getStatus()));
            } else {
                throw new AccessDeniedException("Pacientes só podem alterar o status para CANCELED.");
            }
        }

        if (agendamentoDto.getObservacoes() != null) {
            agendamentoExistente.setObservacoes(agendamentoDto.getObservacoes());
        }

        Agendamento agendamentoSalvo = agendamentoService.save(agendamentoExistente);
        return ResponseEntity.ok(agendamentoService.toResponseDTO(agendamentoSalvo));
    }

    // DELETE /{id}: Permite Psicólogo ou Paciente (Lógica de propriedade já existente)
    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable @Min(1) Long id) {
        Agendamento agendamentoExistente = agendamentoService.findById(id);

        // Lógica de autorização de DELETE
        Long userId = securityService.getAuthenticatedUserId();
        boolean isOwner = agendamentoExistente.getPsicologo().getId().equals(userId) || agendamentoExistente.getPaciente().getId().equals(userId);

        if (!isOwner) {
            throw new AccessDeniedException("Você não tem permissão para deletar este agendamento.");
        }

        agendamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}