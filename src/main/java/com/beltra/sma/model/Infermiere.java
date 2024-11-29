package com.beltra.sma.model;


import jakarta.persistence.*;

@Table(name="infermieri")
@Entity
public class Infermiere {

    @Id
    @OneToOne
    @JoinColumn(name = "id_anagrafica")
    private Anagrafica anagrafica;

    @Column(name="num_cartellino")
    private String numCartellino;

    private String tipologia;

    // Getters and Setters
}
