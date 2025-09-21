package com.psicoagenda.psicoagendaapi.repository;

import com.psicoagenda.psicoagendaapi.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

}