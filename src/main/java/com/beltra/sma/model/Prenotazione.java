package com.beltra.sma.model;

import jakarta.persistence.*;

import java.util.Date;

@Table(name="prenotazioni")
@Entity
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_prenotazione")
    private Long idPrenotazione;

    @Column(name="data_prenotazione")
    private Date dataPrenotazione;
    private boolean effettuata;

    @ManyToOne
    @JoinColumn(name = "id_anagrafica")
    private Anagrafica anagrafica;

    @ManyToOne
    @JoinColumn(name = "id_visita")
    private Visita visita;

    // Getters and Setters
}