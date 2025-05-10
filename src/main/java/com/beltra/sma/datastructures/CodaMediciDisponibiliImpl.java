package com.beltra.sma.datastructures;

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.FineVisita;
import com.beltra.sma.utils.Parameters;
import lombok.Getter;
import lombok.Setter;


import java.sql.Time;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;


@Deprecated
public class CodaMediciDisponibiliImpl implements CodaMediciDisponibili {


    /** Coda di priorità basata su FineVisita.getOraFine().<br>
     *  Si deve generare tramite <b>mediciMap</b>, con il metodo builQueue().<br>
     *
     *  */
    @Getter
    @Setter
    private Queue<Map.Entry<Medico, FineVisita>> mediciQueue;


    /** Mappa per legare ad ogni medico del sistema il rispettivo orario di fine visita. */

    @Setter
    private Map<Medico, FineVisita> mediciMap;


    private final CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilita;

    // Costruttore
    public CodaMediciDisponibiliImpl(List<Medico> listaMedici, List<Visita> listaVisite, LocalTime oraAttuale,
                                     Double durataMediaNuovaVisita) {

        calcolatoreAmmissibilita = new CalcolatoreAmmissibilitaComponentImpl();

        mediciMap = new HashMap<>();
        mediciQueue = new PriorityQueue<>(Map.Entry.comparingByValue());

//        /// 1) costruisco ed inizializzo mediciMap sulla base di listaVisite
//        buildMap(listaVisite, listaMedici, durataMediaNuovaVisita);
//        // listaVisite.stream().reduce( List.empty(),  buildMap(listaVisite, listaMedici, durataMediaNuovaVisita) );
//
//        /// 2) costruisco coda di priorità sulla base di mediciMap
//        buildQueue();

        assegnaVisite(listaVisite, listaMedici, durataMediaNuovaVisita);

    }


    @Override
    public void buildMap(List<Visita> listaVisite, List<Medico> listaMedici, Double durataMediaNuovaVisita) { // Map<Medico, FineVisita>

        // Caso base: lista visite vuota
        if(listaVisite.isEmpty())
            listaMedici.forEach( m -> mediciMap.put(m, new FineVisita(0L, null))); // per convenzione non gli ho ancora assegnato una visita, dato che non ne esistono ancora

        // Domanda: se sono vuote sia listaVisite che listaMedici?

        if(mediciMap.isEmpty() && !listaMedici.isEmpty() )
            zipVisiteMedici(listaVisite, listaMedici);

        // TODO: Devo fare un controllo su mediciMap vedendo se è vuota o meno e comportarmi di conseguenza
        if(!mediciMap.isEmpty() && (listaVisite.size() < listaMedici.size())) {
           zipVisiteMedici(listaVisite, listaMedici);

           // ai medici rimanenti assegno un FineVisita vuoto
           listaMedici.stream()
                   .skip(listaVisite.size())
                   .forEach(m -> mediciMap.put(m, new FineVisita(0L, null))); // per convenzione non gli ho ancora assegnato una visita, dato che si fermano a listaVisite.size() e che listaMedici.size() > listaVisite.size()
        }
    }

    /** Genera mediciMap sulla base di listaVisite */
    private void zipVisiteMedici(List<Visita> listaVisite, List<Medico> listaMedici) {

        // Soluzione in stile funzionale simulando la HOF zip
        IntStream
                .range(0, Math.min(listaMedici.size(), listaVisite.size())) // prende l'indice minimo delle due liste
                // TODO: inserimento in mappa
                .forEach(i -> mediciMap.put(listaMedici.get(i),
                        new FineVisita(listaVisite.get(i).getIdVisita(),

                         //listaVisite.get(i).calcolaOraFine()))); // prima avevo solo questo
                         getRightOraFine( listaVisite.get(i).getOra().toLocalTime(),
                                 listaVisite.get(i).getPrestazione().getDurataMedia()) ) )
                         );
    }

