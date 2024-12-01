package com.beltra.sma.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="utenti")
public class Utente {

    @Id
    @Column(name = "id_utente")
    private String idUtente;

    @Column(name="username", nullable = false, unique = true)
    private String username;
    private String password;
    private Boolean attivo;

    @OneToOne
    @JoinColumn(name="id_anagrafica",
            referencedColumnName = "id_anagrafica",
            nullable = false)
    private Anagrafica anagrafica;


    @OneToMany(
            mappedBy = "utente",
            fetch = FetchType.EAGER, // TODO: deve per forza essere EAGER
            cascade = CascadeType.ALL)
    private Set<Ruolo> ruoli = new HashSet<>();


    // Getters and Setters

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }

    public Anagrafica getAnagrafica() {
        return anagrafica;
    }

    public void setAnagrafica(Anagrafica anagrafica) {
        this.anagrafica = anagrafica;
    }

    public Set<Ruolo> getRuoli() {
        return ruoli;
    }

    public void setRuoli(Set<Ruolo> ruoli) {
        this.ruoli = ruoli;
    }
}
