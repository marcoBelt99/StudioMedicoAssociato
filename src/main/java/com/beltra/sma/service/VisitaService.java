package com.beltra.sma.service;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.*;
import com.beltra.sma.repository.VisitaRepository;
import com.beltra.sma.utils.SlotDisponibile;


import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VisitaService {

    /** Elenco di tutte le visite presenti nel Database. */
    List<Visita> getAll();

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


    /** @return Elenco visite prenotate ma non ancora effettuate di tutti i pazienti. */
    List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuate();


    /** @return Elenco visite ordinate dalla più recente alla meno. */
    List<Visita> getAllVisiteOrderByDataVisitaAsc();


    /** @param anagrafica Anagrafica del medico da usare per la ricerca.
     * @return Lista di visite che fanno match con il medico.
     */
    List<Visita> getVisiteByAnagraficaMedico(Anagrafica anagrafica);


    /** @return Elenco visite relative ad una determinata data. */
    List<Visita> getAllVisiteByData(Date data);

    List<Visita> getAllVisiteStartingFromNow();

    List<Visita> getAllVisiteByMedicoAndData(Medico medico, Date data);


    /** Creazione di una nuova visita: implica la creazione nella stessa transazione sia dell'entità Visita, che dell'entità Prenotazione.
     *  La prenotazione avrà come data la data attuale.
     *  La visita avrà come data una data calcolata sulla base di vari fattori, tra cui le visite già esistenti e
     *  sulla durata della prestazione richiesta.
     * @param prestazione: definisce l'oggetto della visita, descrive lo scopo della visita
     * */
    Visita createVisita( String usernameUtente, Prestazione prestazione );


    Visita salvaVisita( Visita visita );

}
