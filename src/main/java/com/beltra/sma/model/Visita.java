package com.beltra.sma.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Table(name="visite")
@Entity
@Getter
@Setter
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
                ", anagrafica (medico)=" + anagrafica.getCognome().concat(" ").concat(anagrafica.getNome()) +
                ", prestazione=" + prestazione.getTitolo() +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visita visita = (Visita) o;
        return Objects.equals(idVisita, visita.idVisita) && Objects.equals(dataVisita, visita.dataVisita) && Objects.equals(ora, visita.ora) && Objects.equals(numAmbulatorio, visita.numAmbulatorio) && Objects.equals(anagrafica, visita.anagrafica) && Objects.equals(prestazione, visita.prestazione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVisita, dataVisita, ora, numAmbulatorio, anagrafica, prestazione);
    }


    /** Metodo per ricavare l'orario di fine della visita */
    public Time calcolaOraFine() {
        return Time.valueOf( getOra().toLocalTime().plusMinutes( Math.round( getPrestazione().getDurataMedia() ) ) );
    }



}
