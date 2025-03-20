package com.beltra.sma.components.data;


import com.beltra.sma.datastructures.Pianificatore;
import com.beltra.sma.datastructures.PianificatoreImpl;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.VisitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class DatiVisiteTest implements DatiTest<Visita> {


    private final DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
    private final DatiAnagraficheTest  datiAnagraficheTest = new DatiAnagraficheTest();
    private final DatiMediciTest datiMediciTest = new DatiMediciTest();


    private final Pianificatore pianificatore = new PianificatoreImpl();

    @Autowired
    private VisitaService visitaService;

    public List<Prestazione> getListaPrestazioniTest() {return datiPrestazioniTest.getDatiTest(); }
    public List<Anagrafica> getListaAnagraficheTest() {return  datiAnagraficheTest.getDatiTest(); }
    public List<Medico> getListaMediciTest() { return datiMediciTest.getDatiTest(); }


    public List<Visita> getDatiTest()  {
        return getListaVisiteTest();
    }

    public List<Visita> getListaDiUnaVisita() {

        for(int i=0;i < getListaMediciTest().size()-2;i++) { // faccio solo 1 giro nel for

            Visita visita = new Visita();

            visita.setIdVisita( (long) (i+1) );
            visita.setAnagrafica( getListaAnagraficheTest().get( i ) );
            visita.setDataVisita( dataAttualeDiTest );
            visita.setOra( Time.valueOf( LocalTime.of(7,0).plusMinutes( pianificatore.getPausa5Minuti() ) ));
            visita.setPrestazione( getListaPrestazioniTest().get( i ) ); // ad ogni iterazione prendi una determinata durata che ho scelto io prima
            visita.setNumAmbulatorio( i+1 );

            // Aggiungo la visita sia alla listaVisite sia alla mappa visiteMap
            pianificatore.aggiungiVisita( visita );

        }


        return pianificatore.getListaVisite();
    }

    public List<Visita> getListaDiDueVisite() {
        PianificatoreImpl pmLocal = new PianificatoreImpl(); // TODO: Nuova istanza

        for(int i=0;i < getListaMediciTest().size()-1;i++) { // faccio solo 2 giri nel for

            Visita visita = new Visita();

            visita.setIdVisita( (long) (i+1) );
            visita.setAnagrafica( getListaAnagraficheTest().get( i ) );
            visita.setDataVisita( dataAttualeDiTest );
            visita.setOra( Time.valueOf( LocalTime.of(7,0).plusMinutes( pianificatore.getPausa5Minuti() ) ));
            visita.setPrestazione( getListaPrestazioniTest().get( i ) ); // ad ogni iterazione prendi una determinata durata che ho scelto io prima
            visita.setNumAmbulatorio( i+1 );

            // Aggiungo la visita sia alla listaVisite sia alla mappa visiteMap
            pmLocal.aggiungiVisita( visita );

        }


        return pmLocal.getListaVisite();
    }


    public List<Visita> getListaVisiteTest() {

        /** Scelgo un caso rappresentativo:
         *  Come data attuale scelgo: venerdì 17 gennaio: le visite devono essere schedulate
         *  entro gli orari di apertura di mattina e pomeriggio, e se una visita poi sfora la
         *  giornata di venerdì, deve essere assegnata a lunedì 20.
         * */
        List<Anagrafica> listaAnagrafiche = getListaAnagraficheTest(); // Medici (indici): 0  1  2
//        List<Prestazione> listaPrestazioni = getListaPrestazioniTest();
        List<Prestazione> listaPrestazioni = getListaPrestazioniTest();
        // listaPrestazioni.durate = { 15min, 2h, 3h, 4h, 4.5h, 5h, 20min }
        //  (indici)                     0     1   2   3    4    5   6


        // Lista di indici per assegnare le prestazioni che desidero alle visite di inizio giornata
        List<Integer> listaIndiciPrestazioniIniziali = Arrays.asList(0, 2, 1);
        List<Prestazione> listaPrestazioniIniziali = new ArrayList<>(); // Prime prestazioni da assegnare alle visite di inizio giornata

        listaIndiciPrestazioniIniziali.forEach(i -> listaPrestazioniIniziali.add( listaPrestazioni.get(i) ) );

//        /** Data di test */
//        Date dataAttuale = new GregorianCalendar(2025, Calendar.JANUARY, 17 ).getTime();

        /** TODO: Act */

        // TODO: 1)
        // Se sono alla prima visita del giorno, finchè non si sono occupati tutti i medici in una visita, allora assegno sempre
        // come ora le 07:00, maggiorata dei 5 minuti (quindi 07:05).
        for(int i=0;i < getListaMediciTest().size();i++) {

            Visita visita = new Visita();

            visita.setIdVisita( (long) (i+1) );
            visita.setAnagrafica( listaAnagrafiche.get( i ) );
            visita.setDataVisita( dataAttualeDiTest );
            visita.setOra( Time.valueOf( LocalTime.of(7,0).plusMinutes( pianificatore.getPausa5Minuti() ) ));
            visita.setPrestazione( listaPrestazioniIniziali.get( i ) ); // ad ogni iterazione prendi una determinata durata che ho scelto io prima
            visita.setNumAmbulatorio( i+1 );

            // Aggiungo la visita sia alla listaVisite sia alla mappa visiteMap
            pianificatore.aggiungiVisita( visita );

        }


        // Inizio ad inserire le visite


        Visita v4 = new Visita();
        v4 = pianificatore.pianificaNuovaVisita( v4, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        Visita v5 = new Visita();
        v5 = pianificatore.pianificaNuovaVisita( v5, dataAttualeDiTest, listaPrestazioni.get(6) ); // 20 min

        Visita v6 = new Visita();
        v6 = pianificatore.pianificaNuovaVisita( v6, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        // Stampa a video per chiarezza
        pianificatore.stampaListaAndMappa();

        return pianificatore.getListaVisite();

    }

    public List<Visita> getListaVisiteWithMattinoFull(){

        List<Visita> listaVisite = getListaVisiteTest();

        pianificatore.setListaVisite( listaVisite );

        Visita v7 = new Visita();
        pianificatore.pianificaNuovaVisita(v7, dataAttualeDiTest, getListaPrestazioniTest().get(0) ); // 15 min

        Visita v8 = new Visita();
        pianificatore.pianificaNuovaVisita(v8, dataAttualeDiTest, getListaPrestazioniTest().get(1) ); // 2 ore

        return pianificatore.getListaVisite();
    }











    /** Legge il file CSV di tutte le visite di test */
    public List<Visita> getListaDiSetteVisite() {


        // Medici (indici): 0  1  2
        // listaPrestazioni.durate = { 15min, 2h, 3h, 4h, 4.5h, 5h, 20min }
        //  (indici)                     0     1   2   3    4    5   6

        List<Anagrafica> listaAnagrafiche = getListaAnagraficheTest();
        List<Prestazione> listaPrestazioni = getListaPrestazioniTest();
        List<Visita> listaVisite = new ArrayList<>(getListaVisiteTest());

        Visita v7 = new Visita();

        return listaVisite;
    }

    /** Ritorna una serie di visite che rendono piena la lista di visite al mattino.
     * <br>
     * Serve per testare se la prossima visita viene programmata ad oraAperturaPomeriggio e con medico il primo*/
    public List<Visita> getListaVisiteMattinoFull() {
        return null;
    }

    public List<Visita> getListaVisite_OF_DATABASE() {
        return visitaService.getAll();
    }
}
