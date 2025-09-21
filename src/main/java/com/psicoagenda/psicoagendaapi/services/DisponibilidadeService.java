package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.DisponibilidadeResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.DiaSemana;
import com.psicoagenda.psicoagendaapi.repository.DisponibilidadeRepository;
import com.psicoagenda.psicoagendaapi.repository.PsicologoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DisponibilidadeService {

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @Autowired
    private PsicologoRepository psicologoRepository;

    public Disponibilidade save(DisponibilidadeRequestDTO disponibilidadeDto) {
        Optional<Psicologo> psicologo = psicologoRepository.findById(disponibilidadeDto.getPsicologoId());
        if (psicologo.isEmpty()) {
            // Tratamento de erro ou exceção, se o psicólogo não for encontrado
            return null;
        }

        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade.setPsicologo(psicologo.get());
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

    public Optional<Disponibilidade> findById(Long id) {
        return disponibilidadeRepository.findById(id);
    }

    public void delete(Long id) {
        disponibilidadeRepository.deleteById(id);
    }
}