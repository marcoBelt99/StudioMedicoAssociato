package com.beltra.sma.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "visite", schema = "public")
public class Visita {
    @Id
    @ColumnDefault("nextval('visite_id_visita_seq')")
    @Column(name = "id_visita", nullable = false)
    private Integer id;

    @Column(name = "data_visita", nullable = false)
    private LocalDate dataVisita;

    @Column(name = "ora", nullable = false)
    private LocalTime ora;

    @Column(name = "num_ambulatorio", nullable = false)
    private Integer numAmbulatorio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_anagrafica", nullable = false)
    private Anagrafica idAnagrafica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_prestazione", nullable = false)
    private Prestazione idPrestazione;

}