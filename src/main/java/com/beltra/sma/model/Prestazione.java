package com.beltra.sma.model;


import jakarta.persistence.*;

@Table(name="prestazioni")
@Entity
public class Prestazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestazione")
    private Long idPrestazione;

    private String titolo;
    private String descrizione;

    @Column(name="durata_media")
    private Double durataMedia;
    private Double costo;
    private Double ticket;

    // Getters and Setters
}
