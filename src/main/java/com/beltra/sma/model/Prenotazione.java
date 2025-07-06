package com.beltra.sma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
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

    private Boolean deleted; // di default vale false

    @ManyToOne
    @JoinColumn(name = "id_anagrafica")
    private Anagrafica anagrafica;

    @ManyToOne // Vecchio
    @JoinColumn(name = "id_visita")
    private Visita visita;


}
