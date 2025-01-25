package com.beltra.sma.components;

import com.beltra.sma.components.data.*;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
/** Classe di utilita' che genera liste di dati utili per la classe di test {@link PianificazioneComponentTest}  */
public class PianificazioneManagerTest {


    private final DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
    private final DatiAnagraficheTest datiAnagraficheTest = new DatiAnagraficheTest();
    private final DatiMediciTest datiMediciTest = new DatiMediciTest();

    @Autowired
    private DatiVisiteTest datiVisiteTest;

    /** Per svolgere le principali operazioni della struttura dati pianificazioneManagerPerTesting */


    @Autowired
    //    private PianificazioneManagerPerTesting pianificazioneManager;
    private PianificazioneManager pianificazioneManager;

    public List<Prestazione> getListaPrestazioniTest() {return datiPrestazioniTest.getDatiTest();}
    public List<Anagrafica>  getListaAnagraficheTest() {return  datiAnagraficheTest.getDatiTest();}
    public List<Medico>      getListaMediciTest()      {return datiMediciTest.getDatiTest();}
    public List<Visita>      getListaVisiteTest()      {return  datiVisiteTest.getDatiTest();}

    public List<Visita>     getListaVisiteTest_ForPianificazioneAfterOraAperturaPomeriggio() {return datiVisiteTest.getListaVisiteTest_ForPianificazioneAfterOraAperturaPomeriggio();}

    public List<Visita>     getListaVisite_OF_DATABASE() {return datiVisiteTest.getListaVisite_OF_DATABASE(  );}

    /** Data di test attuale */
    public static final  Date dataAttualeDiTest = DatiTest.dataAttualeDiTest;
    // public static final  Date dataAttualeDiTest = new GregorianCalendar(2025, Calendar.JANUARY, 16 ).getTime();




    @Test
    public void testPianificazioneManager_pianificaVisita_isWorkingOk() {

    /** TODO: Arrange */
    /** Scelgo un caso rappresentativo:
     *  Come data attuale scelgo: venerdì 17 gennaio: le visite devono essere schedulate
     *  entro gli orari di apertura di mattina e pomeriggio, e se una visita poi sfora la
     *  giornata di venerdì, deve essere assegnata a lunedì 20.
     * */
        List<Anagrafica> listaAnagrafiche = getListaAnagraficheTest(); // Medici (indici): 0  1  2
//        List<Prestazione> listaPrestazioni = getListaPrestazioniTest();
          List<Prestazione> listaPrestazioni = datiPrestazioniTest.getDatiTest();

        // listaPrestazioni.durate = { 15min, 2h, 3h, 4h, 4.5h, 5h, 20min }
        //  (indici)                     0     1   2   3    4    5   6


        // Lista di indici per assegnare le prestazioni che desidero alle visite di inizio giornata
        List<Integer> listaIndiciPrestazioniIniziali = Arrays.asList(0, 2, 1);
        List<Prestazione> listaPrestazioniIniziali = new ArrayList<>(); // Prime prestazioni da assegnare alle visite di inizio giornata

        listaIndiciPrestazioniIniziali.forEach(i -> listaPrestazioniIniziali.add( listaPrestazioni.get(i) ) );



        /** TODO: Act */

        // TODO: 1)
        // Se sono alla prima visita del giorno, finchè non si sono occupati tutti i medici in una visita, allora assegno sempre
        // come ora le 07:00, maggiorata dei 5 minuti (quindi 07:05).
        for(int i=0;i < getListaMediciTest().size();i++) {

            Visita visita = new Visita();

            visita.setIdVisita( (long) (i+1) );
            visita.setAnagrafica( listaAnagrafiche.get( i ) );
            visita.setDataVisita(dataAttualeDiTest);
            visita.setOra( Time.valueOf( LocalTime.of(7,0).plusMinutes( pianificazioneManager.getPausa5Minuti() ) ));
            visita.setPrestazione( listaPrestazioniIniziali.get( i ) ); // ad ogni iterazione prendi una determinata durata che ho scelto io prima
            visita.setNumAmbulatorio( i+1 );

           // Aggiungo la visita sia alla listaVisite sia alla mappa visiteMap
            pianificazioneManager.aggiungiVisita( visita );

        }


        System.out.println();
        // TODO: 2)
        // Inizio ad inserire le visite


        Visita v4 = new Visita();
        pianificazioneManager.pianificaNuovaVisita( v4, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        Visita v5 = new Visita();
        pianificazioneManager.pianificaNuovaVisita( v5, dataAttualeDiTest, listaPrestazioni.get(6) ); // 20 min

        Visita v6 = new Visita();
        pianificazioneManager.pianificaNuovaVisita( v6, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        // Stampa a video per chiarezza
        pianificazioneManager.stampaListaAndMappa();

        /** TODO: Assert */
        Assertions.assertEquals( Set.of(3L, 2L, 1L),  pianificazioneManager.getMediciMap().keySet());
    }




    @Test
    public void testPianificazioneManager_pianificaVisitaWithCheck_isWorkingOk() {
        List<Visita> listaVisiteDB = getListaVisite_OF_DATABASE();

        assertNotNull( listaVisiteDB );

    }


    @Test
    public void testPianificazioneManager_PianificaVisitaAfterOraAperturaPomeriggio() {
        List<Visita> listaVisite = getListaVisiteTest_ForPianificazioneAfterOraAperturaPomeriggio();

        assertNotNull( listaVisite );
    }






}
