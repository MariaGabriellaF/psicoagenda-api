package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.AgendamentoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Agendamento;
import com.psicoagenda.psicoagendaapi.services.AgendamentoService;
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
@RequestMapping("/agendamentos")
@Validated
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
    public ResponseEntity<AgendamentoResponseDTO> listarAgendamentoPorId(@PathVariable @Min(1) Long id) {
        Agendamento agendamento = agendamentoService.findById(id);
        AgendamentoResponseDTO dto = agendamentoService.toResponseDTO(agendamento);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public AgendamentoResponseDTO criarAgendamento(@Valid @RequestBody AgendamentoRequestDTO agendamentoDto) {
        Agendamento agendamentoCriado = agendamentoService.save(agendamentoDto);
        return agendamentoService.toResponseDTO(agendamentoCriado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizarAgendamento(@PathVariable @Min(1) Long id, @RequestBody AgendamentoUpdateRequestDTO agendamentoDto) {
        Agendamento agendamentoExistente = agendamentoService.findById(id);
        if (agendamentoDto.getStartAt() != null) {
            agendamentoExistente.setStartAt(agendamentoDto.getStartAt());
        }
        if (agendamentoDto.getEndAt() != null) {
            agendamentoExistente.setEndAt(agendamentoDto.getEndAt());
        }
        if (agendamentoDto.getStatus() != null) {
            agendamentoExistente.setStatus(com.psicoagenda.psicoagendaapi.models.StatusAgendamento.valueOf(agendamentoDto.getStatus()));
        }
        if (agendamentoDto.getObservacoes() != null) {
            agendamentoExistente.setObservacoes(agendamentoDto.getObservacoes());
        }
        Agendamento agendamentoSalvo = agendamentoService.save(agendamentoExistente);
        return ResponseEntity.ok(agendamentoService.toResponseDTO(agendamentoSalvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable @Min(1) Long id) {
        agendamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}