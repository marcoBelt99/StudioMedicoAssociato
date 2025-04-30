package com.beltra.sma.datastructures;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.FineVisita;

import java.util.*;

/** Data una <b>lista di visite</b>, consente di trovare il medico che si libera per primo.
 *  <br>
 *  La ricerca si basa sull'orario di fine visita di ogni visita della lista.
 *  La <b>mediciMap</b> tiene traccia dei medici del sistema, e ad ogni volta che viene inserita una visita in listaVisite,
 *  viene aggiornato il corrispondente "orario di fine occupazione" legato al medico.
 *  <br>
 *  Visto che quando si inserisce una nuova visita a listaVisite c'è bisogno di associarle anche un medico, sara'
 *  necessario aggiornare anche l'orario di fine occupazione del medico che si è scelto di associare alla nuova visita.
 *
 *  */
public interface Pianificatore {

    long pausa5Minuti = 5;

    PriorityQueue<FineVisita> codaMedici  = new PriorityQueue<>(Comparator.comparing(FineVisita::getOraFine));


    /**  Aggiunge una visita sia in listaVisite sia nella mappa <br>
     *   L'ordine di inserimento e' importante: e' obbligatorio inserire prima in lista
     *   e successivamente in Mappa.*/
    void aggiungiVisita(Visita visita);


    /** Aggiorno la mappa mediciMap sulla base delle visite appena inserite */
    void aggiornaMediciMap();


    /** @param Vi Nuova Visita da inserire.
     * @param data Data Visita (a partire da oggi).
     *  <br>
     *   #########################<br>
     *   ** METODO ORIGINALE **<br>
     *   #########################<br>
     *  Costruisce Vi sulla base di Vx: <br>
     *  Vx = visita su cui ci si sta basando (e' quella con orario di fine minimo) */
    Visita pianificaNuovaVisita(Visita Vi, Date data, Prestazione prestazione);


    /** Ricerca il primo medico disponibile utilizzando la struttura dati.*/
    Medico getPrimoMedicoDisponibile(List<Visita> listaVisiteGiornaliere, List<Medico> listaMedici);



    /** Restituisce la lista di tutte le visite pianificate. */
    List<Visita> getListaVisite();

    /** Setta la lista di visite con  listaVisite */
    void setListaVisite(List<Visita> listaVisite);

    /**  Restituisce la mappa di tutti i medici disponibili in base agli orari di fine delle varie visite*/
    Map<Long, FineVisita> getMediciMap();

    /** Stampa le informazioni presenti nelle due strutture dati. */
    void stampaListaAndMappa();

    /** Pulizia strutture dati. Necessario per eseguire gli unit test */
    void clear();


    long getPausa5Minuti();

}
