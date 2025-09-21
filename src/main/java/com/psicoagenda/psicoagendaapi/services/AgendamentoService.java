package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.AgendamentoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.AgendamentoResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Agendamento;
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.StatusAgendamento;
import com.psicoagenda.psicoagendaapi.repository.AgendamentoRepository;
import com.psicoagenda.psicoagendaapi.repository.PacienteRepository;
import com.psicoagenda.psicoagendaapi.repository.PsicologoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PsicologoRepository psicologoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public Agendamento save(AgendamentoRequestDTO agendamentoDto) {
        Optional<Psicologo> psicologo = psicologoRepository.findById(agendamentoDto.getPsicologoId());
        if (psicologo.isEmpty()) {
            // Tratamento de erro ou exceção, se o psicólogo não for encontrado
            return null;
        }

        Optional<Paciente> paciente = pacienteRepository.findById(agendamentoDto.getPacienteId());
        if (paciente.isEmpty()) {
            // Tratamento de erro ou exceção, se o paciente não for encontrado
            return null;
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setPsicologo(psicologo.get());
        agendamento.setPaciente(paciente.get());
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

    public Optional<Agendamento> findById(Long id) {
        return agendamentoRepository.findById(id);
    }

    public void delete(Long id) {
        agendamentoRepository.deleteById(id);
    }
}