package com.beltra.sma.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
