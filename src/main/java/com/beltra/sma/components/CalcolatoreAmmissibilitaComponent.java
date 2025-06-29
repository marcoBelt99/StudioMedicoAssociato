package com.beltra.sma.components;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/** Classe che supporta le principali operazioni di verifica e controllo sulle specifiche riguardanti Date e Orari.
 *  */
public interface CalcolatoreAmmissibilitaComponent {


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
     *  Mi serve la durata media della prestazione.
     *      Vedere che:<br>
     *       - Se ad esempio il primo orario libero e' alle
     *         18:00 e la prestazione ha durata media 30 min allora posso prenotare<br>
     *       - Se, invece, la prestazione ha durata media 30 minuti
     *         ma il primo orario libero e' alle 20:45 allora sforo e non posso.
     *  <br>
     *  Nota bene che nel calcolo si tiene conto della tolleranza di 5 minuti che è imposta tra
     *  una visita e la successiva.
     *  <br>
     *  <b>Funzionamento</b>: prima applica condizioneSoddisfacibilita ad orario e, se true verifica se e' ancora true per
     *  orario maggiorato delle durata.<br>
     *  Se entrambi sono true, allora la condizione e' accettata.
     * @param orario: ora che si vuol analizzare.
     * @param durataPrestazione: necessario per ottenere la durata media per vedere se, sommata ad orario, stiamo sforando
     *  */
    Boolean isOrarioAmmissibile(LocalTime orario, Double durataPrestazione);


    /** Metodo di supporto ad <b>isOrarioAmmissibile</b> per semplificare alcune logiche di calcolo  */
    RisultatoAmmissibilita getRisultatoCalcoloAmmissibilitaOrario(LocalTime orarioDaControllare, Double durataPrestazione);


    /**  @param ora orario da controllare
    *    @param durata durata con cui maggiorare l'orario da controllare */
    boolean isOrarioAmmissibileInMattina(LocalTime ora, Double durata);

    /** Negazione del metodo isOrarioAmmissibileInMattina: il controllo di ammissibilità mi assicura che se
     *  non è mattina, allora è pomeriggio. */
    boolean isOrarioAmmissibileInPomeriggio(LocalTime ora, Double durata);


    /** Controllo che ora sia: <br>
     *  - dopo mezzanotte <br>
     *  - prima di oraChiusuraMattina <br>
     * Non c'è controllo di ammissibilità*/
    boolean isOrarioInMattina(LocalTime ora);

    /** Controllo che ora sia: <br>
     *  - dopo oraChiusuraMattina <br>
     *  - prima di mezzanotte <br>
     * Non c'è controllo di ammissibilità*/
    boolean isOrarioInPomeriggio(LocalTime ora);

    /** Controlla che ora sia dopo oraAperturaPomeriggio<br>
     * e prima di mezzanotte */
    boolean isOrarioAfterAperturaPomeriggio(LocalTime ora);

    /** Controlla che ora sia dopo ora chiusura pomeriggio.
     *
     *  */
    boolean isOrarioAfterChiusuraPomeriggio(LocalTime ora);


    /** Verifica che durataMedia sia compreso tra (oraFine-orarioChiusura) */
    boolean isDurataMediaContenuta(Double durataMedia, LocalTime oraFine, LocalTime orarioChiusura);


    /** Metodo di utilità che ritorna il primo giorno ammissibile.<br>
     * @param dataDiPartenza data da analizzare e, se non ammissibile (cioè se è sabato o domenica) da incrementare.
     * @param calendar necessario per effettuare l'eventuale incremento di data.
     * @return il successivo giorno ammissibile, rispetto a dataDaControllare. */
    Date findNextGiornoAmmissibile(Date dataDiPartenza, Calendar calendar);

}
