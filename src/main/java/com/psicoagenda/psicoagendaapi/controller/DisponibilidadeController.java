package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.services.DisponibilidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/disponibilidades")
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
    public ResponseEntity<DisponibilidadeResponseDTO> listarDisponibilidadePorId(@PathVariable Long id) {
        Optional<Disponibilidade> disponibilidade = disponibilidadeService.findById(id);
        if (disponibilidade.isPresent()) {
            DisponibilidadeResponseDTO dto = disponibilidadeService.toResponseDTO(disponibilidade.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public DisponibilidadeResponseDTO criarDisponibilidade(@RequestBody DisponibilidadeRequestDTO disponibilidadeDto) {
        Disponibilidade disponibilidadeCriada = disponibilidadeService.save(disponibilidadeDto);
        return disponibilidadeService.toResponseDTO(disponibilidadeCriada);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> atualizarDisponibilidade(@PathVariable Long id, @RequestBody DisponibilidadeRequestDTO disponibilidadeDto) {
        Optional<Disponibilidade> disponibilidadeExistente = disponibilidadeService.findById(id);
        if (disponibilidadeExistente.isPresent()) {
            Disponibilidade disponibilidadeAtualizada = disponibilidadeExistente.get();

            if (disponibilidadeDto.getStartAt() != null) {
                disponibilidadeAtualizada.setStartAt(disponibilidadeDto.getStartAt());
            }
            if (disponibilidadeDto.getEndAt() != null) {
                disponibilidadeAtualizada.setEndAt(disponibilidadeDto.getEndAt());
            }
            if (disponibilidadeDto.getDiaSemana() != null) {
                disponibilidadeAtualizada.setDiaSemana(com.psicoagenda.psicoagendaapi.models.DiaSemana.valueOf(disponibilidadeDto.getDiaSemana()));
            }
            // O tipo boolean não pode ser nulo
            if (disponibilidadeDto.isRecorrente() != disponibilidadeAtualizada.isRecorrente()) {
                disponibilidadeAtualizada.setRecorrente(disponibilidadeDto.isRecorrente());
            }

            Disponibilidade disponibilidadeSalva = disponibilidadeService.save(disponibilidadeAtualizada);
            return ResponseEntity.ok(disponibilidadeService.toResponseDTO(disponibilidadeSalva));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDisponibilidade(@PathVariable Long id) {
        disponibilidadeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}