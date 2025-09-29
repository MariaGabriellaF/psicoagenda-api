package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.PacienteRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.services.PacienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pacientes")
@Validated
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService ) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public List<PacienteResponseDTO> listarPacientes() {
        List<Paciente> pacientes = pacienteService.findAll();
        return pacientes.stream()
                .map(pacienteService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> listarPacientePorId(@PathVariable @Min(1) Long id) {
        Paciente paciente = pacienteService.findByIdAndAuthorize(id);
        PacienteResponseDTO dto = pacienteService.toResponseDTO(paciente);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public PacienteResponseDTO criarPaciente(@Valid @RequestBody PacienteRequestDTO pacienteDto) {
        Paciente pacienteCriado = pacienteService.save(pacienteDto);
        return pacienteService.toResponseDTO(pacienteCriado);
    }

    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @PatchMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> atualizarPaciente(@PathVariable @Min(1) Long id, @RequestBody PacienteUpdateRequestDTO pacienteDto) {
        Paciente pacienteSalvo = pacienteService.updateAndAuthorize(id, pacienteDto);
        return ResponseEntity.ok(pacienteService.toResponseDTO(pacienteSalvo));
    }

    @PreAuthorize("hasAnyRole('PSICOLOGO', 'PACIENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable @Min(1) Long id) {
        pacienteService.deleteAndAuthorize(id);
        return ResponseEntity.noContent().build();
    }
}