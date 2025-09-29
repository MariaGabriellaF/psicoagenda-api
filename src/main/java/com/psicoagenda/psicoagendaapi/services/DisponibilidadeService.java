package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.DiaSemana;
import com.psicoagenda.psicoagendaapi.repository.DisponibilidadeRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DisponibilidadeService {


    private final DisponibilidadeRepository disponibilidadeRepository;
    private final PsicologoService psicologoService;

    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository, PsicologoService psicologoService) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.psicologoService = psicologoService;
    }

    public Disponibilidade save(DisponibilidadeRequestDTO disponibilidadeDto) {
        Psicologo psicologo = psicologoService.findById(disponibilidadeDto.getPsicologoId());

        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade.setPsicologo(psicologo);
        disponibilidade.setStartAt(disponibilidadeDto.getStartAt());
        disponibilidade.setEndAt(disponibilidadeDto.getEndAt());
        disponibilidade.setRecorrente(disponibilidadeDto.isRecorrente());
        disponibilidade.setDiaSemana(DiaSemana.valueOf(disponibilidadeDto.getDiaSemana()));

        return disponibilidadeRepository.save(disponibilidade);
    }

    public Disponibilidade save(Disponibilidade disponibilidade) {
        return disponibilidadeRepository.save(disponibilidade);
    }

    public DisponibilidadeResponseDTO toResponseDTO(Disponibilidade disponibilidade) {
        DisponibilidadeResponseDTO dto = new DisponibilidadeResponseDTO();
        dto.setId(disponibilidade.getId());
        dto.setPsicologoId(disponibilidade.getPsicologo().getId());
        dto.setPsicologoNome(disponibilidade.getPsicologo().getNome());
        dto.setStartAt(disponibilidade.getStartAt());
        dto.setEndAt(disponibilidade.getEndAt());
        dto.setRecorrente(disponibilidade.isRecorrente());
        dto.setDiaSemana(disponibilidade.getDiaSemana().toString());
        return dto;
    }

    public List<Disponibilidade> findAll() {
        return disponibilidadeRepository.findAll();
    }

    public Disponibilidade findById(Long id) {
        return disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade com o ID " + id + " não encontrada."));
    }

    public void delete(Long id) {
        disponibilidadeRepository.deleteById(id);
    }
}