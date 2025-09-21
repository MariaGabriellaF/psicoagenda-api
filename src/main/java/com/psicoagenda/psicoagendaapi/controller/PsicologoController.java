package com.psicoagenda.psicoagendaapi.controller;

import com.psicoagenda.psicoagendaapi.dto.PsicologoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.services.PsicologoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/psicologos")
public class PsicologoController {

    @Autowired
    private PsicologoService psicologoService;

    @GetMapping
    public List<PsicologoResponseDTO> listarPsicologos() {
        List<Psicologo> psicologos = psicologoService.findAll();
        return psicologos.stream()
                .map(psicologo -> psicologoService.toResponseDTO(psicologo))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PsicologoResponseDTO> listarPsicologoPorId(@PathVariable Long id) {
        Optional<Psicologo> psicologo = psicologoService.findById(id);
        if (psicologo.isPresent()) {
            PsicologoResponseDTO dto = psicologoService.toResponseDTO(psicologo.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public PsicologoResponseDTO criarPsicologo(@RequestBody PsicologoRequestDTO psicologoDto) {
        Psicologo psicologoCriado = psicologoService.save(psicologoDto);
        return psicologoService.toResponseDTO(psicologoCriado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PsicologoResponseDTO> atualizarPsicologo(@PathVariable Long id, @RequestBody PsicologoRequestDTO psicologoDto) {
        Optional<Psicologo> psicologoExistente = psicologoService.findById(id);
        if (psicologoExistente.isPresent()) {
            Psicologo psicologoAtualizado = psicologoExistente.get();

            if (psicologoDto.getNome() != null) {
                psicologoAtualizado.setNome(psicologoDto.getNome());
            }
            if (psicologoDto.getEspecialidade() != null) {
                psicologoAtualizado.setEspecialidade(psicologoDto.getEspecialidade());
            }
            if (psicologoDto.getCrp() != null) {
                psicologoAtualizado.setCrp(psicologoDto.getCrp());
            }
            // O tipo boolean não pode ser nulo, mas o tipo Boolean pode.
            // Para não ter o erro, é preciso incluir o campo no DTO
            if (psicologoDto.isTeleatendimento() != psicologoAtualizado.isTeleatendimento()) {
                psicologoAtualizado.setTeleatendimento(psicologoDto.isTeleatendimento());
            }

            Psicologo psicologoSalvo = psicologoService.save(psicologoAtualizado);
            return ResponseEntity.ok(psicologoService.toResponseDTO(psicologoSalvo));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPsicologo(@PathVariable Long id) {
        psicologoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}