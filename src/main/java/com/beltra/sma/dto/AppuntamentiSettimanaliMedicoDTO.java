package com.beltra.sma.dto;

import lombok.Getter;


import java.sql.Time;
import java.util.Date;

@Getter
public class AppuntamentiSettimanaliMedicoDTO {
    private Long idVisita;
    private Date dataVisita;
    private Time oraInizioVisita;
    private Time oraFineVisita; // campo calcolato
    private String titoloPrestazione;
    private Integer numAmbulatorio;
    private String nomePaziente;
    private String cognomePaziente;
    private String codiceFiscalePaziente;


    public AppuntamentiSettimanaliMedicoDTO(Long idVisita, Date dataVisita, Time oraInizioVisita,
                                            Double durataVisita, String titoloPrestazione,
                                            Integer numAmbulatorio, String nomePaziente,
                                            String cognomePaziente, String codiceFiscalePaziente) {
        this.idVisita = idVisita;
        this.dataVisita = dataVisita;
        this.oraInizioVisita = oraInizioVisita;
        this.oraFineVisita = Time.valueOf( oraInizioVisita.toLocalTime().plusMinutes(durataVisita.intValue()) );
        this.titoloPrestazione = titoloPrestazione;
        this.numAmbulatorio = numAmbulatorio;
        this.nomePaziente = nomePaziente;
        this.cognomePaziente = cognomePaziente;
        this.codiceFiscalePaziente = codiceFiscalePaziente;
    }
}
