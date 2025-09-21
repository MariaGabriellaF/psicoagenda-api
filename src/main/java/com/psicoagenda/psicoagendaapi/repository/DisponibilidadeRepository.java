package com.psicoagenda.psicoagendaapi.repository;

import com.psicoagenda.psicoagendaapi.models.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {

}