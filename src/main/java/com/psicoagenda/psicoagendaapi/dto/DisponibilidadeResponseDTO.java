package com.psicoagenda.psicoagendaapi.dto;

import java.time.LocalDateTime;

public class DisponibilidadeResponseDTO {
    private Long id;
    private Long psicologoId;
    private String psicologoNome;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private boolean recorrente;
    private String diaSemana;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPsicologoId() {
        return psicologoId;
    }

    public void setPsicologoId(Long psicologoId) {
        this.psicologoId = psicologoId;
    }

    public String getPsicologoNome() {
        return psicologoNome;
    }

    public void setPsicologoNome(String psicologoNome) {
        this.psicologoNome = psicologoNome;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public boolean isRecorrente() {
        return recorrente;
    }

    public void setRecorrente(boolean recorrente) {
        this.recorrente = recorrente;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }
}