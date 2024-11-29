package com.beltra.sma.model;


import jakarta.persistence.*;

@Table(name="esiti")
@Entity
public class Esito {

    @Id
    @OneToOne
    @JoinColumn(name = "id_visita")
    private Visita visita;

    private String descrizioneReferto;
    private boolean visible;

    // Getters and Setters
}
