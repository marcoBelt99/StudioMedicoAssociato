package com.beltra.sma.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

/** Unisce i campi di interesse delle tabelle coinvolte in specifiche queries.<br>
 *  In particolare, funziona bene sia per:
 *  - "Elenco giornaliero delle visite prenotate (ed effettuate)".
 *  - "Elenco giornaliero delle visite prenotate e non effettuate".
 *  <br>
 *  Le entità coinvolte sono: Visita, Prenotazione, Paziente, Anagrafica
 * */
@Getter
@Setter
public class VisitaPrenotataDTO {
    private Date dataVisita;
    private Time ora;
    private Integer numAmbulatorio;
    private String nomePaziente;
    private String cognomePaziente;

    public VisitaPrenotataDTO(Date dataVisita, Time ora, Integer numAmbulatorio,
                              String nomePaziente, String cognomePaziente) {
        this.dataVisita = dataVisita;
        this.ora = ora;
        this.numAmbulatorio = numAmbulatorio;
        this.nomePaziente = nomePaziente;
        this.cognomePaziente = cognomePaziente;
    }


}
