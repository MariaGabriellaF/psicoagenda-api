package com.psicoagenda.psicoagendaapi.dto;

public class PsicologoRequestDTO {
    private String nome;
    private String especialidade;
    private String crp;
    private boolean teleatendimento;
    private UserRequestDTO user;

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

    public UserRequestDTO getUser() {
        return user;
    }

    public void setUser(UserRequestDTO user) {
        this.user = user;
    }
}