package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.PacienteRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.services.PacienteService;
import com.psicoagenda.psicoagendaapi.security.SecurityService;
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
    private final SecurityService securityService; // Injeção do SecurityService

    // Injeção via construtor
    public PacienteController(PacienteService pacienteService, SecurityService securityService) {
        this.pacienteService = pacienteService;
        this.securityService = securityService;
    }

    // Autorização: Permissão no SecurityConfig (somente PSICOLOGO pode listar)
    @GetMapping
    public List<PacienteResponseDTO> listarPacientes() {
        List<Paciente> pacientes = pacienteService.findAll();
        return pacientes.stream()
                .map(paciente -> pacienteService.toResponseDTO(paciente))
                .collect(Collectors.toList());
    }

    // GET /{id}: Paciente só pode ver o seu perfil. Psicólogo pode ver qualquer.
    @PreAuthorize("hasRole('PSICOLOGO') or (hasRole('PACIENTE') and #id == @securityService.getAuthenticatedUserId())")
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> listarPacientePorId(@PathVariable @Min(1) Long id) {
        Paciente paciente = pacienteService.findById(id);
        PacienteResponseDTO dto = pacienteService.toResponseDTO(paciente);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public PacienteResponseDTO criarPaciente(@Valid @RequestBody PacienteRequestDTO pacienteDto) {
        Paciente pacienteCriado = pacienteService.save(pacienteDto);
        return pacienteService.toResponseDTO(pacienteCriado);
    }

    // PATCH /{id}: Paciente só pode atualizar o seu perfil. Psicólogo pode atualizar qualquer.
    @PreAuthorize("hasRole('PSICOLOGO') or (hasRole('PACIENTE') and #id == @securityService.getAuthenticatedUserId())")
    @PatchMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> atualizarPaciente(@PathVariable @Min(1) Long id, @RequestBody PacienteUpdateRequestDTO pacienteDto) {
        Paciente pacienteExistente = pacienteService.findById(id);

        if (pacienteDto.getNome() != null) {
            pacienteExistente.setNome(pacienteDto.getNome());
        }
        if (pacienteDto.getTelefone() != null) {
            pacienteExistente.setTelefone(pacienteDto.getTelefone());
        }

        Paciente pacienteSalvo = pacienteService.save(pacienteExistente);
        return ResponseEntity.ok(pacienteService.toResponseDTO(pacienteSalvo));
    }

    // DELETE /{id}: Paciente só pode deletar o seu perfil. Psicólogo pode deletar qualquer.
    @PreAuthorize("hasRole('PSICOLOGO') or (hasRole('PACIENTE') and #id == @securityService.getAuthenticatedUserId())")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable @Min(1) Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}