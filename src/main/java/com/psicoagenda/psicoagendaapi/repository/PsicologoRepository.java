package com.psicoagenda.psicoagendaapi.repository;

import com.psicoagenda.psicoagendaapi.models.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {

}