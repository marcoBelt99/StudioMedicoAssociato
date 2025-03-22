package com.beltra.sma.datastructures;

import com.beltra.sma.components.pianificazionecomponent.PianificazioneComponentTest;
import com.beltra.sma.components.data.*;
import com.beltra.sma.components.data.DatiVisiteTest;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.sql.Time;
import java.time.LocalTime;
import java.util.*;



/** Classe di utilita' che genera liste di dati utili per la classe di test {@link PianificazioneComponentTest}  */
@SpringBootTest
public class PianificatoreTests {


    private final DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
    private final DatiAnagraficheTest datiAnagraficheTest = new DatiAnagraficheTest();
    private final DatiMediciTest datiMediciTest = new DatiMediciTest();

    @Autowired
    private DatiVisiteTest datiVisiteTest;


    /** Per svolgere le principali operazioni della struttura dati Pianificatore */
    @Autowired
    private Pianificatore pianificatore;

    // LISTE CONTENENTI DATI DI TEST
    List<Anagrafica> listaAnagrafiche;
    List<Medico> listaMedici;
    List<Prestazione> listaPrestazioni;
    List<Integer> listaIndiciPrestazioniIniziali; // lista ausiliaria
    List<Prestazione> listaPrestazioniIniziali;

    /** Data di test attuale */
    public static final  Date dataAttualeDiTest = DatiTest.dataAttualeDiTest;


    @BeforeEach
    void setup() {

        /** Scelgo un caso rappresentativo:
         *  Come data attuale scelgo: venerdì 17 gennaio: le visite devono essere schedulate
         *  entro gli orari di apertura di mattina e pomeriggio, e se una visita poi sfora la
         *  giornata di venerdì, deve essere assegnata a lunedì 20.
         * */
        listaAnagrafiche = datiAnagraficheTest.getDatiTest();
        listaMedici = datiMediciTest.getDatiTest(); // Medici (indici): 0  1  2
        listaPrestazioni = datiPrestazioniTest.getDatiTest();
        // listaPrestazioni.durate = { 15min, 2h, 3h, 4h, 4.5h, 5h, 20min }
        //  (indici)                     0     1   2   3    4    5   6


        // Lista di indici per assegnare le prestazioni che desidero alle visite di inizio giornata
        listaIndiciPrestazioniIniziali = Arrays.asList(0, 2, 1);
        listaPrestazioniIniziali = new ArrayList<>(); // Prime prestazioni da assegnare alle visite di inizio giornata

        listaIndiciPrestazioniIniziali.forEach(i -> listaPrestazioniIniziali.add( listaPrestazioni.get(i) ) );


        // Se sono alla prima visita del giorno, finchè non si sono occupati tutti i medici in una visita, allora assegno sempre
        // come ora le 07:00, maggiorata dei 5 minuti (quindi 07:05).
        for(int i=0;i < listaMedici.size();i++) {

            Visita visita = new Visita();

            visita.setIdVisita( (long) (i+1) );
            visita.setAnagrafica( listaAnagrafiche.get( i ) );
            visita.setDataVisita(dataAttualeDiTest);
            visita.setOra( Time.valueOf( LocalTime.of(7,0).plusMinutes( pianificatore.getPausa5Minuti() ) ));
            visita.setPrestazione( listaPrestazioniIniziali.get( i ) ); // ad ogni iterazione prendi una determinata durata che ho scelto io prima
            visita.setNumAmbulatorio( i+1 );

            // Aggiungo la visita sia alla listaVisite sia alla mappa visiteMap
            pianificatore.aggiungiVisita( visita );

        }

    }

    // TODO: Questi test riflettono i calcoli/disegni delle strutture dati fatti su carta!!

    @Test
    @Order(1)
    void testPianificatore_pianificaNuovaVisita_With6Visite_isWorkingOk() {


        System.out.println();
        // TODO: 2)
        // Inizio ad inserire le visite


        Visita v4 = new Visita();
        pianificatore.pianificaNuovaVisita( v4, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        Visita v5 = new Visita();
        pianificatore.pianificaNuovaVisita( v5, dataAttualeDiTest, listaPrestazioni.get(6) ); // 20 min

        Visita v6 = new Visita();
        pianificatore.pianificaNuovaVisita( v6, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        // Stampa a video per chiarezza
        pianificatore.stampaListaAndMappa();

        /** TODO: Assert */
        Assertions.assertEquals( Set.of(3L, 2L, 1L),  pianificatore.getMediciMap().keySet());
    }


    @Test
    @Order(2)
    void testPianificatore_pianificaNuovaVisita_With7Visite_isWorkingOk() {

        Visita v4 = new Visita();
        pianificatore.pianificaNuovaVisita( v4, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        Visita v5 = new Visita();
        pianificatore.pianificaNuovaVisita( v5, dataAttualeDiTest, listaPrestazioni.get(6) ); // 20 min

        Visita v6 = new Visita();
        pianificatore.pianificaNuovaVisita( v6, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        Visita v7 = new Visita();
        pianificatore.pianificaNuovaVisita(v7, dataAttualeDiTest, listaPrestazioni.get(0) );

        // Stampa a video per chiarezza


        /// ASSERT
        // Mi aspetto che davanti ci sia il medico che si libera per primo: con queste visite è il medico 3
        Assertions.assertEquals( Set.of(3L, 2L, 1L),  pianificatore.getMediciMap().keySet());
        Assertions.assertEquals(LocalTime.of(9,50), // mi aspetto che il medico di id 3 si liberi alle 09:50
                pianificatore.getMediciMap().get(3L).getOraFine().toLocalTime()); // 3L è il medico di id 3

    }

    @DisplayName("Pianificando una qualsiasi visita dopo queste 8, deve essere pianificata al Pomeriggio")
    @Test
    @Order(3)
    void testPianificatore_pianificaNuovaVisita_With8Visite_isWorkingOk() {

        Visita v4 = new Visita();
        pianificatore.pianificaNuovaVisita( v4, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        Visita v5 = new Visita();
        pianificatore.pianificaNuovaVisita( v5, dataAttualeDiTest, listaPrestazioni.get(6) ); // 20 min

        Visita v6 = new Visita();
        pianificatore.pianificaNuovaVisita( v6, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        Visita v7 = new Visita();
        pianificatore.pianificaNuovaVisita(v7, dataAttualeDiTest, listaPrestazioni.get(0) ); // 15 min

        Visita v8 = new Visita();
        pianificatore.pianificaNuovaVisita(v8, dataAttualeDiTest, listaPrestazioni.get(1) ); // 2 ore

        // Stampa a video per chiarezza


        /// ASSERT
        // Mi aspetto che davanti ci sia il medico che si libera per primo: con queste visite è il medico 3
        Assertions.assertEquals( Set.of(2L, 1L, 3L),  pianificatore.getMediciMap().keySet());
        Assertions.assertEquals(LocalTime.of(11,55), // mi aspetto che il medico di id 3 si liberi alle 11:55
                pianificatore.getMediciMap().get(3L).getOraFine().toLocalTime()); // 3L è il medico di id 3

    }


    @AfterEach
    void teardown() {
        pianificatore.clear();
    }
}
