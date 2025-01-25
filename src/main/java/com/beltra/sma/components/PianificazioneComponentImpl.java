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



    public Optional<SlotDisponibile> trovaPrimoSlotDisponibile(Double durataMedia,
                                                               Date dataAttuale,
                                                               LocalTime oraAttuale,
                                                               List<Medico> listaMedici,
                                                               List<Visita> listaVisiteGiornaliere) {

        // Ottengo una istanza di Calendar per poter sfruttarne i metodi
        Calendar calendar = Calendar.getInstance();

        calendar.setTime( dataAttuale );

        // TODO: LOGICA: SE NON È RISPETTATA L'AMMISSIBILITÀ DEGLI ORARI DI APERTURA E CHIUSURA METTO TUTTO IN CODA.


        /** Ciclo vero e proprio */
        while (true) {

            // Finchè il giorno non è ammissibile incrementi il calendario
            while (!calcolatore.isGiornoAmmissibile( dataAttuale ))
                calendar.add(Calendar.DATE, 1);

            // Se sono arrivato qui, il giorno è ammissibile

/// ####################################################################################################################################
        // TODO: CASO LISTA VISITE GIORNALIERE VUOTA

            if (listaVisiteGiornaliere.isEmpty())
                return calcolaSlotDisponibileConListaVisiteVuota(calendar, dataAttuale, oraAttuale, durataMedia, listaMedici);

/// ####################################################################################################################################
            // TODO: CASO LISTA VISITE GIORNALIERE NON VUOTA
            //  Devo recuperare tramite mediciMap il primo medico disponibile sulla base della lista di visite
            Medico medicoLiberato = new Medico();
            medicoLiberato = pianificazioneManager.getPrimoMedicoDisponibile( listaVisiteGiornaliere );

            // RECUPERO DALLA MAPPA ANCHE LA VISITA AVENTE ORARIO DI FINE MINORE
            FineVisita fineVisita = pianificazioneManager.getMediciMap().get(medicoLiberato.getIdAnagrafica());

            // USO TALE ORA TROVATA PER CALCOLARMI L'INIZIO DELLA PROSSIMA VISITA (che sarà 5 minuti dopo la visita recuperata dalla mappa)
            LocalTime oraInizioPrevistaProssimaVisita = fineVisita.getOraFine().toLocalTime().plusMinutes(pausaFromvisite);

            // TODO: Aggiorno mediciMap ?
            pianificazioneManager.aggiornaMediciMap();


            // A QUESTO PUNTO DOVREI AVERE TUTTI I DATI PER POPOLARE LO SlotDisponibile
            // DEVO SOLO CONTROLLARE AMMISSIBILITA' ORARIO INIZIO PROSSIMA VISITA
            Risultato risultatoCalcoloAmmissibilita = calcolatore.getRisultatoCalcoloAmmissibilitaOrario( oraInizioPrevistaProssimaVisita, durataMedia );
            boolean isOrarioAmmissibile =
                    ( calcolatore.isOrarioAmmissibile(oraInizioPrevistaProssimaVisita, durataMedia) ) &&
                    ( risultatoCalcoloAmmissibilita== Risultato.AMMISSIBILE );

            if ( isOrarioAmmissibile )
                return Optional.of(new SlotDisponibile( dataAttuale, Time.valueOf(oraInizioPrevistaProssimaVisita), medicoLiberato)); // OK, È AMMISSIBILE
            else {
                // ALTRIMENTI, SE GIUNGO QUI SIGNIFICA CHE L'ORARIO CALCOLATO NON È AMMISSIBILE

                List<Visita> visiteGiornaliereDelMattino =
                        dividiListaVisiteGiornaliere.apply(orarioAperturaMattina, orarioChiusuraMattina, listaVisiteGiornaliere);
                List<Visita> visiteGiornaliereDelPomeriggio =
                        dividiListaVisiteGiornaliere.apply(orarioAperturaPomeriggio, orarioChiusuraPomeriggio, listaVisiteGiornaliere);

                Visita ultimaVisitaMattino =
                        !visiteGiornaliereDelMattino.isEmpty() ?  visiteGiornaliereDelMattino.get(visiteGiornaliereDelMattino.size() - 1) : null;

                Visita ultimaVisitaPomeriggio =
                        !visiteGiornaliereDelPomeriggio.isEmpty() ? visiteGiornaliereDelPomeriggio.get(visiteGiornaliereDelPomeriggio.size() - 1) : null;

                // Per comodità mi trovo gli orari di fine visita ultima visita di entrambe le sottoliste
                LocalTime oraInizioPrevistaProssimaVisitaMattinoFromUltimaVisitaMattino =
                       ultimaVisitaMattino != null ? ultimaVisitaMattino.calcolaOraFine().toLocalTime().plusMinutes(pausaFromvisite) : null;
                LocalTime oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio =
                        ultimaVisitaPomeriggio != null ? ultimaVisitaPomeriggio.calcolaOraFine().toLocalTime().plusMinutes(pausaFromvisite) : null;


                // ESSENDO NON AMMISSIBILE, DEVO CAPIRNE LA CAUSA
                switch (risultatoCalcoloAmmissibilita) {

                    case NO_BECAUSE_BEFORE_APERTURA_MATTINA -> {

                        // VEDO SE C'È POSTO IN FONDO NELLE VISITE DEL MATTINO
                        if ( oraInizioPrevistaProssimaVisitaMattinoFromUltimaVisitaMattino != null &&
                             pianificazioneManager.isDurataMediaContenuta.test(durataMedia,
                             oraInizioPrevistaProssimaVisitaMattinoFromUltimaVisitaMattino,
                             orarioChiusuraMattina))

                            // OK, SI PUÒ METTERE UNO SLOT QUI IN FONDO AL MATTINO
                            return getSlotDisponibile( visiteGiornaliereDelMattino, dataAttuale,
                                    oraInizioPrevistaProssimaVisitaMattinoFromUltimaVisitaMattino );


                        // SE ARRIVO QUI SIGNIFICA CHE SFORO, QUINDI DEVO PASSARE AL POMERIGGIO,

                        // VEDO SE C'È POSTO ALL'INIZIO DEL POMERIGGIO
                        if( visiteGiornaliereDelPomeriggio.isEmpty() )
                            return occupiesFirstSlot( dataAttuale,
                                    Time.valueOf( orarioAperturaPomeriggio.plusMinutes(pausaFromvisite) ),
                                    /*medicoService.getFirstMedico() */ listaMedici.get(0) );

                        // OPPURE VEDO SE C'È POSTO IN FONDO AL POMERIGGIO
                        if ( oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio != null &&
                             pianificazioneManager.isDurataMediaContenuta.test(durataMedia,
                             oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio,
                             orarioChiusuraPomeriggio))

                            // OK, SI PUÒ METTERE UNO SLOT QUI IN FONDO AL POMERIGGIO
                            return getSlotDisponibile(visiteGiornaliereDelPomeriggio, dataAttuale,
                                    oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio);

                        // SE ARRIVO QUI ... ?
                    }
                    case NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO -> {

                        // SE ARRIVO QUI SIGNIFICA CHE SFORO, QUINDI DEVO PASSARE AL POMERIGGIO

                        // CONTROLLO SE C'È POSTO ALL'INIZIO
                        if( visiteGiornaliereDelPomeriggio.isEmpty() )
                            return occupiesFirstSlot( dataAttuale,
                                    Time.valueOf( orarioAperturaPomeriggio.plusMinutes(pausaFromvisite) ),
                                    /*medicoService.getFirstMedico() */ listaMedici.get(0) );
                        // OPPURE METTO ALLA FINE
                        if (oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio != null &&
                            pianificazioneManager.isDurataMediaContenuta.test(durataMedia,
                            oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio,
                            orarioChiusuraPomeriggio))

                            // OK, SI PUÒ METTERE UNO SLOT QUI IN FONDO AL POMERIGGIO
                            return getSlotDisponibile(visiteGiornaliereDelPomeriggio, dataAttuale,
                                    oraInizioPrevistaProssimaVisitaPomeriggioFromUltimaVisitaPomeriggio);
                    }


                } // fine switch


                System.out.println();

                // TODO: Passa al giorno successivo
                calendar.add( Calendar.DATE, 1);
                oraAttuale = orarioAperturaMattina; // Reset orario per il nuovo giorno (credo che serva!)

                dataAttuale = calendar.getTime();

                // TODO: ritorno lo slot dispoinbile ?? (non credo)

            }


            // Ad ogni iterazione trovo le nuove visite giornaliere
            listaVisiteGiornaliere = visitaService.getAllVisiteByData( dataAttuale );

        } // fine while(true)
    } // fine metodo






    /**
     * SE LA LISTA È VUOTA, SIGNIFICA CHE PER OGGI NON HO ANCORA ASSEGNATO ALCUNA VISITA
          -> COME MEDICO ASSEGNO IL PRIMO DELLA LISTA DEI MEDICI DISPONIBILI NEL SISTEMA
              -> SOLO SE L'ORARIO ATTUALE È AMMISSIBILE
                -> SE NOW() == dataAttuale ALLORA DEVO VERIFICARE CHE NOW()+durataMedia+5min SIA AMMISSIBILE
                   ALTRIMENTI CONTROLLA AMMISSIBILITÀ SOLO DI NOW() */
    private Optional<SlotDisponibile> calcolaSlotDisponibileConListaVisiteVuota(Calendar calendar, Date dataAttuale, LocalTime oraAttuale, Double durataMedia, List<Medico> listaMedici) {

        SlotDisponibile slotDisponibile = new SlotDisponibile();
        slotDisponibile.setMedico( /*medicoService.getFirstMedico() */ listaMedici.get(0) ); // posso tranquillamente assegnare il primo medico
        slotDisponibile.setData( dataAttuale ); // e posso mettere la data odierna

    // SE (oraAttuale+durataMedia) È AMMISSIBILE:
    //  SE OGGI È LO STESSO GIORNO CHE STO CONSIDERANDO NELLA PIANIFICAZIONE (CIOÈ NON SONO AVANZATO CON I GIORNI,
    //  nel senso che dataDiRicerca non è diventata pari ai giorni successivi rispetto alla data di adesso)
    //      ALLORA VERIFICO ANCHE L'ORARIO DI ADESSO SIA AMMISSIBILE, E SE VALE CIÒ ALLORA COME ORA DELLO SLOT DISPONIBILE ASSEGNO NOW() + 5MIN
    //      dataDiRicerca: è la data che si incrementa di 1 giorno ad ogni iterazione nel while(true)
    //      now(): Per recuperare la data in questo momento invece uso GregorianCalendar.getInstance().getTime()
    //

        Risultato risultatoCalcoloAmmissibilita = calcolatore.getRisultatoCalcoloAmmissibilitaOrario( oraAttuale, durataMedia );
        boolean isOrarioAmmissibile =
                ( calcolatore.isOrarioAmmissibile( oraAttuale, durataMedia ) ) &&
                ( risultatoCalcoloAmmissibilita== Risultato.AMMISSIBILE );

        if(isOrarioAmmissibile) {

            slotDisponibile.setOrario(
                calcolatore.isStessoGiorno.test(dataAttuale, calendar.getTime() ) ?
                    // Se dataAttuale == now(), assegno con oraAttuale+5min
                    Time.valueOf( oraAttuale.plusMinutes(durataMedia.intValue()).plusMinutes(pausaFromvisite) ) :
                    // Altrimenti, sono ad Es. nel giorno successivo, posso impostare come orario le 07:05 (appunto perchè
                   //  dataDiRicerca è > now() e, come controllato prima, non ci sono neppure visite.
                    Time.valueOf( orarioAperturaMattina.plusMinutes(pausaFromvisite) )
                    );
            return Optional.of(slotDisponibile); // quindi posso ritornare lo slot disponibile
        } else {

    // ALTRIMENTI (SE ORARIO NON È AMMISSIBILE), DEVO ANALIZZARNE IL MOTIVO:
            switch ( risultatoCalcoloAmmissibilita ) {
                // PERCHÈ PRIMA DELL'ORA DI APERTURA MATTINA:
                case NO_BECAUSE_BEFORE_APERTURA_MATTINA -> {
                    // PROPONGO COME ORA DELLO SLOT oraAperturaMattina+5min ( dal momento che listaVisiteGiornaliere = [] )
                    slotDisponibile.setOrario(Time.valueOf(orarioAperturaMattina.plusMinutes(pausaFromvisite)));
                    return Optional.of(slotDisponibile);
                }
                // PERCHÈ ORA COMPRESA IN PAUSA PRANZO
                case NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO -> {
                    // PROPONGO COME ORA DELLO SLOT oraAperturaPomeriggio+5min
                    slotDisponibile.setOrario(Time.valueOf(orarioAperturaPomeriggio.plusMinutes(pausaFromvisite)));
                    return Optional.of(slotDisponibile);
                }
                // PERCHÈ OLTRE ORA CHIUSURA POMERIGGIO
                case NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO ->
                    // PASSA AL GIORNO SUCCESSIVO
                    calendar.add(Calendar.DATE, 1);
            }

        } // fine else orario non ammissibile

    // SONO ARRIVATO IN FONDO AL METODO  E NON HO TROVATO LO SLOT DISPONIBILE,
    // QUINDI RITORNO VUOTO
    return Optional.empty();
    }



    /**  */
    private Optional<SlotDisponibile> getSlotDisponibile(List<Visita> sottolistaVisiteGiornaliere, Date dataDiRicerca, LocalTime oraInizioPrevistaProssimaVisitaFromUltimaVisitaInSottolista) {

        // ESSENZIALE: Setto il pianificazioneManager con l'insieme di visite da cui basarsi per ricercare il medico
        pianificazioneManager.setListaVisite( sottolistaVisiteGiornaliere ); // seleziono solo quelle del mattino / pomeriggio

        pianificazioneManager.aggiornaMediciMap(); // ESSENZIALE: Aggiorno la mediciMap del pianificazioneManager

        // CERCO IL MEDICO DA ASSEGNARE USANDO LE STRUTTURE DATI DEL PIANIFICAZIONE MANAGER
        Medico medicoLiberato = pianificazioneManager.getPrimoMedicoDisponibile( sottolistaVisiteGiornaliere );

        return Optional.of(new SlotDisponibile(dataDiRicerca, Time.valueOf(oraInizioPrevistaProssimaVisitaFromUltimaVisitaInSottolista), medicoLiberato));
    }




    Optional<SlotDisponibile> occupiesFirstSlot(Date data, Time time, Medico medico) {
        return Optional.of(new SlotDisponibile(data, time, medico));
    }



    /** TriPredicate e' una interfaccia funzionale che ho creato io per poter usare più parametri nel mio predicato.
     * <br>
     *  Divide la lista di visite che prende come parametro nella sottolista di tutte le visite comprese tra oraInizio e oraFine.
     *  */
    public TriFunction<LocalTime, LocalTime, List<Visita>, List<Visita>> dividiListaVisiteGiornaliere =
            (oraInizio, oraFine, listaVisiteGiornaliere) ->
                    listaVisiteGiornaliere.stream()
                            .filter(v -> v.getOra().after(Time.valueOf(oraInizio))
                                    && v.getOra().before(Time.valueOf(oraFine)))
                            .toList();



    @Override
    /** In questo metodo inserisco i dati di test per i vari casi di test specifici.
     *  In fase di produzione invece e' consigliato usare il metodo getAllVisiteSt */
    public List<Visita> getAllVisiteByData() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Data attuale
        Date dataCorrente = calendar.getTime();
        return visitaService.getAllVisiteByData( dataCorrente );
    }



}