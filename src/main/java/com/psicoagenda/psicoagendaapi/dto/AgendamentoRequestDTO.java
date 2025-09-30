package com.psicoagenda.psicoagendaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AgendamentoRequestDTO {
    @NotNull
    private Long psicologoId;
    @NotNull
    private Long pacienteId;
    @NotNull
    private LocalDateTime startAt;
    @NotNull
    private LocalDateTime endAt;
    @NotBlank
    private String status;
    private String observacoes;

    public Long getPsicologoId() {
        return psicologoId;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public String getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }

}