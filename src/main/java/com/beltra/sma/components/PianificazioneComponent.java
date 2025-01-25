package com.beltra.sma.components;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.SlotDisponibile;


import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/** Le operazioni presenti in questa interfaccia sono di supporto per la corretta gestione delle visite.
 *  <br>
 *  L'operazione (di ricerca) piu' importante e' descritta dal metodo <b>trovaPrimoSlotDisponibile()</b>.*/
public interface PianificazioneComponent {

    /** Orari di apertura e chisura dello SMA */
    LocalTime orarioAperturaMattina =    LocalTime.of(7, 0);
    LocalTime orarioChiusuraMattina =    LocalTime.of(12, 0);
    LocalTime orarioAperturaPomeriggio = LocalTime.of(14, 0);
    LocalTime orarioChiusuraPomeriggio = LocalTime.of(21, 0);


    /** Tolleranza / Pausa in minuti tra una visita e l'altra. */
    long pausaFromvisite = 5;



    /**
     * Logica per trovare lo slot disponibile: considera gli orari di apertura e chiusura dello SMA e calcola gli intervalli
     * disponibili tra le visite esistenti.<br>
     *  Va richiamato sia nel Controller della Prenotazione, per poter stamapare a video e in HTML nello stepper lo SlotDisponibile,
     *  Sia in fase di Creazione Visita (e prenotazione).
     * @param durata durata media della prestazione a cui la visita che si creando fa riferimento.
     * @param dataAttuale data di partenza da cui iniziare a cercare lo slot.
     * @param oraAttuale ora attuale di riferimento (dalla quale iniziare la ricerca).
     * @param listaMedici elenco di tutti i medici del sistema su cui ricercare il prossimo medico disponibile.
     * @param visiteGiornaliere lista di visite presenti in una determinata data.
     * @return slot disponibile: una tripla composta da Data, Orario, Medico.
     * */
    Optional<SlotDisponibile> trovaPrimoSlotDisponibile(Double durata,
                                                        Date dataAttuale,
                                                        LocalTime oraAttuale,
                                                        List<Medico> listaMedici,
                                                        List<Visita> visiteGiornaliere);



    List<Visita> getAllVisiteByData();

}
