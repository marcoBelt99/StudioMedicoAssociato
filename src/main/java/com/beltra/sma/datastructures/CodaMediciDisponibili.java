package com.beltra.sma.datastructures;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.FineVisita;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Queue;


public interface CodaMediciDisponibili {



//    /** A partire da listaVisite,costruisce mediciMap, che e' composta da coppie <K, V> del tipo:<br>
//     * <{@link Medico}, {@link FineVisita}> come segue:<br>
//     *
//     * @param listaMedici da cui inizializzare <b>mediciMap</b>
//     * @param listaVisite da cui basarsi per calcolare il prossimo medico disponibile
//     * */
//    void buildMap(List<Visita> listaVisite, List<Medico> listaMedici, Double durataMediaNuovaVisita ); // Map<Medico, FineVisita>
//
//
//
//    /** Costruisce <b>mediciQueue</b> a partire da mediciMap. <br>
//     *  Ogni elemento della coda e' una entry di mediciMap.
//     *  */
//    void buildQueue(); // PriorityQueue<Map.Entry<Medico, FineVisita>>



    /** Estrae il primo elemento dalla coda dei medici, aggiornando opportunamente le altre strutture dati di supporto. */
    Map.Entry<Medico, FineVisita> getPrimoMedicoDisponibile(Double durataMediaNuovaVisita);


    /** Ritorna una versione non modificabile di mediciMap */
    Map<Medico, FineVisita> getMediciMap();

    /** Ritorna una versione non modificabile di mediciQueue */
    Queue<Map.Entry<Medico, FineVisita>> getMediciQueue();

}
