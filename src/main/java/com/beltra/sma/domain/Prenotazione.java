package com.beltra.sma.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "prenotazioni", schema = "public")
public class Prenotazione {
    @Id
    @ColumnDefault("nextval('prenotazioni_id_prenotazione_seq')")
    @Column(name = "id_prenotazione", nullable = false)
    private Integer id;

    @Column(name = "data_prenotazione", nullable = false)
    private LocalDate dataPrenotazione;

    @ColumnDefault("false")
    @Column(name = "effettuata")
    private Boolean effettuata;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_visita", nullable = false)
    private Visita idVisita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_anagrafica", nullable = false)
    private Anagrafica idAnagrafica;

}