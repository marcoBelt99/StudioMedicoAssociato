package com.beltra.sma.components;

import com.beltra.sma.model.Prestazione;
import com.beltra.sma.utils.SlotDisponibile;


import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
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
     * Logica per trovare lo slot disponibile: considera l'orario di apertura e chiusura dello SMA e calcola gli intervalli
     * disponibili tra le visite esistenti.
     * @param durata durata media della prestazione a cui la visita che si creando fa riferimento.
     * @param dataInizio giorno da cui iniziare la ricerca per lo slot.
     * @return slot disponibile: una tripla composta da Data, Orario, Medico.
     * */
    Optional<SlotDisponibile> trovaPrimoSlotDisponibile(Double durata, Date dataInizio);

    /**
     * @param  data giorno che si vuol analizzare: vogliamo che sia diverso da Sabato e Domenica.<br>
     * @return true Se il giorno che si sta analizzando deve essere compreso tra Lunedi' e Venerdi'.
     */
    Boolean isGiornoAmmissibile(Date data);


    /** Condizione di verifica sforamento dagli orari di lavoro dello Studio Medico Associato. */
    Boolean condizioneSoddisfacibilita(LocalTime orarioDaControllare);


    /**
     *  Verifica se l'Orario e' ammissibile' in base agli orari dello SMA e alla durata media prevista della prestazione:
     *
     *  Lo SMA lavora dalle 07:00 alle 12:00 e dalle 14:00 alle 21:00
     *      orario compreso in quell'intervallo.
     *
     *  Mi serve la durata prevista della prestazione:
     *      vedere che:
     *       - se ad esempio il primo orario libero e' alle
     *         18:00 e la prestazione ha durata media 30 min allora posso prenotare
     *       - se invece la prestazione ha durata media 30 minuti
     *         ma il primo orario libero e' alle 20:45 allora sforo e non posso
     * @param orario: ora che si vuol analizzare.
     * @param prestazione: necessario per ottenere la durata media per vedere se, sommata ad orario, stiamo sforando
     *  */
    Boolean isOrarioAmmissibile(Time orario, Prestazione prestazione);



    LocalTime aggiungiDurata(LocalTime ora, Double durataMedia);



}
