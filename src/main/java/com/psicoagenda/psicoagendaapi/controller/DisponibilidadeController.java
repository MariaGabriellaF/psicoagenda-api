package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.services.DisponibilidadeService;
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
@RequestMapping("/disponibilidades")
@Validated
public class DisponibilidadeController {

    @Autowired
    private DisponibilidadeService disponibilidadeService;

    @GetMapping
    public List<DisponibilidadeResponseDTO> listarDisponibilidades() {
        List<Disponibilidade> disponibilidades = disponibilidadeService.findAll();
        return disponibilidades.stream()
                .map(disponibilidade -> disponibilidadeService.toResponseDTO(disponibilidade))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> listarDisponibilidadePorId(@PathVariable @Min(1) Long id) {
        Disponibilidade disponibilidade = disponibilidadeService.findById(id);
        DisponibilidadeResponseDTO dto = disponibilidadeService.toResponseDTO(disponibilidade);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public DisponibilidadeResponseDTO criarDisponibilidade(@Valid @RequestBody DisponibilidadeRequestDTO disponibilidadeDto) {
        Disponibilidade disponibilidadeCriada = disponibilidadeService.save(disponibilidadeDto);
        return disponibilidadeService.toResponseDTO(disponibilidadeCriada);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> atualizarDisponibilidade(@PathVariable @Min(1) Long id, @RequestBody DisponibilidadeUpdateRequestDTO disponibilidadeDto) {
        Disponibilidade disponibilidadeExistente = disponibilidadeService.findById(id);

        if (disponibilidadeDto.getStartAt() != null) {
            disponibilidadeExistente.setStartAt(disponibilidadeDto.getStartAt());
        }
        if (disponibilidadeDto.getEndAt() != null) {
            disponibilidadeExistente.setEndAt(disponibilidadeDto.getEndAt());
        }
        if (disponibilidadeDto.getDiaSemana() != null) {
            disponibilidadeExistente.setDiaSemana(com.psicoagenda.psicoagendaapi.models.DiaSemana.valueOf(disponibilidadeDto.getDiaSemana()));
        }
        if (disponibilidadeDto.getRecorrente() != null) {
            disponibilidadeExistente.setRecorrente(disponibilidadeDto.getRecorrente());
        }

        Disponibilidade disponibilidadeSalva = disponibilidadeService.save(disponibilidadeExistente);
        return ResponseEntity.ok(disponibilidadeService.toResponseDTO(disponibilidadeSalva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDisponibilidade(@PathVariable @Min(1) Long id) {
        disponibilidadeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}