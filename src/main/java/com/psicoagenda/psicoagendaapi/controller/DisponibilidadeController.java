package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.services.DisponibilidadeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/disponibilidades")
@Validated
public class DisponibilidadeController {

    private final DisponibilidadeService disponibilidadeService;

    public DisponibilidadeController(DisponibilidadeService disponibilidadeService) {
        this.disponibilidadeService = disponibilidadeService;
    }

    @GetMapping
    public List<DisponibilidadeResponseDTO> listarDisponibilidades() {
        List<Disponibilidade> disponibilidades = disponibilidadeService.findAll();
        return disponibilidades.stream()
                .map(disponibilidadeService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> listarDisponibilidadePorId(@PathVariable @Min(1) Long id) {
        Disponibilidade disponibilidade = disponibilidadeService.findById(id);
        DisponibilidadeResponseDTO dto = disponibilidadeService.toResponseDTO(disponibilidade);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PostMapping
    public DisponibilidadeResponseDTO criarDisponibilidade(@Valid @RequestBody DisponibilidadeRequestDTO disponibilidadeDto) {
        Disponibilidade disponibilidadeCriada = disponibilidadeService.saveAndAuthorize(disponibilidadeDto);
        return disponibilidadeService.toResponseDTO(disponibilidadeCriada);
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @PatchMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> atualizarDisponibilidade(@PathVariable @Min(1) Long id, @RequestBody DisponibilidadeUpdateRequestDTO disponibilidadeDto) {
        Disponibilidade disponibilidadeSalva = disponibilidadeService.updateAndAuthorize(id, disponibilidadeDto);
        return ResponseEntity.ok(disponibilidadeService.toResponseDTO(disponibilidadeSalva));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDisponibilidade(@PathVariable @Min(1) Long id) {
        disponibilidadeService.deleteAndAuthorize(id);
        return ResponseEntity.noContent().build();
    }
}