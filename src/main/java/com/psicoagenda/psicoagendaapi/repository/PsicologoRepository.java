package com.psicoagenda.psicoagendaapi.repository;

import com.psicoagenda.psicoagendaapi.models.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
    List<Psicologo> findByNomeContainingIgnoreCase(String nome);
}