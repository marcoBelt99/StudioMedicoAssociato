package com.beltra.sma.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name="prestazioni")
@Getter
@Setter
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

    @Override
    public String toString() {
        return "Prestazione{" +
                "idPrestazione=" + idPrestazione +
                ", titolo='" + titolo + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", durataMedia=" + durataMedia +
                ", costo=" + costo +
                ", ticket=" + ticket +
                '}';
    }
}
