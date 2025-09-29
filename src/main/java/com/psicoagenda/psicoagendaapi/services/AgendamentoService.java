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
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AgendamentoService {


    private final AgendamentoRepository agendamentoRepository;
    private final PsicologoService psicologoService;
    private final PacienteService pacienteService;

    // Injeção via construtor
    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            PsicologoService psicologoService,
            PacienteService pacienteService) {

        this.agendamentoRepository = agendamentoRepository;
        this.psicologoService = psicologoService;
        this.pacienteService = pacienteService;
    }

    // ... (restante do código de save e toResponseDTO)

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

    // NOVO Método para buscar agendamentos do psicólogo logado
    public List<Agendamento> findByPsicologoId(Long psicologoId) {
        return agendamentoRepository.findByPsicologoId(psicologoId);
    }

    // NOVO: Método para buscar agendamentos do paciente logado
    public List<Agendamento> findByPacienteId(Long pacienteId) {
        return agendamentoRepository.findByPacienteId(pacienteId);
    }

    public void delete(Long id) {
        agendamentoRepository.deleteById(id);
    }
}