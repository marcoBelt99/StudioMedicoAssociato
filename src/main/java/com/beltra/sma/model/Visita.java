package com.beltra.sma.model;


import jakarta.persistence.*;

import java.sql.Time;
import java.util.Date;

@Table(name="visite")
@Entity
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVisita;

    private Date dataVisita;
    private Time ora;
    @Column(name = "num_ambulatorio")
    private Integer numAmbulatorio;

    @ManyToOne
    @JoinColumn(name = "id_anagrafica")
    private Anagrafica anagrafica;

    @ManyToOne
    @JoinColumn(name = "id_prestazione")
    private Prestazione prestazione;

    // Getters and Setters

    @Override
    public String toString() {
        return "Visita{" +
                "idVisita=" + idVisita +
                ", dataVisita=" + dataVisita +
                ", ora=" + ora +
                ", numAmbulatorio=" + numAmbulatorio +
                ", anagrafica=" + anagrafica.getCognome().concat(" ").concat(anagrafica.getNome()) +
                ", prestazione=" + prestazione.getTitolo() +
                '}';
    }
}
