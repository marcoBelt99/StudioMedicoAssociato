package com.beltra.sma.service;

import com.beltra.sma.dto.VisitaPrenotataDTO;


import java.util.List;

public interface VisitaService {

    /** @apiNote Riservato ad utente di tipo "MEDICO".
     * @return Recupera l'intera tabella delle Visite usufruite dai pazienti.<br>
     * La tabella e' in un formato intelliggibile (grazie al meccanismo dei DTO).  */
    List<VisitaPrenotataDTO> getAllVisite();

    /** @apiNote Riservato ad utente di tipo "PAZIENTE".
     *  @return Recupera l'elenco delle visite prenotate ma non ancora effettuate, dato lo username di uno specifico paziente.<br>
     *  La tabella e' in un formato intelliggibile (grazie al meccanismo dei DTO).
     *  @param username username del paziente. */
    List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(String username);

    /** @apiNote Riservato ad utente di tipo "PAZIENTE".
     *  @return Recupera l'elenco delle visite prenotate ed effettuate, dato lo username di uno specifico paziente.<br>
     *  La tabella e' in un formato intelliggibile (grazie al meccanismo dei DTO).
     *  @param username username del paziente. */
    List<VisitaPrenotataDTO> getAllVisitePrenotateAndEffettuateByUsernamePaziente(String username);



}
