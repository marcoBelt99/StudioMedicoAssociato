package com.beltra.sma.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name="ruoli")
@Entity
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_ruolo")
    private Long idRuolo;

    private String tipo;

    @ManyToOne
    @JoinColumn(name = "id_utente", referencedColumnName = "id_utente")
    private Utente utente;

    // Getters and Setters
}
