package com.psicoagenda.psicoagendaapi.dto;

public class PsicologoUpdateRequestDTO {
    private String nome;
    private String especialidade;
    private String crp;
    private Boolean teleatendimento;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getCrp() {
        return crp;
    }

    public void setCrp(String crp) {
        this.crp = crp;
    }

    public Boolean getTeleatendimento() {
        return teleatendimento;
    }

    public void setTeleatendimento(Boolean teleatendimento) {
        this.teleatendimento = teleatendimento;
    }
}