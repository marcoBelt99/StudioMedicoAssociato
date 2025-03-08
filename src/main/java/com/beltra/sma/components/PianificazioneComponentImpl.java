package com.beltra.sma.components;


import com.beltra.sma.functional.TriFunction;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;

import com.beltra.sma.service.MedicoService;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.FineVisita;
import com.beltra.sma.utils.SlotDisponibile;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


import java.sql.Time;

import java.time.LocalTime;
import java.util.*;


@Component
public class PianificazioneComponentImpl implements PianificazioneComponent {

    // Code Injection
    private final VisitaService visitaService;
    private final MedicoService medicoService;
    private final PianificazioneManager pianificazioneManager;
    private final CalcolatoreAmmissibilitaComponentImpl calcolatore;


    /**
     * Nota bene: @Lazy per evitare dipendenze circolari tra i Bean: PianificazioneComponentImpl e VisitaServiceImpl
     */
    public PianificazioneComponentImpl(@Lazy VisitaService visitaService,
                                       MedicoService medicoService,
                                       PianificazioneManager pianificazioneManager,
                                       CalcolatoreAmmissibilitaComponentImpl calcolatore) {
        this.visitaService = visitaService;
        this.medicoService = medicoService;
        this.pianificazioneManager = pianificazioneManager;
        this.calcolatore = calcolatore;
    }


    public Optional<SlotDisponibile> trovaPrimoSlotDisponibile(Double durata,
                                                               Date dataAttuale,
                                                               LocalTime oraAttuale,
                                                               List<Medico> listaMedici,
                                                               List<Visita> visiteGiornaliere) {

        // Settaggio del calendario alla dataAttuale
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataAttuale);


            // Se data di oggi non è ammissibile,
            if (!calcolatore.isGiornoAmmissibile(dataAttuale))

                // allora passi a considerare come data il/i giorno/i successivo/i
                // ti richiami ricorsivamente sul giorno successivo, con una nuova listaVisiteGiornaliere (relativa al giorno successivo)
                // partendo però non più da oraAttuale, bensì da orarioApertura
                return trovaSlotGiornoSuccessivo(calendar, durata, listaMedici);

            // ALTRIMENTI, SE ARRIVO QUI LA DATA E' AMMISSIBILE,
            // mi resta solo da verificare AMMISSIBILITÀ ORARIO!
            else
                return  visiteGiornaliere.isEmpty() ?

                    /// CASO BASE: LISTA VISITE VUOTA
                    calcolaSlotDisponibileConListaVisiteGiornaliereVuota(calendar, dataAttuale, oraAttuale, durata, listaMedici) :

