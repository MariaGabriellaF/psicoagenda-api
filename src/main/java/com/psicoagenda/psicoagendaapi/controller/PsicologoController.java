package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.PsicologoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.services.PsicologoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/psicologos")
public class PsicologoController {

    private final PsicologoService psicologoService;

    public PsicologoController(PsicologoService psicologoService) {
        this.psicologoService = psicologoService;
    }

    @GetMapping
    public List<PsicologoResponseDTO> listarPsicologos(@RequestParam(required = false) String nome) {
        List<Psicologo> psicologos = psicologoService.findAll();
        if (nome != null && !nome.trim().isEmpty()) {
            psicologos = psicologoService.findByNome(nome);
        } else {
            psicologos = psicologoService.findAll();
        }
        return psicologos.stream()
                .map(psicologoService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PsicologoResponseDTO> listarPsicologoPorId(@PathVariable Long id) {
        Psicologo psicologo = psicologoService.findById(id);
        PsicologoResponseDTO dto = psicologoService.toResponseDTO(psicologo);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public PsicologoResponseDTO criarPsicologo(@Valid @RequestBody PsicologoRequestDTO psicologoDto) {
        Psicologo psicologoCriado = psicologoService.save(psicologoDto);
        return psicologoService.toResponseDTO(psicologoCriado);
    }

    @PreAuthorize("hasRole('PSICOLOGO')") // Simplificado. Antes: hasRole('PSICOLOGO')
    @PatchMapping("/{id}")
    public ResponseEntity<PsicologoResponseDTO> atualizarPsicologo(@PathVariable Long id, @RequestBody PsicologoUpdateRequestDTO psicologoDto) {
        Psicologo psicologoSalvo = psicologoService.updateAndAuthorize(id, psicologoDto);
        return ResponseEntity.ok(psicologoService.toResponseDTO(psicologoSalvo));
    }

    @PreAuthorize("hasRole('PSICOLOGO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPsicologo(@PathVariable Long id) {
        psicologoService.deleteAndAuthorize(id);
        return ResponseEntity.noContent().build();
    }
}