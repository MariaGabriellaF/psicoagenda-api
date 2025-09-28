package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.PacienteRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.services.PacienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pacientes")
@Validated
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<PacienteResponseDTO> listarPacientes() {
        List<Paciente> pacientes = pacienteService.findAll();
        return pacientes.stream()
                .map(paciente -> pacienteService.toResponseDTO(paciente))
                .collect(Collectors.toList());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable @Min(1) Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}