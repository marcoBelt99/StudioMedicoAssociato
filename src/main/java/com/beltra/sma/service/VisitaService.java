package com.beltra.sma.service;

import com.beltra.sma.dto.AppuntamentoSettimanaleMedicoDTO;
import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.exceptions.AppuntamentoNotFoundException;
import com.beltra.sma.model.*;
import com.beltra.sma.utils.SlotDisponibile;


import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VisitaService {

    /** Elenco di tutte le visite presenti nel Database. */
    List<Visita> getAll();

    /** @apiNote Riservato ad utente di tipo "MEDICO".
     * @return Recupera l'intera tabella delle Visite usufruite dai pazienti.<br>
     * La tabella e' in un formato intelliggibile (grazie al meccanismo dei DTO).  */
    List<VisitaPrenotataDTO> getAllVisite();

    /** @apiNote Riservato ad utente di tipo "MEDICO".
     * @return Recupera l'elenco di Visite usufruite dai pazienti della settimana corrente.<br>
     * La tabella e' in un formato intelliggibile (grazie al meccanismo dei DTO).  */
//    List<VisitaPrenotataDTO> getVisitePrenotateSettimana();


    List<Visita> getVisiteByAnagraficaMedico(Anagrafica anagrafica);

    /** @apiNote Riservato ad utente di tipo "MEDICO".
     *  @return  Lista di elementi di tipo AppuntamentoSettimanaleMedicoDTO.
     *  Notare che la lista è grezza e richiede di essere sistemata con le opportune conversioni prima di
     *  poter essere processato dal frontend (in questo caso, da schedule-x).*/
    List<AppuntamentoSettimanaleMedicoDTO> getAppuntamentiSettimanaliMedicoLista(String username, Date dataInizio, Date dataFine);

    /** @apiNote Riservato ad utente di tipo "MEDICO".
     *  @return  Lista di mappe, ognuna rappresentante un evento, nel nostro caso appuntamenti
     *  gia' pronto per essere processato dal frontend (in questo caso, da schedule-x).*/
    List<Map<String, Object>> getAppuntamentiSettimanaliMedicoListaMappe(String username, Date dataInizio, Date dataFine);

    /** ###############################################################################################*/
    /** ###############################################################################################*/
    /** ###############################################################################################*/
    /** ###############################################################################################*/
    /** ###############################################################################################*/
    /** ###############################################################################################*/
    /** ###############################################################################################*/
    /** ###############################################################################################*/
    /** ###############################################################################################*/


    /** @apiNote Riservato ad utente di tipo "PAZIENTE".
     *  @return Recupera l'elenco delle visite prenotate ma non ancora effettuate, dato lo username di uno specifico paziente.<br>
     *  La tabella e' in un formato intelliggibile (grazie al meccanismo dei DTO).
     *  @param username username del paziente. */
    List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(String username);


    /** @apiNote Riservato ad utente di tipo "PAZIENTE".
     *  @return Recupera l'elenco delle visite prenotate ma non ancora effettuate, dati: lo username dello specifico paziente, la data di ricerca.<br>
     *  La tabella e' in un formato intelliggibile (grazie al meccanismo dei DTO).
     *  @param username username del paziente.
     *  @param dataDiRicerca data in cui ricercare le visite
     *  */
    List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePazienteByData(String username, Date dataDiRicerca);

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
//    List<Visita> getVisiteByAnagraficaMedico(Anagrafica anagrafica);


    /** @return Elenco visite relative ad una determinata data. */
    List<Visita> getAllVisiteByData(Date data);

    List<Visita> getAllVisiteStartingFromNow();

//    List<Visita> getAllVisiteByMedicoAndData(Medico medico, Date data);



    /** (1) Crea la visita: questo metodo dovrebbe essere chiamato esclusivamente dall'opportuno
     *  Controller {@link com.beltra.sma.controller.PrenotazioneVisitaController}.<br>
     *  Questo metodo si occupa di costruire una visita, pronta per essere salvata in sessione. */
    Visita creaVisita(Prestazione prestazione, SlotDisponibile slotDisponibile);

    /** Si occupa di storicizzare la visita che prende come parametro.  */
    Visita salvaVisita(Visita visita);

    /** (2) Salva la visita: questo metodo dovrebbe essere chiamato esclusivamente dall'opportuno
     * Controller{@link com.beltra.sma.controller.PrenotazioneVisitaController}.<br>
     *   La creazione di una nuova visita: implica la creazione, nella stessa transazione, sia dell'entità Visita, che dell'entità Prenotazione.
     *   La prenotazione avrà come data la data attuale. <br>
     *   La visita avrà come data una data calcolata sulla base di vari fattori, tra cui le visite già esistenti e
     *   sulla durata della prestazione richiesta.
     *   @param prenotazione: prenotazione da salvare.
     *   @param visita: visita da salvare.
     * */
    void salvaVisitaAndPrenotazione(Visita visita, Prenotazione prenotazione );


    /** Verifica se l'utente paziente corrente ha già prenotato una visita.<br>
     *  Questo metodo risolve il "Problema delle sovrapposizioni temporali":
     *  Siano U=utente, V=visita, O=oggi, Y=orario, L=lista di visite prenotate da U in X, allora:
     *  Se U ha prenotato V per O alle Y, allora bisogna chiamare
     *      trovaSlotDisponibile() con oraPartenza=L.last.ora.calcolaOraFine()  */
    boolean utenteOggiHaGiaPrenotatoAlmenoUnaVisita(String username, Date oggi);


    /** Questo metodo risolve il "Problema delle sovrapposizioni temporali":
     *  Siano U=utente, V=visita, X=giorno, Y=orario, L=lista di visite prenotate da U in X, allora:<br>
     *  Se U ha prenotato V per X alle Y, allora bisogna chiamare
     *      trovaSlotDisponibile() con oraPartenza=L.last.ora.calcolaOraFine().plusMinutes(pausaFromVisite).
     */
//    LocalTime getRightOraDiPartenza(String username, Prestazione prestazione, Date oggi);

    /** Recupera un determinato appuntamento in base al suo id.<br>
     *  @param idAppuntamento della visita da ricercare.
     *  @return La visita cercata.
     *  @throws AppuntamentoNotFoundException Se non trova l'appuntamento desiderato.*/
    AppuntamentoSettimanaleMedicoDTO getAppuntamentoById(Long idAppuntamento, String usernameMedico, Date dataInizio, Date dataFine) throws AppuntamentoNotFoundException;


//    AppuntamentoSettimanaleMedicoDTO getAppuntamentoById(Long idAppuntamento) throws AppuntamentoNotFoundException;
}
