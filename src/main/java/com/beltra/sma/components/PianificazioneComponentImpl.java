package com.beltra.sma.components;


import com.beltra.sma.datastructures.CodaMediciDisponibili;
import com.beltra.sma.groovy.datastructures.CodaMediciDisponibiliGroovyImpl;
//import com.beltra.sma.datastructures.Pianificatore;
//import com.beltra.sma.datastructures.PianificatoreImpl;
import com.beltra.sma.functional.TriFunction;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;

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

    //private final Pianificatore pianificatore;

    private final CalcolatoreAmmissibilitaComponentImpl calcolatore;

    // TODO: Importo la nuova struttura dati
    private CodaMediciDisponibili codaMediciDisponibili;


    /**
     * Nota bene: @Lazy per evitare dipendenze circolari tra i Bean: PianificazioneComponentImpl e VisitaServiceImpl
     */
    public PianificazioneComponentImpl(@Lazy VisitaService visitaService,
                                       //PianificatoreImpl pianificatore, // TODO: da rimuovere quando finisco il passaggio delle strutture dati
                                       CalcolatoreAmmissibilitaComponentImpl calcolatore) {
        this.visitaService = visitaService;
        //this.pianificatore = pianificatore; // TODO: da rimuovere quando finisco il passaggio delle strutture dati
        this.calcolatore = calcolatore;
    }


    public Optional<SlotDisponibile> trovaSlotDisponibile(Double durata,
                                                          Date dataAttuale,
                                                          LocalTime oraAttuale,
                                                          List<Medico> listaMedici,
                                                          List<Visita> visiteGiornaliere) {

        // TODO: Creazione della coda come una delle prime operazioni
        // Nota bene: e' obbligatorio inizializzarla qui ai fini del funzionamento degli altri metodi di calcolo dello slot disponibile
        // Nota bene: inizializzandola sempre qui all'inizio sono in grado di passarle la giusta lista di visite giornaliere
        // quindi dovrei risolvere le ambiguita'
        this.codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMedici, visiteGiornaliere, oraAttuale, durata);

        // Settaggio del calendario alla dataAttuale
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataAttuale);


            // Se data di oggi non è ammissibile,
            if (!calcolatore.isGiornoAmmissibile(dataAttuale))

                // allora passi a considerare come data il/i giorno/i successivo/i
                // ti richiami ricorsivamente sul giorno successivo, con una nuova listaVisiteGiornaliere (relativa al giorno successivo)
                // partendo però non più da oraAttuale, bensì da orarioApertura
                return trovaSlotGiornoSuccessivo(calendar, durata, listaMedici);

            // #########################################################################################################
            // TODO: Pensare di mettere qui (e fattorizzare) la ricerca del primo medico disponibile (tramite il metodo)
            // #########################################################################################################


            // ALTRIMENTI, SE ARRIVO QUI LA DATA E' AMMISSIBILE,
            // mi resta solo da verificare AMMISSIBILITÀ ORARIO!
            else
                return visiteGiornaliere.isEmpty() ?

                    /// CASO BASE: LISTA VISITE VUOTA
                    calcolaSlotDisponibileConListaVisiteGiornaliereVuota(calendar, dataAttuale, oraAttuale, durata, listaMedici) :

                    /// CASO INDUTTIVO: LISTA VISITE NON VUOTA
                    calcolaSlotDisponibileConListaVisiteGiornaliereNonVuota(calendar, dataAttuale, oraAttuale, durata, visiteGiornaliere,listaMedici);

    }



    private Optional<SlotDisponibile> calcolaSlotDisponibileConListaVisiteGiornaliereVuota(Calendar calendar, Date dataDiRicerca, LocalTime oraAttuale, Double durataMedia, List<Medico> listaMedici) {

        // TODO: inizializzo codaMediciDisponibili con i giusti dati
        codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMedici, new ArrayList<>(), oraAttuale, durataMedia);

        /// CASO PARTICOLARE DELLA MEZZANOTTE
        // Se (oraAttuale+durataMedia) supera la mezzanotte rientro nel giorno successivo, e di conseguenza
        // risultatoCalcoloAmmissibilità mi darebbe NO_BECAUSE_BEFORE_APERTURA_MATTINA,
        // ma in realtà io sono in NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO !!!
        //    ==> allora passi a considerare come data il giorno successivo
        if( calcolatore.isOrarioAfterMezzanotte(oraAttuale, durataMedia) )
            // ti richiami ricorsivamente sul giorno successivo, con una nuova listaVisiteGiornaliere (relativa al giorno successivo)
            return trovaSlotGiornoSuccessivo(calendar, durataMedia, listaMedici);

        // #################################
        // TODO:  SOSTITUIRE LA STRUTTURA DATI
        // #################################
        // Il medico da assegnare allo slot deve sempre essere il PRIMO MEDICO, dato che listaVisiteGiornaliera e' vuota
        //Medico medico = pianificatore.getPrimoMedicoDisponibile(new ArrayList<Visita>(), listaMedici); // ORIGINALE
        Medico medico = codaMediciDisponibili.getPrimoMedicoDisponibile(durataMedia).getKey(); // NUOVA

        SlotDisponibile slotDisponibile = new SlotDisponibile(dataDiRicerca,medico);


        // Se arrivo QUI, e' lo stesso giorno da cui sono partito (ora attuale)
        // VERIFICO SE oraAttuale è ammissibile !!
        // DEVO ANALIZZARE IL MOTIVO DELLA NON AMMISSIBILITA' ORARIA,
        // e poi PROPORRE DEI DIVERSI ORARI PER LO SLOT (A SECONDA DEL MOTIVO):

        calendar.add(Calendar.DAY_OF_MONTH, 1); // Incrementa di un giorno
        Date dataSuccessiva = calendar.getTime(); // Ottieni la nuova data

        return switch ( calcolatore.getRisultatoCalcoloAmmissibilitaOrario( oraAttuale, durataMedia ) ) {

            case AMMISSIBILE ->
                     setOrarioSlot(slotDisponibile, oraAttuale.plusMinutes(pausaFromvisite)); // allora assegno con oraAttuale+5min, e ritorno lo slot

            case NO_BECAUSE_BEFORE_APERTURA_MATTINA ->
                    setOrarioSlot(slotDisponibile, orarioAperturaMattina.plusMinutes(pausaFromvisite)); // oraAperturaMattina+5min ( dal momento che listaVisiteGiornaliere = [] )

            case NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ->
                    setOrarioSlot(slotDisponibile, orarioAperturaPomeriggio.plusMinutes(pausaFromvisite)); //  oraAperturaPomeriggio+5min ( dal momento che listaVisiteGiornaliere = [] )

            case NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO -> // PASSA AL GIORNO SUCCESSIVO

                // ti richiami ricorsivamente sul giorno successivo, con una nuova listaVisiteGiornaliere (relativa al giorno successivo)
                trovaSlotDisponibile(durataMedia,
                                          dataSuccessiva, // dataSuccessiva
                                          orarioAperturaMattina,
                                          listaMedici,
                                          getAllVisiteByData(dataSuccessiva) // dataSuccessiva
                );


            // TODO: Perchè invece non ho usato trovaSlotGiornoSuccessivo() ?????
            // se funziona in questo modo, non ho neanche bisogno di gestire dataSuccessiva prima dello switch == riuso del codice!
            //trovaSlotGiornoSuccessivo(calendar, durataMedia, listaMedici );

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
    private Optional<SlotDisponibile> calcolaSlotDisponibileConListaVisiteGiornaliereNonVuota(Calendar calendar, Date dataDiRicerca, LocalTime oraAttuale, Double durataMedia, List<Visita> listaVisiteGiornaliere, List<Medico> listaMedici) {


        // #################################
        // TODO:  SOSTITUIRE LA STRUTTURA DATI
        // #################################

        //
        // Devo recuperare tramite la mediciMap del pianificatore il primo medico disponibile sulla base della lista di visite // VECCHIO
        // Medico medicoLiberato = new Medico(); // VECCHIO
        //medicoLiberato = pianificatore.getPrimoMedicoDisponibile(listaVisiteGiornaliere, listaMedici); // VECCHIO

        // NUOVO: codaMediciDisponibili dovrebbe gia' essere stata inizializzata

        Map.Entry<Medico, FineVisita> entryMedicoDisponibile = codaMediciDisponibili.getPrimoMedicoDisponibile(durataMedia);
        Medico medicoLiberato = entryMedicoDisponibile.getKey(); // NUOVO

        // #################################
        // TODO:  SOSTITUIRE LA STRUTTURA DATI
        // #################################

        // TODO: visto che qui sotto sto usando il FineVisita allora devo modificare il metodo
        //      codaMediciDisponibili.getPrimoMedicoDisponibile(durataMedia) in modo che ritorni una
        //  Entry<Medico, FineVisita> anzichè solo Medico

        // RECUPERO DALLA MAPPA DEL pianificatore ANCHE LA VISITA AVENTE ORARIO DI FINE MINORE // VECCHIO
        // FineVisita fineVisita = pianificatore.getMediciMap().get(medicoLiberato.getIdAnagrafica()); // VECCHIO
        FineVisita fineVisita = entryMedicoDisponibile.getValue();

        SlotDisponibile slotDisponibile = new SlotDisponibile(dataDiRicerca, medicoLiberato);


        // Analizzo oraAttuale
        return switch (calcolatore.getRisultatoCalcoloAmmissibilitaOrario(oraAttuale, durataMedia)) {
            case AMMISSIBILE ->

                // Fichè listaVisiteGiornaliere.size() <= listaMedici.size()
                    (listaVisiteGiornaliere.size() <= listaMedici.size()) ?
                            // allora come ora dello slot ho oraAttuale+5min (perchè ho subito almeno un medico libero)
                            (setOrarioSlot(slotDisponibile, oraAttuale.plusMinutes(pausaFromvisite))) :

                            // Altrimenti, calcolo lo slot usando o la sottolista di visite in mattino, oppure la sottolista di visite in pomeriggio
                            // a seconda di oraAttuale
                            // isOrarioInMattina mi controlla 2 cose:
                            // 1) che oraAttuale e poi anche oraAttuale+durataMedia+5min sia ammissibile
                            // 2) che oraAttuale+durata+5min isBefore oraChiusuraMattina
                            (calcolatore.isOrarioAmmissibileInMattina(oraAttuale, durataMedia) ? // Se isOrarioInMattina
                                    // allora mi pianifichi la visita in coda a quelle del mattino: Quando?
                                    // ==> serve sapere l'ultima visita del mattino per poter calcolare l'ora di pianificazione di questa nuova visita
                                    accodaVisitaAlMattino(listaVisiteGiornaliere, fineVisita, slotDisponibile,
                                            calendar, oraAttuale, durataMedia, listaMedici) :

                                    // Altrimenti sicuramente sarà un orario ammissibile al pomeriggio
                                    accodaVisitaAlPomeriggio(listaVisiteGiornaliere, fineVisita, slotDisponibile,
                                            calendar, oraAttuale, durataMedia, listaMedici)
                            );
            // TODO:
            //   2) Per i soli casi di non ammissibilità,
            //      uso fineVisita per verificare se essa stessa è in Mattina oppure in Pomeriggio
            //      ==> già fatto per case NO_BECAUSE_BEFORE_APERTURA_MATTINA
            case NO_BECAUSE_BEFORE_APERTURA_MATTINA ->

                // Fichè listaVisiteGiornaliere.size() <= listaMedici.size()
                    listaVisiteGiornaliere.size() <= listaMedici.size() ?
                            // allora come ora dello slot ho oraAperturaMattina+5min (perchè ho subito almeno un medico libero)
                            setOrarioSlot(slotDisponibile, orarioAperturaMattina.plusMinutes(pausaFromvisite)) :

                            // Altrimenti, calcolo lo slot usando o la sottolista di visite in mattino, oppure la sottolista di visite in pomeriggio a seconda di fineVisita.getOraFine()
                            // isOrarioInMattina mi controlla 2 cose:
                            // 1) che oraAttuale e poi anche oraAttuale+durataMedia+5min siano ammissibile
                            // 2) che oraAttuale+durata+5min isBefore oraChiusuraMattina
                            ( calcolatore.isOrarioAmmissibileInMattina(fineVisita.getOraFine().toLocalTime().plusMinutes(pausaFromvisite), durataMedia) ?

                                    cercaSlotAlMattino(listaVisiteGiornaliere, fineVisita, slotDisponibile,
                                            calendar, orarioAperturaMattina, durataMedia, listaMedici, oraAttuale) :
                                    // allora mi pianifichi la visita in coda a quelle del mattino: Quando?
                                    // ==> serve sapere l'ultima visita del mattino per poter calcolare l'ora di pianificazione di questa nuova visita
                                    // accodaVisitaAlMattino(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato,
                                    //        calendar, orarioAperturaMattina, durataMedia, listaMedici) :

                                    cercaSlotAlPomeriggio(listaVisiteGiornaliere, fineVisita, slotDisponibile,
                                            calendar, orarioAperturaPomeriggio, durataMedia, listaMedici)
                                    // Altrimenti sicuramente sarà un orario ammissibile al pomeriggio
                                    // accodaVisitaAlPomeriggio(listaVisiteGiornaliere, fineVisita, dataDiRicerca, medicoLiberato,
                                    //         calendar, oraAttuale, durataMedia, listaMedici)
                            );

            case NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ->
                    cercaSlotAlPomeriggio(listaVisiteGiornaliere, fineVisita, slotDisponibile, calendar, oraAttuale, durataMedia, listaMedici);
            case NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO -> trovaSlotGiornoSuccessivo(calendar, durataMedia, listaMedici);
        };

    }


    /** Caso orario non ammissibile because before apertura mattino */
    private Optional<SlotDisponibile> cercaSlotAlMattino(List<Visita> listaVisiteGiornaliere, FineVisita fineVisita,
                                                         SlotDisponibile slotDisponibile, Calendar calendar,
                                                         LocalTime orario, Double durataMedia, List<Medico> listaMedici, LocalTime oraAttuale ) {


        // TODO: per fixare il bug per cui oraAttuale senza durata non è ammissibile because after chiusura pomeriggio
        //       mentre invece (oraAttuale + durata) non è ammissibile because before apertura mattina
        //  Esempio: il 26/03/2025 alle ore 23:17 ==> non è ammissibile perchè after chiusura pomeriggio
        //           facendo (23:17 + 45min) = 00:02 ==> non è ammissibile because before apertura mattina del giorno 27/03/2025
        if(calcolatore.getRisultatoCalcoloAmmissibilitaOrario(oraAttuale, Double.MIN_VALUE) == RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO )
            return trovaSlotGiornoSuccessivo(calendar, durataMedia, listaMedici); // Quindi devo cercare nel giorno successivo


        // Se (ora di fine ultima visita) + 5min è ammissibile
        return calcolatore.isOrarioAmmissibile( fineVisita.getOraFine().toLocalTime(), durataMedia ) ?
            // allora assegna come orario mattutino (ora fine ultima visita) + 5min
            setOrarioSlot(slotDisponibile, fineVisita.getOraFine().toLocalTime().plusMinutes( pausaFromvisite)) :

            // altrimenti cerca al pomeriggio
            cercaSlotAlPomeriggio(listaVisiteGiornaliere, fineVisita, slotDisponibile, calendar, orario, durataMedia, listaMedici);
    }

    /** Caso (orario non ammissibile because before apertura mattino) or (between chiusura mattina and apertura pomeriggio)*/
    private Optional<SlotDisponibile> cercaSlotAlPomeriggio(List<Visita> listaVisiteGiornaliere, FineVisita fineVisita,
                                                            SlotDisponibile slotDisponibile, Calendar calendar,
                                                            LocalTime oraAttuale, Double durataMedia, List<Medico> listaMedici){

        // Trovo le visite del mattino
        List<Visita> visiteGiornaliereDelPomeriggio = splittaListaVisiteGiornaliere.apply( orarioAperturaPomeriggio, orarioChiusuraPomeriggio, listaVisiteGiornaliere);



        // Se listaVisitePomeriggio vuota
        //      allora pianifica ad inizio pomeriggio
        // altrimenti (listaVisitePomeriggio non vuota):
        //      se fineVisitaPomeriggio is ammissibile
        //          allora accoda al pomeriggio
        //      altrimenti
        //          passa al giorno successivo
        if(visiteGiornaliereDelPomeriggio.isEmpty())
            return calcolaSlotDisponibileConListaVisiteGiornaliereVuota(calendar, slotDisponibile.getData(), oraAttuale, durataMedia, listaMedici  );

        LocalTime oraFineUltimaVisitaPomeriggio = visiteGiornaliereDelPomeriggio.get( visiteGiornaliereDelPomeriggio.size() -1 ).calcolaOraFine().toLocalTime();


        return calcolatore.isOrarioAmmissibile( oraFineUltimaVisitaPomeriggio, durataMedia ) ?

                setOrarioSlot( slotDisponibile , oraFineUltimaVisitaPomeriggio ) :

                trovaSlotGiornoSuccessivo(calendar, durataMedia, listaMedici);
    }


    /** Solo nel caso AMMISSIBILE */
    private Optional<SlotDisponibile> accodaVisitaAlMattino(List<Visita> listaVisiteGiornaliere, FineVisita fineVisita,
                                                            SlotDisponibile slotDisponibile, Calendar calendar,
                                                            LocalTime oraAttuale, Double durataMedia, List<Medico> listaMedici) {

        // Trovo le visite del mattino
        List<Visita> visiteGiornaliereDelMattino = splittaListaVisiteGiornaliere.apply( orarioAperturaMattina, orarioChiusuraMattina, listaVisiteGiornaliere);

        // Però se è vuota posso pianificare all'ora attuale (con lista visite vuota)
        if(visiteGiornaliereDelMattino.isEmpty())
            return calcolaSlotDisponibileConListaVisiteGiornaliereVuota(calendar, slotDisponibile.getData(), oraAttuale, durataMedia, listaMedici);


        // Arrivato qui, la listaVisiteMattino non è vuota: contiene almeno una visita


        Visita ultimaVisitaMattino = visiteGiornaliereDelMattino.get( visiteGiornaliereDelMattino.size() - 1);

        // TODO: Controllo che ci sia spazio in fondo al mattino

        return (calcolatore.isDurataMediaContenuta(durataMedia,
                ultimaVisitaMattino.calcolaOraFine().toLocalTime(), orarioChiusuraMattina )
                ) ?
            // Se arrivo qui significa che in fondo al mattino c'è spazio sufficiente, procedo a calcolare l'orario in funzione dell'ultima visita presente in listaVisiteMattino
            // Trovo gli orari di fine visita ultima visita della sottolista
            setOrarioSlot( slotDisponibile, ultimaVisitaMattino.calcolaOraFine().toLocalTime().plusMinutes(pausaFromvisite)) :
            // Altrimenti, non c'è spazio in fondo al mattino, considera il pomeriggio
            accodaVisitaAlPomeriggio(listaVisiteGiornaliere, fineVisita, slotDisponibile, calendar, oraAttuale, durataMedia, listaMedici);

    }

    /** Solo nel caso AMMISSIBILE */
    private Optional<SlotDisponibile> accodaVisitaAlPomeriggio(List<Visita> listaVisiteGiornaliere, FineVisita fineVisita,
                                                               SlotDisponibile slotDisponibile, Calendar calendar,
                                                               LocalTime oraAttuale, Double durataMedia, List<Medico> listaMedici) {


        // Trovo le visite del pomeriggio
        List<Visita> visiteGiornaliereDelPomeriggio = splittaListaVisiteGiornaliere.apply(orarioAperturaPomeriggio, orarioChiusuraPomeriggio, listaVisiteGiornaliere);


        // Però se è vuota posso pianificare all'ora attuale (con lista visite vuota)
        if(visiteGiornaliereDelPomeriggio.isEmpty())
            return calcolaSlotDisponibileConListaVisiteGiornaliereVuota(calendar, slotDisponibile.getData(), orarioAperturaPomeriggio, durataMedia, listaMedici);

        // TODO: forse qui devo aggiungere la condizione (la aggiungo sotto alla condizione che mi controlla se visiteGiornaliereDelPomeriggio è vuota):
        //  if(visiteGiornaliereDelPomeriggio.size() < pianificatore.getMediciMap().size()
        //      return Optional.of( setOrarioSlot( slotDisponibile, oraAperturaPomeriggio.plusMinutes(pausaFromVisite) )
        if(visiteGiornaliereDelPomeriggio.size() <= listaMedici.size())
        {
            // #################################
            // TODO:  SOSTITUIRE LA STRUTTURA DATI
            // #################################
            //pianificatore.setListaVisite( listaVisiteGiornaliere); // VECCHIO
            //pianificatore.aggiornaMediciMap(); // VECCHIO
            //slotDisponibile.setMedico(pianificatore.getPrimoMedicoDisponibile(listaVisiteGiornaliere, listaMedici)); // VECCHIO
            slotDisponibile.setMedico( codaMediciDisponibili.getPrimoMedicoDisponibile(durataMedia).getKey() ); // NUOVO

            return setOrarioSlot( slotDisponibile, orarioAperturaPomeriggio.plusMinutes(pausaFromvisite) ) ;
        }


        Visita ultimaVisitaPomeriggio = visiteGiornaliereDelPomeriggio.get(visiteGiornaliereDelPomeriggio.size() - 1);


        LocalTime oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio =
                ultimaVisitaPomeriggio != null ? ultimaVisitaPomeriggio.calcolaOraFine().toLocalTime().plusMinutes(pausaFromvisite) : null;


        return setOrarioSlot( slotDisponibile, oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio);
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

        return trovaSlotDisponibile(durataMedia,
                dataSuccessiva,
                orarioAperturaMattina,
                listaMedici,
                getAllVisiteByData(dataSuccessiva));
    }



    /** Metodo ausiliario a scopo di DRY */
    private Optional<SlotDisponibile> setOrarioSlot(SlotDisponibile slot, LocalTime orario) {
        slot.setOrario(Time.valueOf(orario));
        return Optional.of(slot);
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