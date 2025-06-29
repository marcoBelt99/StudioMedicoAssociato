package com.beltra.sma.groovy.datastructures

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent
import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl
import com.beltra.sma.components.RisultatoAmmissibilita
import com.beltra.sma.datastructures.CodaMediciDisponibili
import com.beltra.sma.model.Medico
import com.beltra.sma.model.Prestazione
import com.beltra.sma.model.Visita
import com.beltra.sma.service.VisitaService

import com.beltra.sma.utils.FineVisita
import com.beltra.sma.utils.Parameters
import lombok.Getter
import lombok.Setter


import java.sql.Time
import java.time.LocalTime
import java.util.function.Function



class CodaMediciDisponibiliGroovyImpl implements CodaMediciDisponibili {


    /** Mappa per legare ad ogni medico del sistema il rispettivo orario di fine visita. */
    @Getter
    @Setter
    private Map<Medico, FineVisita> mediciMap

    /** Coda di priorità basata su FineVisita.getOraFine().<br>
     *  Fa uso di <b>mediciMap</b><br>
     *  */
    @Setter
    private Queue<Map.Entry<Medico, FineVisita>> mediciQueue


    // Componenti necessari. visitaService e' necessario per cercare le visite a database
    private final CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilita
    private final VisitaService visitaService

    // Lista visite su cui basarsi per il calcolo del primo medico disponibile
    List<Visita> listaVisite
    List<Medico> listaMedici

    // Attributi da definire meglio
    private final LocalTime oraAttuale
    private final Double durataMediaNuovaVisita



    /** Nel costruttore, e' necessario anche il visitaService per cercare le visite a database. */
    CodaMediciDisponibiliGroovyImpl(List<Medico> listaMedici, List<Visita> listaVisite,
                                    LocalTime oraAttuale, Double durataMediaNuovaVisita,
                                    VisitaService visitaService) {

        /** Inizializzazione di mediciMap */
        mediciMap = new HashMap<>()

        /** Inizializzazione di mediciQueue */
        // Gestione dei comparatori per le priorità della coda:
        // Prima voglio ordinare per valore delle entry della mappa (per ora fine).
        // Poi, in caso ottenga due orari uguali (ad esempio <1, 20:35> e <2, 20:35>) ordino per chiave delle entry della mappa.
        Function<Map.Entry<Medico, FineVisita>, Time> valueExtractor = { entry -> entry.value.oraFine } as Function
        Function<Map.Entry<Medico, FineVisita>, Long> keyExtractor = { entry -> entry.key.idAnagrafica } as Function

        Comparator<Map.Entry<Medico, FineVisita>> comparator = Comparator
                .comparing(valueExtractor)
                .thenComparing(keyExtractor)

        mediciQueue = new PriorityQueue<>(comparator)

        /** Inizializzazione degli elementi che servono */
        calcolatoreAmmissibilita = new CalcolatoreAmmissibilitaComponentImpl()
        this.listaVisite = listaVisite
        this.listaMedici = listaMedici
        this.oraAttuale = oraAttuale
        this.durataMediaNuovaVisita = durataMediaNuovaVisita
        this.visitaService = visitaService

        /** Chiamata al metodo delegato della costruzione (rispettando i guisti ordini) della coda. */
        // Questa è la build che dovrebbe tener conto dell'ammissibilità
        // build( valutaNextGiornoAmmissibile( durataMediaNuovaVisita) ? getListaVisiteGiornaliereNotAmmissibiliAndVisiteNextGiornoAmmissibile(durataMediaNuovaVisita) : listaVisite, listaMedici, durataMediaNuovaVisita)

        // Pezzo che c'era prima:
        build(listaVisite, listaMedici, durataMediaNuovaVisita)

    }




