package com.beltra.sma.service;

import com.beltra.sma.dto.VisitaDTO;
import com.beltra.sma.model.Visita;

import java.util.List;

public interface VisitaService {

    /** Recupera l'intera tabella delle Visite usufruite dai pazienti. */
    List<VisitaDTO> getAllVisite();


    /** Per l'utente paziente: recupera le visite che ha prenotato */
    List<VisitaDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(String username);
}
