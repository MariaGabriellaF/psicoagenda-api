package com.psicoagenda.psicoagendaapi.repository;

import com.psicoagenda.psicoagendaapi.models.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

}