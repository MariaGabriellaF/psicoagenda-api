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
        // this.securityService = securityService; // REMOVIDO
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

    // Autorização: APENAS PSICOLOGO (a checagem de ID é feita no Service)
    @PreAuthorize("hasRole('PSICOLOGO')") // Simplificado. Antes: hasRole('PSICOLOGO') and #id == @securityService.getAuthenticatedUserId()
    @PatchMapping("/{id}")
    // DTO alterado para PsicologoUpdateRequestDTO para suportar atualizações parciais
    public ResponseEntity<PsicologoResponseDTO> atualizarPsicologo(@PathVariable Long id, @RequestBody PsicologoUpdateRequestDTO psicologoDto) {
        Psicologo psicologoSalvo = psicologoService.updateAndAuthorize(id, psicologoDto);
        return ResponseEntity.ok(psicologoService.toResponseDTO(psicologoSalvo));
    }

    // Autorização: APENAS PSICOLOGO (a checagem de ID é feita no Service)
    @PreAuthorize("hasRole('PSICOLOGO')") // Simplificado. Antes: hasRole('PSICOLOGO') and #id == @securityService.getAuthenticatedUserId()
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPsicologo(@PathVariable Long id) {
        psicologoService.deleteAndAuthorize(id);
        return ResponseEntity.noContent().build();
    }
}