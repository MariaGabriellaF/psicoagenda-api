package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.AgendamentoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Agendamento;
import com.psicoagenda.psicoagendaapi.services.AgendamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agendamentos")
@Validated
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @GetMapping
    public List<AgendamentoResponseDTO> listarAgendamentos() {
        List<Agendamento> agendamentos = agendamentoService.listAgendamentosForAuthenticatedUser();

        return agendamentos.stream()
                .map(agendamentoService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @PostMapping
    public AgendamentoResponseDTO criarAgendamento(@Valid @RequestBody AgendamentoRequestDTO agendamentoDto) {
        Agendamento agendamentoCriado = agendamentoService.createAndAuthorize(agendamentoDto);

        return agendamentoService.toResponseDTO(agendamentoCriado);
    }

    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> listarAgendamentoPorId(@PathVariable @Min(1) Long id) {
        // Lógica de checagem de propriedade movida para o Service
        Agendamento agendamento = agendamentoService.findByIdAndAuthorize(id);

        AgendamentoResponseDTO dto = agendamentoService.toResponseDTO(agendamento);
        return ResponseEntity.ok(dto);
    }
    // NOVO: Rota para listar agendamentos de um psicólogo.
    // Autorização apenas para PSICOLOGO (a checagem de ID é feita no Service)
    @PreAuthorize("hasRole('PSICOLOGO')")
    @GetMapping("/psicologo/{psicologoId}")
    public List<AgendamentoResponseDTO> listarAgendamentosPorPsicologo(@PathVariable @Min(1) Long psicologoId) {
        List<Agendamento> agendamentos = agendamentoService.listAgendamentosByPsicologoIdAndAuthorize(psicologoId);
        return agendamentos.stream()
                .map(agendamentoService::toResponseDTO)
                .collect(Collectors.toList());
    }

    // NOVO: Rota para listar agendamentos de um paciente.
    // Autorização para PSICOLOGO e PACIENTE (a checagem de ID/Role é feita no Service)
    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @GetMapping("/paciente/{pacienteId}")
    public List<AgendamentoResponseDTO> listarAgendamentosPorPaciente(@PathVariable @Min(1) Long pacienteId) {
        List<Agendamento> agendamentos = agendamentoService.listAgendamentosByPacienteIdAndAuthorize(pacienteId);
        return agendamentos.stream()
                .map(agendamentoService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @PatchMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizarAgendamento(
            @PathVariable @Min(1) Long id,
            @RequestBody AgendamentoUpdateRequestDTO agendamentoDto) {

        Agendamento agendamentoExistente = agendamentoService.findByIdAndAuthorize(id);

        Agendamento agendamentoSalvo = agendamentoService.updateAndAuthorize(
                agendamentoExistente,
                agendamentoDto
        );
        return ResponseEntity.ok(agendamentoService.toResponseDTO(agendamentoSalvo));
    }

    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable @Min(1) Long id) {
        agendamentoService.deleteAndAuthorize(id);
        return ResponseEntity.noContent().build();
    }
}