                    /// CASO INDUTTIVO: LISTA VISITE NON VUOTA
                    calcolaSlotDisponibileConListaVisiteGiornaliereNonVuota(calendar, dataAttuale, oraAttuale, durata, visiteGiornaliere,listaMedici); // se sono sempre sullo stesso giorno, uso dataAttuale

    }



    private Optional<SlotDisponibile> calcolaSlotDisponibileConListaVisiteGiornaliereVuota(
                                        Calendar calendar,
                                        Date dataDiRicerca,
                                        LocalTime oraAttuale,
                                        Double durataMedia,
                                        List<Medico> listaMedici) {

        /// CASO PARTICOLARE DELLA MEZZANOTTE
        // Se (oraAttuale+durataMedia) supera la mezzanotte rientro nel giorno successivo, e di conseguenza
        // risultatoCalcoloAmmissibilità mi darebbe NO_BECAUSE_BEFORE_APERTURA_MATTINA,
        // ma in realtà io sono in NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO !!!
        //    ==> allora passi a considerare come data il giorno successivo
        if( calcolatore.isOrarioAfterMezzanotte(oraAttuale, durataMedia) )
            // ti richiami ricorsivamente sul giorno successivo, con una nuova listaVisiteGiornaliere (relativa al giorno successivo)
            return trovaSlotGiornoSuccessivo(calendar, durataMedia, listaMedici);


        // Il medico da assegnare allo slot deve sempre essere il PRIMO MEDICO, dato che listaVisiteGiornaliera e' vuota
        Medico medico = pianificazioneManager.getPrimoMedicoDisponibile(new ArrayList<Visita>(), listaMedici);
        SlotDisponibile slotDisponibile = new SlotDisponibile(dataDiRicerca,medico);


        // Se arrivo QUI, e' lo stesso giorno da cui sono partito (ora attuale)
        // VERIFICO SE oraAttuale è ammissibile !!
        // DEVO ANALIZZARE IL MOTIVO DELLA NON AMMISSIBILITA' ORARIA,
        // e poi PROPORRE DEI DIVERSI ORARI PER LO SLOT (A SECONDA DEL MOTIVO):

        calendar.add(Calendar.DAY_OF_MONTH, 1); // Incrementa di un giorno
        Date dataSuccessiva = calendar.getTime(); // Ottieni la nuova data

        return switch ( calcolatore.getRisultatoCalcoloAmmissibilitaOrario( oraAttuale, durataMedia ) ) {
            case AMMISSIBILE ->
                    Optional.of( setOrarioSlot(slotDisponibile, oraAttuale.plusMinutes(pausaFromvisite)) ); // allora assegno con oraAttuale+5min, e ritorno lo slot

            case NO_BECAUSE_BEFORE_APERTURA_MATTINA ->
                    Optional.of(setOrarioSlot(slotDisponibile, orarioAperturaMattina.plusMinutes(pausaFromvisite))); // oraAperturaMattina+5min ( dal momento che listaVisiteGiornaliere = [] )

            case NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ->
                    Optional.of(setOrarioSlot(slotDisponibile, orarioAperturaPomeriggio.plusMinutes(pausaFromvisite))); //  oraAperturaPomeriggio+5min ( dal momento che listaVisiteGiornaliere = [] )

            case NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO -> // PASSA AL GIORNO SUCCESSIVO

                // ti richiami ricorsivamente sul giorno successivo, con una nuova listaVisiteGiornaliere (relativa al giorno successivo)
                trovaPrimoSlotDisponibile(durataMedia,
                                          dataSuccessiva, // dataSuccessiva
                                          orarioAperturaMattina,
                                          listaMedici,
                                          getAllVisiteByData(dataSuccessiva) // dataSuccessiva
                );
        };

    }




