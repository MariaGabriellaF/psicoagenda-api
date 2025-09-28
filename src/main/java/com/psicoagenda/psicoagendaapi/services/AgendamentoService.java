package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.AgendamentoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Agendamento;
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.StatusAgendamento;
import com.psicoagenda.psicoagendaapi.repository.AgendamentoRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import com.psicoagenda.psicoagendaapi.exception.InvalidDateRangeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {


    private final AgendamentoRepository agendamentoRepository;


    private final PsicologoService psicologoService;


    private final PacienteService pacienteService;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            PsicologoService psicologoService,
            PacienteService pacienteService) {

        this.agendamentoRepository = agendamentoRepository;
        this.psicologoService = psicologoService;
        this.pacienteService = pacienteService;
    }

    public Agendamento save(AgendamentoRequestDTO agendamentoDto) {
        if (agendamentoDto.getStartAt().isAfter(agendamentoDto.getEndAt())) {
            throw new InvalidDateRangeException("A data de início deve ser anterior à data de fim.");
        }

        Psicologo psicologo = psicologoService.findById(agendamentoDto.getPsicologoId());
        Paciente paciente = pacienteService.findById(agendamentoDto.getPacienteId());

        Agendamento agendamento = new Agendamento();
        agendamento.setPsicologo(psicologo);
        agendamento.setPaciente(paciente);
        agendamento.setStartAt(agendamentoDto.getStartAt());
        agendamento.setEndAt(agendamentoDto.getEndAt());
        agendamento.setStatus(StatusAgendamento.valueOf(agendamentoDto.getStatus()));
        agendamento.setObservacoes(agendamentoDto.getObservacoes());

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento save(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    public AgendamentoResponseDTO toResponseDTO(Agendamento agendamento) {
        AgendamentoResponseDTO dto = new AgendamentoResponseDTO();
        dto.setId(agendamento.getId());
        dto.setPsicologoId(agendamento.getPsicologo().getId());
        dto.setPsicologoNome(agendamento.getPsicologo().getNome());
        dto.setPacienteId(agendamento.getPaciente().getId());
        dto.setPacienteNome(agendamento.getPaciente().getNome());
        dto.setStartAt(agendamento.getStartAt());
        dto.setEndAt(agendamento.getEndAt());
        dto.setStatus(agendamento.getStatus().toString());
        dto.setObservacoes(agendamento.getObservacoes());
        dto.setCreatedAt(agendamento.getCreatedAt());
        return dto;
    }

    public List<Agendamento> findAll() {
        return agendamentoRepository.findAll();
    }

    public Agendamento findById(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento com o ID " + id + " não encontrado."));
    }

    public void delete(Long id) {
        agendamentoRepository.deleteById(id);
    }
}