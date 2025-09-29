package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.PsicologoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.services.PsicologoService;
import com.psicoagenda.psicoagendaapi.security.SecurityService;
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
    private final SecurityService securityService;

    // Injeção via construtor
    public PsicologoController(PsicologoService psicologoService, SecurityService securityService) {
        this.psicologoService = psicologoService;
        this.securityService = securityService;
    }

    @GetMapping
    public List<PsicologoResponseDTO> listarPsicologos() {
        List<Psicologo> psicologos = psicologoService.findAll();
        return psicologos.stream()
                .map(psicologo -> psicologoService.toResponseDTO(psicologo))
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

    // Autorização: APENAS PSICOLOGO e APENAS o seu próprio ID
    @PreAuthorize("hasRole('PSICOLOGO') and #id == @securityService.getAuthenticatedUserId()")
    @PatchMapping("/{id}")
    public ResponseEntity<PsicologoResponseDTO> atualizarPsicologo(@PathVariable Long id, @RequestBody PsicologoRequestDTO psicologoDto) {
        Psicologo psicologoExistente = psicologoService.findById(id);

        if (psicologoDto.getNome() != null) {
            psicologoExistente.setNome(psicologoDto.getNome());
        }
        if (psicologoDto.getEspecialidade() != null) {
            psicologoExistente.setEspecialidade(psicologoDto.getEspecialidade());
        }
        if (psicologoDto.getCrp() != null) {
            psicologoExistente.setCrp(psicologoDto.getCrp());
        }
        if (psicologoDto.isTeleatendimento() != psicologoExistente.isTeleatendimento()) {
            psicologoExistente.setTeleatendimento(psicologoDto.isTeleatendimento());
        }

        Psicologo psicologoSalvo = psicologoService.save(psicologoExistente);
        return ResponseEntity.ok(psicologoService.toResponseDTO(psicologoSalvo));
    }

    // Autorização: APENAS PSICOLOGO e APENAS o seu próprio ID
    @PreAuthorize("hasRole('PSICOLOGO') and #id == @securityService.getAuthenticatedUserId()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPsicologo(@PathVariable Long id) {
        psicologoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}