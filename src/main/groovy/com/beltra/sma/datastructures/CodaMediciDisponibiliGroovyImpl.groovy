package com.beltra.sma.datastructures

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent
import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl
import com.beltra.sma.components.RisultatoAmmissibilita
import com.beltra.sma.model.Medico
import com.beltra.sma.model.Visita
import com.beltra.sma.utils.FineVisita
import com.beltra.sma.utils.Parameters
import lombok.Getter
import lombok.Setter

import java.sql.Time
import java.time.LocalTime
import java.util.function.Function;

class CodaMediciDisponibiliGroovyImpl implements CodaMediciDisponibili{

    /** Coda di priorità basata su FineVisita.getOraFine().<br>
     *  Si deve generare tramite <b>mediciMap</b>, con il metodo builQueue().<br>
     *
     *  */
    @Setter
    private Queue<Map.Entry<Medico, FineVisita>> mediciQueue;


    /** Mappa per legare ad ogni medico del sistema il rispettivo orario di fine visita. */
    @Getter
    @Setter

    private Map<Medico, FineVisita> mediciMap;


    private final CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilita;

    CodaMediciDisponibiliGroovyImpl(List<Medico> listaMedici, List<Visita> listaVisite, LocalTime oraAttuale,
                                     Double durataMediaNuovaVisita) {

        calcolatoreAmmissibilita = new CalcolatoreAmmissibilitaComponentImpl();

        mediciMap = new HashMap<>();

        // Gestione dei comparatori per le priorità della coda: prima voglio ordinare per valore delle entry della mappa (per ora fine).
        // Poi in caso ottenga due orari uguali (ad esempio <1, 20:35> e <2, 20:35>) ordino per chiave delle entry della mappa.
        Function<Map.Entry<Medico, FineVisita>, Time> valueExtractor = { entry -> entry.value.oraFine } as Function
        Function<Map.Entry<Medico, FineVisita>, Long> keyExtractor = { entry -> entry.key.idAnagrafica } as Function

        Comparator<Map.Entry<Medico, FineVisita>> comparator = Comparator
                .comparing(valueExtractor)
                .thenComparing(keyExtractor)

        mediciQueue = new PriorityQueue<>(comparator)

        // Soluzione funzionante:
        //mediciQueue = new PriorityQueue<>(Map.Entry.comparingByValue())



        build(listaVisite, listaMedici, durataMediaNuovaVisita);

    }

    @Override
    void buildMap(List<Visita> listaVisite, List<Medico> listaMedici, Double durataMediaNuovaVisita) {
    }

    @Override
    void buildQueue() {
    }


    @Override
    Medico getPrimoMedicoDisponibile(Double durataMediaNuovaVisita) {
        if (mediciQueue.isEmpty()) return null
        Map.Entry<Medico, FineVisita> entry = mediciQueue.peek() // non poll(), solo peek!
        return entry.key
    }

    @Override
    Map<Medico, FineVisita> getMediciMap() {
        return Collections.unmodifiableMap(mediciMap);
    }

    @Override
    Queue<Map.Entry<Medico, FineVisita>> getMediciQueue() {
        return this.mediciQueue;
    }


    /** Funzione builder. */
    private void build(List<Visita> listaVisite, List<Medico> listaMedici, Double durataMedia) {
        if (!listaVisite || !listaMedici) return

        // Caso lista visite < lista medici
        listaVisite
                .take(listaMedici.size())
                .withIndex()
                .each { visita, i ->
                    Medico medico = listaMedici[i] // vado in ordine di medico

                    // TODO: calcolo ammissibilita' oraria:
                    durataMedia = visita.getPrestazione().getDurataMedia()
                    Time oraFine = getRightOraFine(visita.ora.toLocalTime(),durataMedia )

                    FineVisita fineVisita = new FineVisita(visita.getIdVisita(), oraFine)

                    this.@mediciMap[medico] =  fineVisita //oraFine // la @ serve per esplicitare di riferirsi al campo anzichè al getter del campo
                    this.@mediciQueue.add( Map.entry(medico, fineVisita) )
                }

        // considero le altre visite
        listaVisite
                .drop(listaMedici.size())
                .each { visita ->

                    // TODO: 1) Trovo l'elemento avente oraFine minore grazie alla coda di priorita'
                    def entry = mediciQueue.poll()
                    this.@mediciQueue.remove( this.@mediciMap[entry.key] ) // lo rimuovo subito dalla coda
                    Medico medicoKey = entry.key

                    //  TODO: 2) Aggiorno la mappa con la nuova oraFine per il medico appena estratto
                    // TODO: 2-a) calcolo ammissibilita'
                    durataMedia = visita.getPrestazione().getDurataMedia()
                    Time nuovaOraFine = getRightOraFine(visita.ora.toLocalTime(), durataMedia)

                    // TODO: 2-b) inserimento in mappa
                    FineVisita fineVisita = new FineVisita( visita.getIdVisita(), nuovaOraFine )
                    this.@mediciMap[medicoKey] = fineVisita

                    // TODO: 3) Ri-aggiungo l'elemento in coda
                    Map.Entry<Medico, FineVisita> entryAggiornata = mediciMap.find { it -> it.key.idAnagrafica == medicoKey.idAnagrafica}
                    this.@mediciQueue.add( entryAggiornata  )

                }
    }



    // Questo metodo potrebbe dover richiedere un 2° passaggio da un altro metodo per eseguire ulteriori controlli
    // che non sono catturati da getRisultatoCalcolatoreAmmissibilitaOrario()
    //
    private Time getRightOraFine(LocalTime oraDaControllare, Double durataMediaNuovaVisita) {
        def durata = durataMediaNuovaVisita.intValue()
        def risultato = calcolatoreAmmissibilita.getRisultatoCalcoloAmmissibilitaOrario(oraDaControllare, durataMediaNuovaVisita)

        def oraFine = switch (risultato) {
            case RisultatoAmmissibilita.AMMISSIBILE -> yield oraDaControllare.plusMinutes(durata)

            case RisultatoAmmissibilita.NO_BECAUSE_BEFORE_APERTURA_MATTINA ->
                yield Parameters.orarioAperturaMattina
                        .plusMinutes(Parameters.pausaFromvisite)
                        .plusMinutes(durata)

            case RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO ->
                // Da migliorare (questa e' la casistica se sono esattamente le 21:00) allora non passare a mettere l'orario del mattino ....
                yield oraDaControllare.plusMinutes(durata).isAfter(Parameters.orarioChiusuraPomeriggio) // se oraDaControllare.plusMinutes(durata) == 21:00 allora la isAfter da false
                    ? Parameters.orarioAperturaMattina
                        .plusMinutes(Parameters.pausaFromvisite)
                        .plusMinutes(durata)
                        // TODO: in caso stia sforando la chiusura del pomeriggio allora posso pensare
                        //   di richiamare ricorsivamente build() ?? ==> in questo modo mi baso sulle visite del primo giorno ammissibile
                    : oraDaControllare.plusMinutes(durata)

            case RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ->
                yield Parameters.orarioAperturaPomeriggio
                        .plusMinutes(Parameters.pausaFromvisite)
                        .plusMinutes(durata)

            default -> throw new IllegalArgumentException("Tipo di risultato non gestito: $risultato")
        }

        return Time.valueOf(oraFine)
    }


}