    // Questo metodo potrebbe dover richiedere un 2° passaggio da un altro metodo per eseguire ulteriori controlli
    // che non sono catturati da getRisultatoCalcolatoreAmmissibilitaOrario()
    //
    private Time getRightOraFine(LocalTime oraDaControllare, Double durataMediaNuovaVisita) {
        return
                Time.valueOf(
                        switch ( calcolatoreAmmissibilita.getRisultatoCalcoloAmmissibilitaOrario(oraDaControllare, durataMediaNuovaVisita) ) {
                            case AMMISSIBILE -> // calcola l'orario di fine
                                    oraDaControllare
                                            .plusMinutes(durataMediaNuovaVisita.intValue());
                            case NO_BECAUSE_BEFORE_APERTURA_MATTINA, NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO -> // parti dalla mattina e aggiungi la durata di fine visita
                                    Parameters.orarioAperturaMattina
                                            .plusMinutes(Parameters.pausaFromvisite)
                                            .plusMinutes(durataMediaNuovaVisita.intValue());

                            case NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ->
                                    Parameters.orarioAperturaPomeriggio
                                            .plusMinutes(Parameters.pausaFromvisite)
                                            .plusMinutes(durataMediaNuovaVisita.intValue());
                        }
                );
    }



    @Override
    public void buildQueue() { // PriorityQueue<Map.Entry<Medico, FineVisita>>
        /** Gli elementi della coda sono ordinati sulla base del valore degli elementi della mappa */

        // Meglio scritto così piuttosto che fare mediciQueue.addAll( entry_medici_map ). Così consumo meno memoria

        mediciMap.forEach( (medico, fineVisita) -> {
            if (fineVisita.getOraFine() != null)
                mediciQueue.offer(Map.entry(medico, fineVisita));
        });
    }


    @Override
    public Map.Entry<Medico, FineVisita> getPrimoMedicoDisponibile(Double durataMediaNuovaVisita) {

        //return mediciQueue.isEmpty() ? null : mediciQueue.peek().getKey();
        if (mediciQueue.isEmpty())
            return null;


        // Estraggo il medico con il FineVisita attuale
        Map.Entry<Medico, FineVisita> entry = mediciQueue.poll();

        Medico medicoEstratto = entry.getKey();

        // TODO: Aggiorno il valore di FineVisita in mediciMap (simulando una nuova visita)
        FineVisita nuovoFineVisita = aggiornaOraFineVisita(medicoEstratto, durataMediaNuovaVisita);
        mediciMap.put(medicoEstratto, nuovoFineVisita);

        // TODO: Reinserisco il medico aggiornato nella coda
        entry = Map.entry(medicoEstratto, nuovoFineVisita);
        mediciQueue.offer(entry);

        return entry;
    }

    private FineVisita aggiornaOraFineVisita(Medico medico, Double durataMediaNuovaVisita) {
        // Qui puoi usare la logica che preferisci per calcolare il nuovo orario
        // Ad esempio, sommare una nuova durata media di visita a quella attuale
        FineVisita attuale = mediciMap.get(medico);
        LocalTime nuovoOrario = attuale.getOraFine().toLocalTime().plusMinutes(durataMediaNuovaVisita.intValue()); // esempio
        return new FineVisita(attuale.getIdVisita() + 1,
                getRightOraFine(nuovoOrario, durataMediaNuovaVisita) );
    }


    public Map<Medico, FineVisita> getMediciMap() {
        return Collections.unmodifiableMap(mediciMap);
    }


    public void assegnaVisite(List<Visita> listaVisite, List<Medico> listaMedici, Double durataMediaNuovaVisita) {
        for (int i = 0; i < listaVisite.size(); i++) {
            if (i < listaMedici.size()) {
                // Primo giro: assegno le visite direttamente in ordine
                Medico medico = listaMedici.get(i);
                Visita visita = listaVisite.get(i);

                FineVisita fineVisita = new FineVisita(visita.getIdVisita(),
                        getRightOraFine(visita.getOra().toLocalTime(), durataMediaNuovaVisita));

                mediciMap.put(medico, fineVisita);
            } else {
                // Giri successivi: prendo il medico con FineVisita più vicino e lo aggiorno
                Medico medicoDisponibile = getPrimoMedicoDisponibile(durataMediaNuovaVisita).getKey();

                // opzionalmente: salva anche l'associazione visita-medico, se ti serve altrove
            }
        }

        // Ricostruisci la coda dopo aver inizializzato la mappa
        buildQueue();
    }

}
