package com.psicoagenda.psicoagendaapi.models;

import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "pacientes")
@SQLDelete(sql = "UPDATE pacientes SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Paciente implements Serializable {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String telefone;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}