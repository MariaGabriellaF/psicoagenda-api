package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.dto.PsicologoRequestDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoResponseDTO;
import com.psicoagenda.psicoagendaapi.dto.PsicologoUpdateRequestDTO;
import com.psicoagenda.psicoagendaapi.models.Psicologo;
import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import com.psicoagenda.psicoagendaapi.models.Agendamento; // NOVO IMPORT
import com.psicoagenda.psicoagendaapi.models.User;
import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.repository.PsicologoRepository;
import com.psicoagenda.psicoagendaapi.repository.DisponibilidadeRepository;
import com.psicoagenda.psicoagendaapi.repository.AgendamentoRepository; // NOVO IMPORT
import com.psicoagenda.psicoagendaapi.exception.ResourceNotFoundException;
import com.psicoagenda.psicoagendaapi.security.SecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PsicologoService {


    private final PsicologoRepository psicologoRepository;
    private final UserService userService;
    private final SecurityService securityService;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final AgendamentoRepository agendamentoRepository; // NOVO CAMPO

    // CONSTRUTOR ATUALIZADO COM AgendamentoRepository
    public PsicologoService(
            PsicologoRepository psicologoRepository,
            UserService userService,
            SecurityService securityService,
            DisponibilidadeRepository disponibilidadeRepository,
            AgendamentoRepository agendamentoRepository) { // NOVO PARÂMETRO
        this.psicologoRepository = psicologoRepository;
        this.userService = userService;
        this.securityService = securityService;
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.agendamentoRepository = agendamentoRepository; // INJEÇÃO
    }

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
        psicologo.setUser(savedUser);

        return psicologoRepository.save(psicologo);
    }

    public List<Psicologo> findByNome(String nome) {
        return psicologoRepository.findByNomeContainingIgnoreCase(nome);
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

    public Psicologo findById(Long id) {
        return psicologoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Psicologo com o ID " + id + " não encontrado."));
    }

    public Psicologo updateAndAuthorize(Long id, PsicologoUpdateRequestDTO psicologoDto) {
        Psicologo psicologoExistente = findById(id);

        Long userId = securityService.getAuthenticatedUserId();

        if (!psicologoExistente.getId().equals(userId)) {
            throw new AccessDeniedException("Um psicólogo só pode atualizar seu próprio perfil.");
        }

        if (psicologoDto.getNome() != null) {
            psicologoExistente.setNome(psicologoDto.getNome());
        }
        if (psicologoDto.getEspecialidade() != null) {
            psicologoExistente.setEspecialidade(psicologoDto.getEspecialidade());
        }
        if (psicologoDto.getCrp() != null) {
            psicologoExistente.setCrp(psicologoDto.getCrp());
        }
        if (psicologoDto.getTeleatendimento() != null) {
            psicologoExistente.setTeleatendimento(psicologoDto.getTeleatendimento());
        }

        return psicologoRepository.save(psicologoExistente);
    }

    // MÉTODO DE SOFT DELETE COM CASCATA COMPLETA
    public void deleteAndAuthorize(Long id) {
        Psicologo psicologoExistente = findById(id);

        Long userId = securityService.getAuthenticatedUserId();

        if (!psicologoExistente.getId().equals(userId)) {
            throw new AccessDeniedException("Um psicólogo só pode deletar seu próprio perfil.");
        }

        // 1. SOFT DELETE DAS DISPONIBILIDADES EM CASCATA
        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findByPsicologoId(id);
        // O método .delete() do JpaRepository aciona o @SQLDelete na entidade Disponibilidade
        disponibilidades.forEach(disponibilidadeRepository::delete);

        // 2. SOFT DELETE DOS AGENDAMENTOS EM CASCATA
        // Soft deleta todos os agendamentos onde este psicólogo é o prestador.
        List<Agendamento> agendamentos = agendamentoRepository.findByPsicologoId(id);
        agendamentos.forEach(agendamentoRepository::delete);

        // 3. SOFT DELETE NO PSICOLOGO (dispara o @SQLDelete na entidade Psicologo)
        psicologoRepository.deleteById(id);

        // 4. SOFT DELETE NO USER (dispara o @SQLDelete na entidade User)
        userService.delete(id);
    }

}