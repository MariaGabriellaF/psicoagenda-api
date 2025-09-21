package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.AgendamentoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Agendamento;
import com.psicoagenda.psicoagendaapi.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping
    public List<AgendamentoResponseDTO> listarAgendamentos() {
        List<Agendamento> agendamentos = agendamentoService.findAll();
        return agendamentos.stream()
                .map(agendamento -> agendamentoService.toResponseDTO(agendamento))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> listarAgendamentoPorId(@PathVariable Long id) {
        Optional<Agendamento> agendamento = agendamentoService.findById(id);
        if (agendamento.isPresent()) {
            AgendamentoResponseDTO dto = agendamentoService.toResponseDTO(agendamento.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public AgendamentoResponseDTO criarAgendamento(@RequestBody AgendamentoRequestDTO agendamentoDto) {
        Agendamento agendamentoCriado = agendamentoService.save(agendamentoDto);
        return agendamentoService.toResponseDTO(agendamentoCriado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizarAgendamento(@PathVariable Long id, @RequestBody AgendamentoRequestDTO agendamentoDto) {
        Optional<Agendamento> agendamentoExistente = agendamentoService.findById(id);
        if (agendamentoExistente.isPresent()) {
            Agendamento agendamentoAtualizado = agendamentoExistente.get();
            if (agendamentoDto.getStartAt() != null) {
                agendamentoAtualizado.setStartAt(agendamentoDto.getStartAt());
            }
            if (agendamentoDto.getEndAt() != null) {
                agendamentoAtualizado.setEndAt(agendamentoDto.getEndAt());
            }
            if (agendamentoDto.getStatus() != null) {
                agendamentoAtualizado.setStatus(com.psicoagenda.psicoagendaapi.models.StatusAgendamento.valueOf(agendamentoDto.getStatus()));
            }
            if (agendamentoDto.getObservacoes() != null) {
                agendamentoAtualizado.setObservacoes(agendamentoDto.getObservacoes());
            }
            Agendamento agendamentoSalvo = agendamentoService.save(agendamentoAtualizado);
            return ResponseEntity.ok(agendamentoService.toResponseDTO(agendamentoSalvo));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        agendamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}