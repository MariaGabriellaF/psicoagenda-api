package com.psicoagenda.psicoagendaapi.repository;

import com.psicoagenda.psicoagendaapi.models.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // NOVO: Busca agendamentos por ID do psicólogo
    List<Agendamento> findByPsicologoId(Long psicologoId);

    // NOVO: Busca agendamentos por ID do paciente
    List<Agendamento> findByPacienteId(Long pacienteId);
}