    @Override
    Map.Entry<Medico, FineVisita> getPrimoMedicoDisponibile(Double durataMediaNuovaVisita) {

        /** Caso base: lista visite vuota ma lista medici non lo è, ritornami sempre il primo medico! */
        if(listaVisite.empty && !listaMedici.empty) return Map.entry(listaMedici[0], new FineVisita()) //  new FineVisita() perche', visto che ci sono 0 visite inserite allora non ha senso basarsi sugli orari delle visite,
        // dato che non ce ne sono ancora

             // TODO: Testare se sia lista visite che lista medici sono vuote cosa succede.
            // return mediciQueue.empty ? null : mediciQueue.peek() // originale


        /// ############################################################################
        /// Devo ricostruire la coda basandomi sulle visite presenti nel primo giorno utile ammissibile!
        ///      se listaVisiteProssimoGiornoAmmissibile = [] allora assegna m1 a v1
        ///      se listaVisiteGiornoAmmissibile = [v1] allora assegna m2 a v2
        ///      se listaVisiteGiornoAmmissibile = [v1, v2] allora assegna m3 a v3
        ///      se listaVisiteGiornoAmmissibile = [v1, v2, v3] allora assegna chi si libera prima a v4
        ///  DOMANDA: come faccio a recuperare il prossimo giorno disponibile?
        ///  RISPOSTA: devo modificare la firma del costruttore di questa classe per fare in modo di ottenere dal chiamante
        ///  anche la data attuale, e poi usare il CalcolatoreAmmissibilitaComponent che vada a verificare che tale data attuale sia ammissibile
        ///  (al 99% dovrebbe esserlo, visto che prima si passa dal componente PianificazioneComponentImpl)
        ///  se non lo è, devo:
        ///   1) recuperare la lista delle visite del primo giorno utile ammissibile. Suppongo si chiami listaVisiteNew
        ///   2)  usare tale lista per fare coda = new CodaMediciDisponibiliGroovyImpl(listaVisiteNew, ...)

        // DOMANDA: e se oltre a V20 ho anche V21, V22 ??
        // RISPOSTA: forse devo richiamare così: coda = new CodaMediciDisponibiliGroovyImpl( listaVisiteNew.append( V21 ), ...)
        //  e poi listaVisiteN

// #################################################################################################
// #################################################################################################
// #################################################################################################
// #################################################################################################

    // TODO: Check sforamento: sono nel caso in cui ho le visite fino a V19 (compreso) e sto inserendo V20 che avrebbe durata 180 min
    // e nessun medico presente in coda ha come ora fine un orario che, sommato a V20.durata sia ammissibile, quindi richiamo la build, usando le visite del giorno successivo
    // usando una visita "fittizia"


        Map.Entry<Medico, FineVisita> risultato = mediciQueue.empty ? null : mediciQueue.peek()

        if(risultato == null)
            return null

        // Tutto sta nell'analizzare la variabile risultato.
        //  Infatti, se questa entry è al di fuori di ora chiusura pomeriggio
        if(calcolatoreAmmissibilita.getRisultatoCalcoloAmmissibilitaOrario(risultato.getValue().getOraFine().toLocalTime(), durataMediaNuovaVisita) == RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO )  {
            // Ricostruisci la coda, usando come base di conoscenza la lista di visite del prossimo giorno utile ammissibile a cui accodo
            // la nuova visita "fittizia".
            // Nota bene, si parla di singola visita fittizia, perchè il chiamante si dovrebbe sempre riferire alla singola visita:
            // Infatti, quando PianificazioneComponent chiama codaMediciDisponibili.getPrimoMedicoDisponibile(), la chiamata avviene in fase di pianificazione
            // della singola visita, e si sta proprio cercando il primo medico libero da assegnare alla futura nuova visita.


            // 1) becca il primo giorno disponibile:
            // 2) Creo la nuova visita "fittizia", che richiede a sua volta anche una prestazione fittizia, che abbia almeno una durataMedia.

            // TODO: questa e' la lista originale
            // 11/06/2025: Ho un errore non gestito questa lista è vuota
            List<Visita> listaVisiteNextGiornoAmmissibile = getListaVisiteNextGiornoAmmissibile()

            // Nota bene che se nel successivo giorno ammissibile non ho visite, allora creando la seguente visita (con la sua relativa prestazione),
            // risolvo il problema della lista vuota

            Prestazione prestazioneFittizia = new Prestazione()
            prestazioneFittizia.setIdPrestazione(0L)
            prestazioneFittizia.setDurataMedia(durataMediaNuovaVisita)
            Visita visitaFittizia = new Visita()
            visitaFittizia.setIdVisita(0L)

            // TODO: se non avessi settato l'orario, allora la chiamata ricorsiva per questa visita fittizia mi avrebbe restituito
            //  un NPE. ==> quindi cerco di settare correttamente la visita!

            // Come determino l'orario della visitaFittizia ?
            // considera che questi sono calcoli che faccio sempre nel futuro

            // Se !listaVisiteNextGiornoAmmissibile.empty
            //  allora listaVisiteNextGiornoAmmissibile.last().getOraFine()
            // Altrimenti calcolatoreAmmissibilita.calcolaOraFine().plusMinutes(pausaFromvisite)
            //

            // (Sono nel futuro)
            // Se listaVisite vuota OR listaVisite <= listaMedici
            if(listaVisiteNextGiornoAmmissibile.empty || listaVisiteNextGiornoAmmissibile.size() <= listaMedici.size())
                    visitaFittizia.setOra( Time.valueOf( Parameters.orarioAperturaMattina.plusMinutes(Parameters.pausaFromVisite) ) ) // allora ora = orarioAperturaMattina.plusMinutes(pausaFromvisite)

            // Altrimenti vuol dire che c'è almeno una visita nel futuro, quindi:
            else
                // Ti prendi l'ultima visita tra quelle future
                //visitaFittizia.setOra(Time.valueOf( listaVisiteNextGiornoAmmissibile.last().calcolaOraFine().toLocalTime().plusMinutes(Parameters.pausaFromvisite) )  )
                visitaFittizia.setOra(Time.valueOf( listaVisiteNextGiornoAmmissibile.last().ora.toLocalTime().plusMinutes(Parameters.pausaFromVisite) )  )


//            visitaFittizia.setOra( Time.valueOf(
//                    (!listaVisiteNextGiornoAmmissibile.empty)  ? // && (listaMedici.size() > listaVisiteNextGiornoAmmissibile.size())
//                            listaVisiteNextGiornoAmmissibile.last().calcolaOraFine().toLocalTime().plusMinutes(Parameters.pausaFromvisite) :
//                            Parameters.orarioAperturaMattina.plusMinutes(Parameters.pausaFromvisite) )
//
//            )
//            // Devo aggiungere l'altra condizone per cui se listaMedici.size() < listaVisite.size()

            visitaFittizia.setPrestazione(prestazioneFittizia )

            listaVisiteNextGiornoAmmissibile.add(visitaFittizia) // Quando arrivo qui devo essere sicuro di avere almeno un medico

            // VECCHIA IDEA:
            // ricostruisco la lista di visite usando come base di partenza le visite del successivo giorno ammissibile
            build( listaVisiteNextGiornoAmmissibile, listaMedici, durataMediaNuovaVisita )

            // Chiamata ricorsiva, che dovrebbe ritornare a questo punto la giusta entry
            return getPrimoMedicoDisponibile(durataMediaNuovaVisita)

            /// NUOVA IDEA: non farei una nuova build, bensì creerei una nuova coda ausiliaria, e ritornerei poi la
            // chiamata getPrimoMedicoDisponibile() da questa nuova coda ausiliaria.
            //CodaMediciDisponibiliGroovyImpl codaDelFuturo = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisiteNextGiornoAmmissibile, oraAttuale, durataMediaNuovaVisita, visitaService)
            //return codaDelFuturo.getPrimoMedicoDisponibile(durataMediaNuovaVisita) // in questo modo sfrutto la coda "ausiliaria" del futuro

        }

        // TODO: Fare caso else: se listaVisite.size < listaMedici.size allora guarda tutti i medici occupati, ed assegna quello libero.
        //  magari posso manipolare la variabile risultato inserendo m3 al posto del medico....

        if (listaVisite.size() < listaMedici.size() && mediciQueue.size() < listaMedici.size()) {
            // Ottieni la lista di medici occupati (da mediciQueue)
            def mediciOccupati = mediciQueue.collect { it.key }

            // Trova un medico libero (il primo che NON è nella coda)
            Medico medicoLibero = (listaMedici - mediciOccupati).find()

            /// IN TEORIA NON DOVREBBE SERVIRE fineVisita...
            FineVisita fineVisitaDelMedicoLibero = new FineVisita((long) listaVisite.size() + 1,
                    Time.valueOf(
                    Parameters.orarioAperturaMattina
                            .plusMinutes(Parameters.pausaFromVisite)
                            .plusMinutes(durataMediaNuovaVisita.toLong()))
            )

            // Crea un Entry con medico e fine visita
//            risultato = new AbstractMap.SimpleEntry<>(medicoLibero, fineVisitaDelMedicoLibero)
            risultato = Map.entry(medicoLibero, fineVisitaDelMedicoLibero)

        }
        return risultato
    }

