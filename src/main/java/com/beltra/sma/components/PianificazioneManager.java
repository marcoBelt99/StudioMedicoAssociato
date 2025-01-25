package com.beltra.sma.components;

import com.beltra.sma.functional.TriPredicate;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.MedicoService;
import com.beltra.sma.utils.FineVisita;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;


/** Struttura dati che offre operazioni di inserimento visite automatico per Unit Tests.
 *  <br> */

@Getter
@Component
public class PianificazioneManager {



    /** Lista di visite aggiunte man mano (simula la tabella a database) */
    @Setter
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

    @Getter
    private long pausa5Minuti = 5;

    @Autowired
    private MedicoService medicoService;

    public PianificazioneManager() {
        listaVisite = new ArrayList<>();
        mediciMap = new LinkedHashMap<>();
    }


    /**  Metodo per aggiungere una visita sia in listaVisite sia nella mappa <br>
     *   L'ordine di inserimento e' importante: e' obbligatorio inserire prima in lista
     *   e successivamente in Mappa.*/
    public void aggiungiVisita(Visita visita) {
        listaVisite.add( visita );
        aggiornaMediciMap();
    }



    private List<Visita> ordinaListaVisite(List<Visita> listaVisite) {

        // TODO: Lista di appoggio per ordinamento delle visite in base a oraFine
        List<Visita> listaVisiteOrdinate = new ArrayList<>( listaVisite );
        listaVisiteOrdinate.sort( Comparator.comparing( Visita::calcolaOraFine ) ); // ordino sulla base del risultato della funzione calcolaOraFine della classe Visita

       return listaVisiteOrdinate;

    }


    /** Aggiorno la mappa mediciMap sulla base delle visite appena inserite */
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



    /** @param Vi Nuova Visita da inserire.
     * @param data Data Visita (a partire da oggi).
     *  <br>
     *   #########################<br>
     *   ** METODO ORIGINALE **<br>
     *   #########################<br>
     *  Costruisce Vi sulla base di Vx: <br>
     *  Vx = visita su cui ci si sta basando (e' quella con orario di fine minimo) */
    public Visita pianificaNuovaVisita(Visita Vi, Date data, Prestazione prestazione) {

        /** Cerco la visita che contiene il medico che si libera per primo */
        Visita Vx = searchVisitaConOraFineMinima();


        // TODO: calcolo orario corretto di inizio nuova visita
        // Vi.setOra(  ) deve rispettare gli orari di apertura / chiusura dello SMA
        // Se e' mattina e voglio creare una nuova visita che sfora l'orario di apertura SMA,
        // allora se non ce ne sono gia' pianificate devo pianificarla per il pomeriggio.
        Time oraInizio = Time.valueOf( Vx.calcolaOraFine().toLocalTime().plusMinutes( pausa5Minuti )); // Ci aggiungo la pausa di 5 minuti



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



    /** TriPredicate e' una interfaccia funzionale che ho creato io per poter usare più parametri nel mio predicato.
     * <br>
     * Controlla se durataMedia è contenuta in oraFine ed orarioChiusura. */
    public TriPredicate<Double, LocalTime, LocalTime> isDurataMediaContenuta = (durataMedia, oraFine, orarioChiusura)  -> {

        // Calcolo la differenza in minuti tra oraFine (maggiorata di 5 minuti) e ora chiusura
        // nota che non e' specificato se chiusura mattino o pomeriggio, lo scelgo in fase di chiamata
        long differenzaInMinuti = Duration.between( oraFine.plusMinutes(5), orarioChiusura ).toMinutes();
        return differenzaInMinuti > durataMedia;
    };



    public Medico getPrimoMedicoDisponibile(List<Visita> listaVisiteGiornaliere) {
        // ESSENZIALE: Setto il pianificazioneManager con l'insieme di visite da cui basarsi per ricercare il medico
        setListaVisite( listaVisiteGiornaliere );
        // ESSENZIALE: Aggiorno la mediciMap del pianificazioneManager
        aggiornaMediciMap();

        // CERCO IL MEDICO DA ASSEGNARE USANDO LE STRUTTURE DATI DEL PIANIFICAZIONE MANAGER
        Long idMedicoDaAssegnare = getMediciMap().keySet().iterator().next(); // scelgo il primo medico (come risaputo, sono sicuro che sia il primo medico della mappa)

        return medicoService.getMedicoByIdAnagrafica(idMedicoDaAssegnare);
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



    public Map<Long, FineVisita> getMediciMap() {
        return Collections.unmodifiableMap( mediciMap ); // Restituisce la mappa immutabile
    }


    // Metodo per ottenere la lista di tutte le visite
    public List<Visita> getListaVisite() {
        return Collections.unmodifiableList( listaVisite ); // Restituisce la lista immutabile
    }


    public void stampaListaAndMappa() {
        System.out.println("VISITE DI TEST:\n");
        getListaVisite().forEach(v -> {
            System.out.println(v);
            System.out.println();
        });


        // Stampa la mappa ordinata
        System.out.println("\nMAPPA ORDINATA: ");
        getMediciMap().forEach( (id, oraFine) -> System.out.println("\nID MEDICO: " + id + ", Ora Fine: " + oraFine) );
    }


}