/**
 *  ///     RICERCA BINARIA: splitto listaVisiteGiornaliera in <br>
*  ///      1)     listaVisiteGiornalieraMattina<br>
*  ///      2)     listaVisiteGiornalieraPomeriggio <br>
*  ///    Prima provo con 1) e, basandomi sull'ultima visita presente in lista, se oraFineUltimaVisita+durata+5' è ammissibile accodo
*  ///    e ritorno slotDisponibile in mattinata
*  ///    Altrimenti, provo con 2) ripetendo il controllo fatto per 1)
*  ///    Altrimenti, passo al giorno successivo
 * */
    private Optional<SlotDisponibile> calcolaSlotDisponibileConListaVisiteGiornaliereNonVuota(
            Calendar calendar,
            Date dataDiRicerca,
            LocalTime oraAttuale,
            Double durataMedia,
            List<Visita> listaVisiteGiornaliere,
            List<Medico> listaMedici) {


        //  Devo recuperare tramite mediciMap il primo medico disponibile sulla base della lista di visite
        Medico medicoLiberato = new Medico();
        medicoLiberato = pianificazioneManager.getPrimoMedicoDisponibile(listaVisiteGiornaliere, listaMedici);

        // RECUPERO DALLA MAPPA ANCHE LA VISITA AVENTE ORARIO DI FINE MINORE
        FineVisita fineVisita = pianificazioneManager.getMediciMap().get(medicoLiberato.getIdAnagrafica());

        return switch ( calcolatore.getRisultatoCalcoloAmmissibilitaOrario(oraAttuale, durataMedia) ) {
            case AMMISSIBILE ->

                // Fichè listaVisiteGiornaliere.size() <= listaMedici.size()
                    (listaVisiteGiornaliere.size() <= listaMedici.size()) ?
                        // allora come ora dello slot ho oraAttuale+5min (perchè ho subito almeno un medico libero)
                        (Optional.of( setOrarioSlot( new SlotDisponibile(dataDiRicerca, medicoLiberato), oraAttuale.plusMinutes(pausaFromvisite) ) )) :

                // Altrimenti, calcolo lo slot usando o la sottolista di visite in mattino, oppure la sottolista di visite in pomeriggio
                // a seconda di oraAttuale
                    // isOrarioInMattina mi controlla 2 cose:
                    // 1) che oraAttuale e poi anche oraAttuale+durataMedia+5min sia ammissibile
                    // 2) che oraAttuale+durata+5min isBefore oraChiusuraMattina
                        ( calcolatore.isOrarioInMattina(oraAttuale, durataMedia) ? // Se isOrarioInMattina
                            // allora mi pianifichi la visita in coda a quelle del mattino: Quando?
                            // ==> serve sapere l'ultima visita del mattino per poter calcolare l'ora di pianificazione di questa nuova visita
                            accodaVisitaAlMattino(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato,
                                                  calendar, oraAttuale, durataMedia, listaMedici) :

                            // Altrimenti sicuramente sarà un orario ammissibile al pomeriggio
                            accodaVisitaAlPomeriggio(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato,
                                                     calendar, oraAttuale, durataMedia, listaMedici)
                        );
            case NO_BECAUSE_BEFORE_APERTURA_MATTINA ->

                // Fichè listaVisiteGiornaliere.size() <= listaMedici.size()
                ( listaVisiteGiornaliere.size() <= listaMedici.size() ) ?
                        // allora come ora dello slot ho oraAperturaMattina+5min (perchè ho subito almeno un medico libero)
                        (Optional.of( setOrarioSlot( new SlotDisponibile(dataDiRicerca, medicoLiberato), orarioAperturaMattina.plusMinutes(pausaFromvisite) ) )) :

                        // Altrimenti, calcolo lo slot usando o la sottolista di visite in mattino, oppure la sottolista di visite in pomeriggio
                        // a seconda di oraAttuale
                        // isOrarioInMattina mi controlla 2 cose:
                        // 1) che oraAttuale e poi anche oraAttuale+durataMedia+5min siano ammissibile
                        // 2) che oraAttuale+durata+5min isBefore oraChiusuraMattina
                        ( calcolatore.isOrarioBetweenMidnightAndAperturaMattina(oraAttuale) ?

                                cercaSlotAlMattino(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato,
                                        calendar, orarioAperturaMattina, durataMedia, listaMedici) :
                                // allora mi pianifichi la visita in coda a quelle del mattino: Quando?
                                // ==> serve sapere l'ultima visita del mattino per poter calcolare l'ora di pianificazione di questa nuova visita
                                // accodaVisitaAlMattino(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato,
                                //        calendar, orarioAperturaMattina, durataMedia, listaMedici) :

                                cercaSlotAlPomeriggio(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato,
                                        calendar, orarioAperturaPomeriggio, durataMedia, listaMedici)
                                // Altrimenti sicuramente sarà un orario ammissibile al pomeriggio
                                // accodaVisitaAlPomeriggio(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato,
                                //         calendar, oraAttuale, durataMedia, listaMedici)
                        );

            case NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO -> Optional.empty(); // TODO
            case NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO -> Optional.empty(); // TODO
        };

    }


    /** Caso orario non ammissibile because before apertura mattino */
    private Optional<SlotDisponibile> cercaSlotAlMattino(List<Visita> listaVisiteGiornaliere, FineVisita fineVisita,
                                                         Date dataDiRicerca, Medico medicoLiberato, Calendar calendar,
                                                         LocalTime orario, Double durataMedia, List<Medico> listaMedici) {

        // Se (ora di fine ultima visita) + 5min è ammissibile
        return calcolatore.isOrarioAmmissibile( fineVisita.getOraFine().toLocalTime(), durataMedia ) ?
                // allora assegna come orario mattutino (ora fine ultima visita) + 5min
            Optional.of( setOrarioSlot(new SlotDisponibile(dataDiRicerca, medicoLiberato),
                                       fineVisita.getOraFine().toLocalTime().plusMinutes( pausaFromvisite)) )  :
                // altrimenti cerca al pomeriggio
                cercaSlotAlPomeriggio(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato, calendar, orario, durataMedia, listaMedici);
    }

    /** Caso (orario non ammissibile because before apertura mattino) or (between chiusura mattina and apertura pomeriggio)*/
    private Optional<SlotDisponibile> cercaSlotAlPomeriggio(List<Visita> listaVisiteGiornaliere, FineVisita fineVisita,
                                                            Date dataDiRicerca, Medico medicoLiberato, Calendar calendar,
                                                            LocalTime oraAttuale, Double durataMedia, List<Medico> listaMedici){
        // C'è spazio in fondo al pomeriggio?
        return calcolatore.isOrarioAmmissibile(fineVisita.getOraFine().toLocalTime(), durataMedia) ?
                // se sì, produci lo slot e come orario metti in fondo al pomeriggio
                Optional.of( setOrarioSlot(new SlotDisponibile(dataDiRicerca, medicoLiberato), calcolatore.aggiungiDurataAndPausa(fineVisita.getOraFine().toLocalTime(), durataMedia)) ) :
              // altrimenti, cerca nel giorno successivo
                trovaSlotGiornoSuccessivo(calendar, durataMedia, listaMedici);
    }


    /** Solo nel caso AMMISSIBILE */
    private Optional<SlotDisponibile> accodaVisitaAlMattino(List<Visita> listaVisiteGiornaliere, FineVisita fineVisita,
                                                            Date dataDiRicerca, Medico medicoLiberato, Calendar calendar,
                                                            LocalTime oraAttuale, Double durataMedia, List<Medico> listaMedici) {

        // Trovo le visite del mattino
        List<Visita> visiteGiornaliereDelMattino = splittaListaVisiteGiornaliere.apply( orarioAperturaMattina, orarioChiusuraMattina, listaVisiteGiornaliere);

        // Però se è vuota posso pianificare all'ora attuale (con lista visite vuota)
        if(visiteGiornaliereDelMattino.isEmpty())
            return calcolaSlotDisponibileConListaVisiteGiornaliereVuota(calendar, dataDiRicerca, oraAttuale, durataMedia, listaMedici);


        // Arrivato qui, la listaVisiteMattino non è vuota, ed ho già controllato che ci sia spazio al mattino
        // quindi procedo a calcolare l'orario in funzione dell'ultima visita presente in lista
        Visita ultimaVisitaMattino = visiteGiornaliereDelMattino.get( visiteGiornaliereDelMattino.size() - 1);

        // Trovo gli orari di fine visita ultima visita della sottolista
        LocalTime oraInizioPrevistaProssimaVisitaMattinoFromUltimaVisitaMattino =
                ultimaVisitaMattino != null ? ultimaVisitaMattino.calcolaOraFine().toLocalTime().plusMinutes(pausaFromvisite) : null;

        // TODO
        return Optional.of( setOrarioSlot( new SlotDisponibile( dataDiRicerca, medicoLiberato),
                oraInizioPrevistaProssimaVisitaMattinoFromUltimaVisitaMattino) );
    }

    /** Solo nel caso AMMISSIBILE */
    private Optional<SlotDisponibile> accodaVisitaAlPomeriggio(List<Visita> listaVisiteGiornaliere, FineVisita fineVisita,
                                                               Date dataDiRicerca, Medico medicoLiberato, Calendar calendar,
                                                               LocalTime oraAttuale, Double durataMedia, List<Medico> listaMedici) {


        // Trovo le visite del pomeriggio
        List<Visita> visiteGiornaliereDelPomeriggio = splittaListaVisiteGiornaliere.apply(orarioAperturaPomeriggio, orarioChiusuraPomeriggio, listaVisiteGiornaliere);


        // Però se è vuota posso pianificare all'ora attuale (con lista visite vuota)
        if(visiteGiornaliereDelPomeriggio.isEmpty())
            return calcolaSlotDisponibileConListaVisiteGiornaliereVuota(calendar, dataDiRicerca, oraAttuale, durataMedia, listaMedici);

        Visita ultimaVisitaPomeriggio = visiteGiornaliereDelPomeriggio.get(visiteGiornaliereDelPomeriggio.size() - 1);


        LocalTime oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio =
                ultimaVisitaPomeriggio != null ? ultimaVisitaPomeriggio.calcolaOraFine().toLocalTime().plusMinutes(pausaFromvisite) : null;


        // TODO
        return Optional.of( setOrarioSlot( new SlotDisponibile( dataDiRicerca, medicoLiberato),
                oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio) );
    }



    /** Metodo ausiliario a scopo di DRY <br>
     *  ti richiami ricorsivamente sul giorno successivo, con una nuova listaVisiteGiornaliere (relativa al giorno successivo)
     *  partendo però non più da oraAttuale, bensì da orarioAperturaMattina
     * */
    public Optional<SlotDisponibile> trovaSlotGiornoSuccessivo(Calendar calendar,
                                                                Double durataMedia,
                                                                List<Medico> listaMedici) {
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Incrementa di un giorno
        Date dataSuccessiva = calendar.getTime(); // Ottieni la nuova data

        return trovaPrimoSlotDisponibile(durataMedia,
                dataSuccessiva,
                orarioAperturaMattina,
                listaMedici,
                getAllVisiteByData(dataSuccessiva));
    }






    /// ######################################################################################
    /// ######################################################################################
    /// ######################################################################################
    /// ######################################################################################
    /// QUA SOTTO HO LA VECCHIA PARTE (ITERATIVO)
    /// ######################################################################################
    /// ######################################################################################
    /// ######################################################################################
    /// ######################################################################################




    /** Metodo ausiliario a scopo di DRY */
    private SlotDisponibile setOrarioSlot(SlotDisponibile slot, LocalTime orario) {
        slot.setOrario(Time.valueOf(orario));
        return slot;
    }


    /**  */
    private Optional<SlotDisponibile> getSlotDisponibile(List<Visita> sottolistaVisiteGiornaliere,
                                                         List<Medico> listaMedici,
                                                         Date dataDiRicerca,
                                                         LocalTime oraInizioPrevistaProssimaVisitaFromUltimaVisitaInSottolista) {

        // ESSENZIALE: Setto il pianificazioneManager con l'insieme di visite da cui basarsi per ricercare il medico
        pianificazioneManager.setListaVisite( sottolistaVisiteGiornaliere ); // seleziono solo quelle del mattino / pomeriggio

        pianificazioneManager.aggiornaMediciMap(); // ESSENZIALE: Aggiorno la mediciMap del pianificazioneManager

        // CERCO IL MEDICO DA ASSEGNARE USANDO LE STRUTTURE DATI DEL PIANIFICAZIONE MANAGER
        Medico medicoLiberato = pianificazioneManager.getPrimoMedicoDisponibile( sottolistaVisiteGiornaliere, listaMedici  );

        return Optional.of(new SlotDisponibile(dataDiRicerca, Time.valueOf(oraInizioPrevistaProssimaVisitaFromUltimaVisitaInSottolista), medicoLiberato));
    }








    /** TriPredicate e' una interfaccia funzionale che ho creato io per poter usare più parametri nel mio predicato.
     * <br>
     *  Divide la lista di visite che prende come parametro nella sottolista di tutte le visite comprese tra oraInizio e oraFine.
     *  */
    public TriFunction<LocalTime, LocalTime, List<Visita>, List<Visita>> splittaListaVisiteGiornaliere =
            (oraInizio, oraFine, listaVisiteGiornaliere) ->
                    listaVisiteGiornaliere.stream()
                            .filter(v -> v.getOra().after(Time.valueOf(oraInizio))
                                      && v.getOra().before(Time.valueOf(oraFine)))
                            .toList();



    @Override
    /** In questo metodo inserisco i dati di test per i vari casi di test specifici.
     *   */
    public List<Visita> getAllVisiteByData(Date dataCorrente) {

        return visitaService.getAllVisiteByData( dataCorrente );
    }



}