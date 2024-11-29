package com.beltra.sma.model;


import jakarta.persistence.*;

@Table(name = "collaborazioni")
@Entity
public class Collaborazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCollaborazione;

    @ManyToOne
    @JoinColumn(name = "id_visita", nullable = false)
    private Visita visita;

    @ManyToOne
    @JoinColumn(name = "id_anagrafica", nullable = false)
    private Anagrafica anagrafica;

    // Getters and Setters
}