    @Override
    Map<Medico, FineVisita> getMediciMap() {
        return Collections.unmodifiableMap(mediciMap)
    }

    @Override
    Queue<Map.Entry<Medico, FineVisita>> getMediciQueue() {
        return this.mediciQueue;
    }


    /** Procedura di costruzione della coda */
    private void build(List<Visita> listaVisite, List<Medico> listaMedici, Double durataMedia) {


        if (!listaVisite || !listaMedici) return // pensare di lasciare solo if(!listaMedici) return


        // TODO: devo gestire il caso in cui listaVisite sia vuota, perchè in tal caso non mi crea fa il build() e va in errore il pianificatore component
//        if(listaVisite.empty) {
//            listaMedici.eachWithIndex {
//                medico, i ->
//                this.@mediciMap[i] = new FineVisita()
//                this.@mediciQueue.add( Map.entry(medico, new FineVisita()))
//            }
//        }


        /** Caso lista visite < lista medici */
        listaVisite
                .take(listaMedici.size())
                .withIndex()
                .each { visita, i ->
                    Medico medico = listaMedici[i] // vado in ordine di medico

                    /** Check ammissibilita' oraria, analizzando l'ora di fine della visita i-esima. */
                    durataMedia = visita.getPrestazione().getDurataMedia()
                    Time oraFine = getRightOraFine(visita.ora.toLocalTime(), durataMedia )

                    /** Posso quindi creare il nuovo oggetto FineVisita */
                    FineVisita fineVisita = new FineVisita(visita.getIdVisita(), oraFine)

                    /** Aggiungo: prima in mediciMap, e poi in mediciQueue. */
                    this.@mediciMap[medico] =  fineVisita //oraFine // la @ serve per esplicitare di riferirsi al campo anzichè al getter del campo
                    this.@mediciQueue.add( Map.entry(medico, fineVisita) )
                }

        /**  Considero le altre visite ("il resto") */
        listaVisite
                .drop(listaMedici.size())
                .each { visita ->

                /** 1) Trovo l'elemento (entry) della coda avente oraFine minore, grazie alla coda di priorita' */
                    // TODO: da provare: le prossime 2 righe potrebbero fare la stessa cosa, quindi provare a tenere solamente la prima delle due e vedere cosa succede.
                    def entry = mediciQueue.poll()
                    this.@mediciQueue.remove( this.@mediciMap[entry.key] ) // lo rimuovo subito dalla coda
                    Medico medicoKey = entry.key // ottengo il medico

                /** 2) Aggiorno la mappa con la nuova oraFine per il medico appena estratto */
                    /** 2-a) calcolo ammissibilita' */
                    // TODO: queste due righe sono in comune con le altre due nel caso della take()
                    durataMedia = visita.getPrestazione().getDurataMedia()
                    Time nuovaOraFine = getRightOraFine(visita.ora.toLocalTime(), durataMedia)

                    /** 2-b) inserimento in mappa */
                    FineVisita fineVisita = new FineVisita( visita.getIdVisita(), nuovaOraFine )
                    this.@mediciMap[medicoKey] = fineVisita

                /** 3) Ri-aggiungo l'elemento in coda */
                    Map.Entry<Medico, FineVisita> entryAggiornata = mediciMap.find { it -> it.key.idAnagrafica == medicoKey.idAnagrafica}
                    this.@mediciQueue.add( entryAggiornata  )
                }
    }


/** Torna la lista di visite del successivo giorno ammissibile */
    List<Visita> getListaVisiteNextGiornoAmmissibile() {

        Date dataDiRicerca = listaVisite.empty ? new Date() : listaVisite[0].getDataVisita()

        Calendar calendar = Calendar.getInstance()
        calendar.setTime( dataDiRicerca); // solitamente, la prima visita della lista visite giornaliere dovrebbe avere la data corretta
        Date nextGiornoAmmissibile = calcolatoreAmmissibilita.findNextGiornoAmmissibile(dataDiRicerca, calendar)


        return visitaService.getAllVisiteByData(nextGiornoAmmissibile)
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
                        .plusMinutes(Parameters.pausaFromVisite)
                        .plusMinutes(durata)

            case RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO ->
                // Da migliorare (questa e' la casistica se sono esattamente le 21:00) allora non passare a mettere l'orario del mattino ....
                // TODO: prevedere di usare calcolatoreAmmissibilita.isOrarioAfterPomeriggio() che
                yield oraDaControllare.plusMinutes(durata).isAfter(Parameters.orarioChiusuraPomeriggio) // se oraDaControllare.plusMinutes(durata) == 21:00 allora la isAfter da false
                    ? Parameters.orarioAperturaMattina
                        .plusMinutes(Parameters.pausaFromVisite)
                        .plusMinutes(durata)
                        // TODO: in caso stia sforando la chiusura del pomeriggio allora posso pensare
                        //   di richiamare ricorsivamente build() ?? ==> in questo modo mi baso sulle visite del primo giorno ammissibile
                    : oraDaControllare.plusMinutes(durata)

            case RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ->
                yield Parameters.orarioAperturaPomeriggio
                        .plusMinutes(Parameters.pausaFromVisite)
                        .plusMinutes(durata)

            default -> throw new IllegalArgumentException("Tipo di risultato non gestito: $risultato")
        }

        return Time.valueOf(oraFine)
    }


}