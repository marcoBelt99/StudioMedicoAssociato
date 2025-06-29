package com.beltra.sma.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
/** Memorizza le informazioni principali relative ad una visita, come la data di  */
public class VisitaPrenotataDTO {
    private Date dataPrenotazione;
    private Date dataVisita;
    private Time ora;
    private Integer numAmbulatorio;
    private String nomePaziente;
    private String cognomePaziente;
    private String titoloPrestazione;
    private Double durataPrestazione; // durata delle prestazione della singola visita

    public VisitaPrenotataDTO(Date dataPrenotazione, Date dataVisita, Time ora, Integer numAmbulatorio,
                              String nomePaziente, String cognomePaziente, String titoloPrestazione) {
        this.dataPrenotazione = dataPrenotazione;
        this.dataVisita = dataVisita;
        this.ora = ora;
        this.numAmbulatorio = numAmbulatorio;
        this.nomePaziente = nomePaziente;
        this.cognomePaziente = cognomePaziente;
        this.titoloPrestazione = titoloPrestazione;
    }

    public VisitaPrenotataDTO(Date dataPrenotazione, Date dataVisita, Time ora, Integer numAmbulatorio,
                              String nomePaziente, String cognomePaziente, String titoloPrestazione, Double durataPrestazione) {
        this.dataPrenotazione = dataPrenotazione;
        this.dataVisita = dataVisita;
        this.ora = ora;
        this.numAmbulatorio = numAmbulatorio;
        this.nomePaziente = nomePaziente;
        this.cognomePaziente = cognomePaziente;
        this.titoloPrestazione = titoloPrestazione;
        this.durataPrestazione = durataPrestazione;
    }

    @Override
    public String toString() {
        return "VisitaPrenotataDTO{" +
                "dataPrenotazione=" + dataPrenotazione +
                ", dataVisita=" + dataVisita +
                ", ora=" + ora +
                ", numAmbulatorio=" + numAmbulatorio +
                ", nomePaziente='" + nomePaziente + '\'' +
                ", cognomePaziente='" + cognomePaziente + '\'' +
                ", titoloPrestazione='" + titoloPrestazione + '\'' +
                '}';
    }

    /** Metodo per ricavare l'orario di fine della visita */
    public Time calcolaOraFine() {
        return Time.valueOf( getOra().toLocalTime().plusMinutes( Math.round( durataPrestazione ) ) );
    }
}
