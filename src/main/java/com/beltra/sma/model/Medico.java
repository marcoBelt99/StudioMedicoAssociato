package com.beltra.sma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Table(name="medici")
@Entity
@Getter
@Setter
public class Medico {


    @Id
    @Column(name="id_anagrafica")
    private Long idAnagrafica;

    @MapsId
    @OneToOne
    @JoinColumn(name="id_anagrafica",
            referencedColumnName = "id_anagrafica"
    )
    private Anagrafica anagrafica;


    private String matricola;

    @Column(name="specializzazione")
    private String specializzazione;

    // Getters and Setters
}

