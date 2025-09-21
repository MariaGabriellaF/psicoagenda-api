package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.PacienteRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pacientes")
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
    public ResponseEntity<PacienteResponseDTO> listarPacientePorId(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteService.findById(id);
        if (paciente.isPresent()) {
            PacienteResponseDTO dto = pacienteService.toResponseDTO(paciente.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public PacienteResponseDTO criarPaciente(@RequestBody PacienteRequestDTO pacienteDto) {
        Paciente pacienteCriado = pacienteService.save(pacienteDto);
        return pacienteService.toResponseDTO(pacienteCriado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> atualizarPaciente(@PathVariable Long id, @RequestBody PacienteRequestDTO pacienteDto) {
        Optional<Paciente> pacienteExistente = pacienteService.findById(id);
        if (pacienteExistente.isPresent()) {
            Paciente pacienteAtualizado = pacienteExistente.get();

            if (pacienteDto.getNome() != null) {
                pacienteAtualizado.setNome(pacienteDto.getNome());
            }
            if (pacienteDto.getTelefone() != null) {
                pacienteAtualizado.setTelefone(pacienteDto.getTelefone());
            }

            Paciente pacienteSalvo = pacienteService.save(pacienteAtualizado);
            return ResponseEntity.ok(pacienteService.toResponseDTO(pacienteSalvo));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}