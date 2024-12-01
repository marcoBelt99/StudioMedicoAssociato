package com.beltra.sma.service;

import com.beltra.sma.dto.VisitaPrenotataDTO;


import java.util.List;

public interface VisitaService {

    /** Recupera l'intera tabella delle Visite usufruite dai pazienti. */
    List<VisitaPrenotataDTO> getAllVisite();

    /** Recupera l'elenco delle visite prenotate ma non ancora effettuate, dato lo username di uno specifico paziente.
     *  @param username username del paziente. */
    List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(String username);

    /** Recupera l'elenco delle visite prenotate ed effettuate, dato lo username di uno specifico paziente.
     *  @param username username del paziente. */
    List<VisitaPrenotataDTO> getAllVisitePrenotateAndEffettuateByUsernamePaziente(String username);



}
