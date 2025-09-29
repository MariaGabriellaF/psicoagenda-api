package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.PacienteRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PacienteResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Paciente;
import com.psicoagenda.psicoagendaapi.models.User;
import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.repository.PacienteRepository;
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PacienteService {


    private final PacienteRepository pacienteRepository;
    private final UserService userService;

    public PacienteService(PacienteRepository pacienteRepository, UserService userService) {
        this.pacienteRepository = pacienteRepository;
        this.userService = userService;
    }

    public Paciente save(PacienteRequestDTO pacienteDto) {
        User user = new User();
        user.setEmail(pacienteDto.getUser().getEmail());
        user.setPasswordHash(pacienteDto.getUser().getPassword());
        user.setRole(UserRole.PACIENTE);

        Paciente paciente = new Paciente();
        paciente.setNome(pacienteDto.getNome());
        paciente.setTelefone(pacienteDto.getTelefone());
        paciente.setUser(user);

        User savedUser = userService.save(user);
        paciente.setUser(savedUser);

        return pacienteRepository.save(paciente);
    }

    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public PacienteResponseDTO toResponseDTO(Paciente paciente) {
        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(paciente.getId());
        dto.setNome(paciente.getNome());
        dto.setTelefone(paciente.getTelefone());
        dto.setEmail(paciente.getUser().getEmail());
        return dto;
    }

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Paciente findById(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com o ID " + id + " não encontrado."));
    }

    public void delete(Long id) {
        pacienteRepository.deleteById(id);
        userService.delete(id);
    }
}