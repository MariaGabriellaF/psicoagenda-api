package com.psicoagenda.psicoagendaapi.dto;

public class PsicologoResponseDTO {
    private Long id;
    private String nome;
    private String especialidade;
    private String crp;
    private boolean teleatendimento;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isTeleatendimento() {
        return teleatendimento;
    }

    public void setTeleatendimento(boolean teleatendimento) {
        this.teleatendimento = teleatendimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}