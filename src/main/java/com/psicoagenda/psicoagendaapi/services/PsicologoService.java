package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.PsicologoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoResponseDTO;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.User;
import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.repository.PsicologoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PsicologoService {

    @Autowired
    private PsicologoRepository psicologoRepository;

    @Autowired
    private UserService userService;

    public Psicologo save(PsicologoRequestDTO psicologoDto) {
        User user = new User();
        user.setEmail(psicologoDto.getUser().getEmail());
        user.setPasswordHash(psicologoDto.getUser().getPassword());
        user.setRole(UserRole.PSICOLOGO);

        Psicologo psicologo = new Psicologo();
        psicologo.setNome(psicologoDto.getNome());
        psicologo.setEspecialidade(psicologoDto.getEspecialidade());
        psicologo.setCrp(psicologoDto.getCrp());
        psicologo.setTeleatendimento(psicologoDto.isTeleatendimento());

        User savedUser = userService.save(user);
        // psicologo.setId(savedUser.getId()); // <<< LINHA REMOVIDA
        psicologo.setUser(savedUser);

        return psicologoRepository.save(psicologo);
    }

    public Psicologo save(Psicologo psicologo) {
        return psicologoRepository.save(psicologo);
    }

    public PsicologoResponseDTO toResponseDTO(Psicologo psicologo) {
        PsicologoResponseDTO dto = new PsicologoResponseDTO();
        dto.setId(psicologo.getId());
        dto.setNome(psicologo.getNome());
        dto.setEspecialidade(psicologo.getEspecialidade());
        dto.setCrp(psicologo.getCrp());
        dto.setTeleatendimento(psicologo.isTeleatendimento());
        dto.setEmail(psicologo.getUser().getEmail());
        return dto;
    }

    public List<Psicologo> findAll() {
        return psicologoRepository.findAll();
    }

    public Optional<Psicologo> findById(Long id) {
        return psicologoRepository.findById(id);
    }

    public void delete(Long id) {
        psicologoRepository.deleteById(id);
        userService.delete(id);
    }
}