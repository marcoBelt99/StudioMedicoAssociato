package com.beltra.sma.components;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.Parameters;
import com.beltra.sma.utils.SlotDisponibile;


import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/** Le operazioni presenti in questa interfaccia sono di supporto per la corretta gestione delle visite.
 *  <br>
 *  L'operazione (di ricerca) piu' importante e' descritta dal metodo <b>trovaSlotDisponibile()</b>.*/
public interface PianificazioneComponent {

    /** Orari di apertura e chisura dello SMA */
    LocalTime orarioAperturaMattina =    Parameters.orarioAperturaMattina;
    LocalTime orarioChiusuraMattina =    Parameters.orarioChiusuraMattina    ;
    LocalTime orarioAperturaPomeriggio = Parameters.orarioAperturaPomeriggio ;
    LocalTime orarioChiusuraPomeriggio = Parameters.orarioChiusuraPomeriggio ;


    /** Tolleranza / Pausa in minuti tra una visita e l'altra. */
    long pausaFromvisite = Parameters.pausaFromVisite;



    /** Sorta di "Strategy Pattern", che mi consente di cambiare il comportamento di un oggetto a runtime, evitando l'uso massiccio di if.<br>
     *  Quando vuoi rendere il codice più flessibile e testabile.<br>
     *  Va richiamato nel Controller della Prenotazione.
     * */
    Optional<SlotDisponibile> trovaSlotDisponibileConControlliPaziente(Double durata,
                                                                       Date dataAttuale,
                                                                       LocalTime oraAttuale,
                                                                       List<Medico> listaMedici,
                                                                       String usernamePazienteCorrente
    );





    /**
     * Logica per trovare, ricorsivamente, lo slot disponibile: considera gli orari di apertura e chiusura dello SMA e calcola gli intervalli
     * disponibili tra le visite esistenti.<br>
     * Va richiamato sia nel Controller della Prenotazione, per poter stamapare a video e in HTML nello stepper lo SlotDisponibile,
     * Sia in fase di Creazione Visita (e prenotazione).
     * @param durata durata media della prestazione a cui fa riferimento la visita che si sta creando.
     * @param dataAttuale data di partenza da cui iniziare a cercare lo slot.
     * @param oraAttuale ora attuale di riferimento (dalla quale iniziare la ricerca).
     * @param listaMedici elenco di tutti i medici del sistema su cui ricercare il prossimo medico disponibile.
     * @param visiteGiornaliere lista di visite presenti in una determinata data.
     * @return slot disponibile: una tripla composta da Data, Orario, Medico.
     * */
    Optional<SlotDisponibile> trovaSlotDisponibile(Double durata,
                                                   Date dataAttuale,
                                                   LocalTime oraAttuale,
                                                   List<Medico> listaMedici,
                                                   List<Visita> visiteGiornaliere);

    List<Visita> getAllVisiteByData(Date dataCorrente);




    Optional<SlotDisponibile> trovaSlotGiornoSuccessivo(Calendar calendar,
                                                        Double durataMedia,
                                                        List<Medico> listaMedici);

}
