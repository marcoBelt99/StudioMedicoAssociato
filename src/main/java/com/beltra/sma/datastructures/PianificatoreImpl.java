package com.beltra.sma.datastructures;


import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl;
import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.components.RisultatoAmmissibilita;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;

import com.beltra.sma.utils.FineVisita;
import lombok.Getter;

import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.*;


@Deprecated
@Getter
@Component
public class PianificatoreImpl implements Pianificatore {


    /** Lista di visite aggiunte man mano (simula la tabella a database) */
    private List<Visita> listaVisite;


    /** Mappa: < idMedico, FineVisita > <br>
        Mappa: < idMedico, {idVisita, oraFineVisita} > <br>
     * Per tutti i medici del sistema, tiene traccia di quando il medico si libera:
     * Questa mappa deve sempre essere ordinata con in testa il primo medico disponibile
     * (che si libererà prima tra i medici presenti), mentre in coda il medico che si libererà più tardi.
     * Quando un medico viene occupato in una visita, devo aggiornare l'orario di fine visita del medico
     * (che indicherà quando sarà nuovamente libero tale medico).
     *  */
    private final Map<Long, FineVisita> mediciMap;


    public PianificatoreImpl() {
        listaVisite = new ArrayList<>();
        mediciMap = new LinkedHashMap<>();
    }

    @Override
    public void aggiungiVisita(Visita visita) {
        listaVisite.add( visita );
        aggiornaMediciMap();
    }



    @Override
    public void aggiornaMediciMap() {

        List<Visita> listaVisiteOrdinate = ordinaListaVisite(listaVisite);

        /** Creazione di una mappa ausiliaria in cui inserisco i valori aggiornati */
        Map<Long, FineVisita> tempMap = new HashMap<>();

        for (Visita visitaOrdinata : listaVisiteOrdinate) {
            Long idAnagrafica = visitaOrdinata.getAnagrafica().getIdAnagrafica();

            /** Fase di aggiornamento dei valori della mappa,
             *  in particolare del campo FineVisita.oraFine
             *  ed inserimento < K, valore aggiornato > nella mappa ausiliaria
             *  */
            FineVisita fineVisita = new FineVisita( visitaOrdinata.getIdVisita(), visitaOrdinata.calcolaOraFine() );

            tempMap.put( idAnagrafica, fineVisita );
        }

        /** Ordinamento della mappa ausiliaria in base ad oraFine,
         *  e successivo inserimento nella mappa orgininale (mediciMap) */
        mediciMap.clear(); // svuoto la mappa originale
        tempMap.entrySet().stream()
                .sorted( Comparator.comparing(entry -> entry.getValue().getOraFine()))
                .forEachOrdered(entry -> mediciMap.put(entry.getKey(), entry.getValue())); // effettivo inserimento in mappa originale
    }


    @Override
    public Visita pianificaNuovaVisita(Visita Vi, Date data, Prestazione prestazione) {

        CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilita = new CalcolatoreAmmissibilitaComponentImpl();

        /** Cerco la visita che contiene il medico che si libera per primo */
        Visita Vx = searchVisitaConOraFineMinima();

        // TODO: calcolo orario corretto di inizio nuova visita
        // Vi.setOra(  ) deve rispettare gli orari di apertura / chiusura dello SMA
        // Se e' mattina e voglio creare una nuova visita che sfora l'orario di apertura SMA,
        // allora se non ce ne sono gia' pianificate devo pianificarla per il pomeriggio.
        Time oraInizio = Time.valueOf( Vx.calcolaOraFine().toLocalTime().plusMinutes( pausa5Minuti )); // Ci aggiungo la pausa di 5 minuti

        RisultatoAmmissibilita risultatoAmmissibilita = calcolatoreAmmissibilita.getRisultatoCalcoloAmmissibilitaOrario(oraInizio.toLocalTime(), prestazione.getDurataMedia());

       /** Se sforo con (oraInizio della nuova visita + durata prestazione della nuova visita) allora passo al pomeriggio.
        *  Quando sforo, è come resettare la mappa assegnando come ora di inizio le 14:05 e come id dei medici gli id dei vari medici
        *  */
        if( ( risultatoAmmissibilita.equals(RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO) ||
              calcolatoreAmmissibilita.isOrarioAfterAperturaPomeriggio( oraInizio.toLocalTime().plusMinutes( prestazione.getDurataMedia().intValue() ))
            ) &&
            ( listaVisite.stream()
                    .filter( v -> PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes(pausa5Minuti).equals( v.getOra().toLocalTime() ) )
                    .count() ) < mediciMap.size()) {
            oraInizio = Time.valueOf(PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes(pausa5Minuti));
            // TODO: devo fare che il medico diventa
        }


        Vi.setIdVisita( (long) ( getListaVisite().size()+1 ) );
        Vi.setDataVisita( data );
        Vi.setAnagrafica( Vx.getAnagrafica() );
        Vi.setOra( oraInizio );
        Vi.setNumAmbulatorio( Vx.getNumAmbulatorio() );
        Vi.setPrestazione( prestazione ); // 2h


        /** Aggiungo Vi alla lista di visite */
        aggiungiVisita( Vi );

        return Vi;
    }


