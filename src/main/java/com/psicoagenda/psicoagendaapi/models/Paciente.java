package com.psicoagenda.psicoagendaapi.models;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pacientes")
public class Paciente implements Serializable {

    @Id
    private Long id; // A chave primária é a mesma da tabela 'users'

    @Column(nullable = false)
    private String nome;

    private String telefone;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    public Paciente() {
    }

    public Paciente(String nome, String telefone, User user) {
        this.nome = nome;
        this.telefone = telefone;
        this.user = user;
    }


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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}