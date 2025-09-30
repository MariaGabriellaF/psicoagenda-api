package com.psicoagenda.psicoagendaapi.models;

import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "psicologos")
@SQLDelete(sql = "UPDATE psicologos SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Psicologo implements Serializable {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String especialidade;

    @Column(unique = true, nullable = false)
    private String crp;

    private boolean teleatendimento;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;


    public Psicologo() {
    }

    public Psicologo(String nome, String especialidade, String crp, boolean teleatendimento, User user) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.crp = crp;
        this.teleatendimento = teleatendimento;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}