    @Override
    public Medico getPrimoMedicoDisponibile(List<Visita> listaVisiteGiornaliere, List<Medico> listaMedici) {

        if(listaVisiteGiornaliere.isEmpty())
            return listaMedici.get(0);

        Long idMedicoDaAssegnare;

        /// ESSENZIALE:
        ///     Setto il pianificatore con l'insieme di visite da cui basarsi per ricercare il medico
        ///     Aggiorno la mediciMap del pianificatore
        setListaVisite( listaVisiteGiornaliere );
        aggiornaMediciMap();


        /// Se in listaMedici ho un medico libero in lista, perchè ho che listaVisiteGiornaliere = [v1, v2],
        /// Mentre listaMedici = [m1, m2, m3], allora m1 ed m2 sono occupati, ma m3 no!!
        List<Long> listaIdMediciLiberi = listaMedici
                .stream()
                .map(Medico::getIdAnagrafica) // estraggo e costruisco la lista dei soli id
                .filter(idAnagrafica -> !mediciMap.containsKey(idAnagrafica) )// mantengo solo quelli non presenti nella mappa
                .toList();


        /// CERCO IL MEDICO DA ASSEGNARE
        ///     Se ho un qualche medico libero allora assegno quello,
        ///     altrimenti scelgo quello che si libera per primo  USANDO LE STRUTTURE DATI DEL PIANIFICAZIONE MANAGER
        if(!listaIdMediciLiberi.isEmpty()) {
            idMedicoDaAssegnare = listaIdMediciLiberi.get(0);

            /// AGGIUNGO IN MAPPA IL MEDICO CHE PRIMA ERA LIBERO?
            mediciMap.put(idMedicoDaAssegnare,
                    new FineVisita(0L,
                            ordinaListaVisite( listaVisiteGiornaliere )
                            .get(listaVisiteGiornaliere.size()-1)
                            .calcolaOraFine())
            );
        } else
            idMedicoDaAssegnare =  getMediciMap().keySet().iterator().next(); // scelgo il primo medico (come risaputo, sono sicuro che sia il primo medico della mappa)


        return listaMedici.stream()
                .filter(medico -> medico.getIdAnagrafica().equals(idMedicoDaAssegnare))
                .findFirst()
                .get();

    }


    @Override
    public Map<Long, FineVisita> getMediciMap() {
        return Collections.unmodifiableMap( mediciMap ); // Restituisce la mappa immutabile
    }


    @Override
    public List<Visita> getListaVisite() {
        return  this.listaVisite ; // Restituisce la lista
    }

    @Override
    public void setListaVisite(List<Visita> listaVisite) {
        this.listaVisite = listaVisite;
    }

    @Override
    public void stampaListaAndMappa() {
        System.out.println("VISITE DI TEST:\n");
        getListaVisite().forEach(System.out::println);

        System.out.println("\nMAPPA ORDINATA: ");
        getMediciMap().forEach( (id, oraFine) -> System.out.println("\nID MEDICO: " + id + ", Ora Fine: " + oraFine) );
    }

    @Override
    public void clear() {
        listaVisite.clear();
        mediciMap.clear();
    }

    @Override
    public long getPausa5Minuti() {
        return pausa5Minuti;
    }



    /** Tra le visite attualmente memorizzate nella tabella "visite", seleziona quella avente orario di fine visita minore.
     * <br> Tale visita trovata servira' poi per calcolare l'orario di inizio della visita che si vuole inserire. */
    private Visita searchVisitaConOraFineMinima() {

        /**
         * Prendo il primo elemento di mediciMap:
         * essendo ordinata, sono sicuro che la visita minima sia sempre in testa
         */
        Long idVisitaConOrarioFineMinimo =  this.mediciMap.entrySet().stream().findFirst().orElseThrow().getValue().getIdVisita();

        /** Dalla "tabella" di tutte le visite, recupero la visita associata che ha orario fine inferiore tramite idVisita */
        return this.listaVisite
                .stream()
                .filter( v -> v.getIdVisita().equals( idVisitaConOrarioFineMinimo))
                .findFirst()
                .orElseThrow();
    }

    /** Ordina listaVisite sulla base del risultato della funzione calcolaOraFine della classe Visita */
    private List<Visita> ordinaListaVisite(List<Visita> listaVisite) {

        /// Lista di appoggio per ordinamento delle visite in base a oraFine
        List<Visita> listaVisiteOrdinate = new ArrayList<>( listaVisite );
        listaVisiteOrdinate.sort( Comparator.comparing( Visita::calcolaOraFine ) );

        return listaVisiteOrdinate;

    }


}