package com.psicoagenda.psicoagendaapi.models;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "psicologos")
public class Psicologo implements Serializable {

    @Id
    private Long id; // A chave primária é a mesma da tabela 'users'

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String especialidade;

    @Column(unique = true, nullable = false)
    private String crp;

    private boolean teleatendimento;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    // Construtor padrão (necessário para JPA)
    public Psicologo() {
    }

    public Psicologo(String nome, String especialidade, String crp, boolean teleatendimento, User user) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.crp = crp;
        this.teleatendimento = teleatendimento;
        this.user = user;
    }

    // Métodos Getters e Setters
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}