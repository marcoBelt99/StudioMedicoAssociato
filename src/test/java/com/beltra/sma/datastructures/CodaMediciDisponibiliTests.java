package com.beltra.sma.datastructures;


import com.beltra.sma.data.DatiMediciTest;
import com.beltra.sma.data.DatiPrestazioniTest;
import com.beltra.sma.data.DatiVisiteTest;
import com.beltra.sma.groovy.datastructures.CodaMediciDisponibiliGroovyImpl;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;

import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.FineVisita;
import com.beltra.sma.utils.Parameters;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;



import java.sql.Time;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest
public class CodaMediciDisponibiliTests {

    List<Visita> listaVisite = new ArrayList<>();
    List<Medico> listaMedici = new ArrayList<>();
    List<Prestazione> listaPrestazioni = new ArrayList<>();
    DatiVisiteTest datiVisiteTest = new DatiVisiteTest();

    //@Autowired
    private VisitaService visitaService;

    Medico m1;
    Medico m2;
    Medico m3;



    @BeforeAll
    void inizializzaDati() {

        // Per testare questa struttura dati mi servo di dati ad hoc.

        listaVisite = getAllVisiteByData();
        listaMedici = getAllDatiMediciTests();
        listaPrestazioni = getAllPrestazioniTests();

        m1 = listaMedici.get(0);
        m2 = listaMedici.get(1);
        m3 = listaMedici.get(2);

        visitaService = mock(VisitaService.class); // per rispettare la "unitarietà" dei test.
        // se dovessi iniettare il VisitaService allora a livello di memoria e tempo di esecuzione avrei maggior overhead.

    }


    ///  ##################################################
    ///  TEST: Funzionamento generico della Struttura Dati
    ///  #################################################


    /** Metodo di providing dei dati, necessario per i test con @ParameterizedTest e @MethodSource */
    private Stream<Arguments> provideDatiTestMediciMapAndMediciQueue() {


        /// Voglio simulare di inserire visite dentro listaVisite, in modo via via crescente
        return Stream.of(

            /// [v1]: simulo l'inserimento di v1
            Arguments.of(
                    listaVisite.stream().limit(1).toList(),
                    listaPrestazioni.get(0).getDurataMedia(), // v1 durerà 15 min

                    m1,
                    new FineVisita(1L, Time.valueOf( LocalTime.of(7,20) ) )
            ),

            /// [v1, v2]: simulo l'inserimento di v2
            Arguments.of(
                    listaVisite.stream().limit(2).toList(),
                    listaPrestazioni.get(2).getDurataMedia(), // v2 durerà 180 min

                    m2,
                    new FineVisita(2L, Time.valueOf( LocalTime.of(10,5) ) )
            ),

            /// [v1, v2, v3]: simulo l'inserimento di v3
            Arguments.of(
                    listaVisite.stream().limit(3).toList(),
                    listaPrestazioni.get(1).getDurataMedia(), // v3 durerà 120 min
                    m3,
                    new FineVisita(listaVisite.stream().limit(3).count(), Time.valueOf( LocalTime.of(9,5) ) )
            ),


            /// [v1, v2, v3, v4]: simulo l'inserimento di v4
            Arguments.of(
                    listaVisite.stream().limit(4).toList(),
                    listaPrestazioni.get(1).getDurataMedia(), // v4 durerà 120 min
                    m1,
                    new FineVisita(listaVisite.stream().limit(4).count(), Time.valueOf( LocalTime.of(9,25) ) )
            ),

            /// [v1, v2, v3, v4, v5]: simulo l'inserimento di v5
            Arguments.of(
                listaVisite.stream().limit(5).toList(),
                listaPrestazioni.get(6).getDurataMedia(), // v5 durerà 20 min
                m3,
                new FineVisita(listaVisite.stream().limit(5).count(), Time.valueOf( LocalTime.of(9,30) ) )
            ),

            /// [v1, v2, v3, v4, v5, v6]: simulo l'inserimento di v6
            Arguments.of(
                    listaVisite.stream().limit(6).toList(),
                    listaPrestazioni.get(1).getDurataMedia(), // v6 durerà 120 min
                    m1,
                    new FineVisita(listaVisite.stream().limit(6).count(), Time.valueOf( LocalTime.of(11,30) ) )
            ),

            /// [v1, v2, v3, v4, v5, v6, v7]: simulo l'inserimento di v7
            Arguments.of(
                    listaVisite.stream().limit(7).toList(),
                    listaPrestazioni.get(0).getDurataMedia(), // v7 durerà 15 min
                    m3,
                    new FineVisita(listaVisite.stream().limit(7).count(), Time.valueOf( LocalTime.of(9,50) ) )
            ),

            /// [v1, v2, v3, v4, v5, v6, v7, v8]: simulo l'inserimento di v8
            Arguments.of(
                    listaVisite.stream().limit(8).toList(),
                    listaPrestazioni.get(1).getDurataMedia(), // v8 durerà 120 min
                    m3,
                    new FineVisita(listaVisite.stream().limit(8).count(), Time.valueOf( LocalTime.of(11,55) ) )
            ),

            /// TODO: Devo sviluppare il check ammissibilità
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9]: simulo l'inserimento di v9
            Arguments.of(
                    listaVisite.stream().limit(9).toList(),
                    listaPrestazioni.get(1).getDurataMedia(), // v9 durerà 120 min
                    m2,
                    new FineVisita(listaVisite.stream().limit(9).count(), Time.valueOf( LocalTime.of(16,5) ) )
            ),

            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10]: simulo l'inserimento di v10
            Arguments.of(
                    listaVisite.stream().limit(10).toList(),
                    listaPrestazioni.get(2).getDurataMedia(), // v10 durerà 180 min
                    m1,
                    new FineVisita(listaVisite.stream().limit(10).count(), Time.valueOf( LocalTime.of(17,5) ) )
            ),
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11]: simulo l'inserimento di v11
            Arguments.of(
                    listaVisite.stream().limit(11).toList(),
                    listaPrestazioni.get(0).getDurataMedia(), // v11 durerà 15 min
                    m3,
                    new FineVisita(listaVisite.stream().limit(11).count(), Time.valueOf( LocalTime.of(14,20) ) )
            ),
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11,  v12]: simulo l'inserimento di v12
            Arguments.of(
                    listaVisite.stream().limit(12).toList(),
                    listaPrestazioni.get(0).getDurataMedia(), // v12 durerà 15 min
                    m3,
                    new FineVisita(listaVisite.stream().limit(12).count(), Time.valueOf( LocalTime.of(18,55) ) )
            ),

            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11,  v12, v13]: simulo l'inserimento di v13
            Arguments.of(
                    listaVisite.stream().limit(13).toList(),
                    listaPrestazioni.get(1).getDurataMedia(), // v13 durerà 120 min
                    m2,
                    new FineVisita(listaVisite.stream().limit(13).count(), Time.valueOf( LocalTime.of(18,10) ) )
            ),
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11,  v12, v13, V14]: simulo l'inserimento di v14
            Arguments.of(
                    listaVisite.stream().limit(14).toList(),
                    listaPrestazioni.get(2).getDurataMedia(), // v14 durerà 180 min
                    m1,
                    new FineVisita(listaVisite.stream().limit(14).count(), Time.valueOf( LocalTime.of(20,10) ) )
            ),
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11,  v12, v13, v14, v15]: simulo l'inserimento di v15
            Arguments.of(
                    listaVisite.stream().limit(15).toList(),
                    listaPrestazioni.get(1).getDurataMedia(), // v15 durerà 120 min
                    m2,
                    new FineVisita(listaVisite.stream().limit(15).count(), Time.valueOf( LocalTime.of(20,15) ) )
            ),
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11,  v12, v13, v14, v15, V16]: simulo l'inserimento di v16
            Arguments.of(
                    listaVisite.stream().limit(16).toList(),
                    listaPrestazioni.get(1).getDurataMedia(), // v16 durerà 120 min
                    m3,
                    new FineVisita(listaVisite.stream().limit(16).count(), Time.valueOf( LocalTime.of(21, 0) ) )
            ),
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11,  v12, v13, v14, v15, V16, v17]: simulo l'inserimento di v17
            Arguments.of(
                    listaVisite.stream().limit(17).toList(),
                    listaPrestazioni.get(6).getDurataMedia(), // v17 durerà 20 min
                    m1,
                    new FineVisita(listaVisite.stream().limit(17).count(), Time.valueOf( LocalTime.of(20, 35) ) )
            ),
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11,  v12, v13, v14, v15, V16, v17, v18]: simulo l'inserimento di v18
            Arguments.of(
                listaVisite.stream().limit(18).toList(),
                listaPrestazioni.get(0).getDurataMedia(), // v18 durerà 15 min
                m2,
                new FineVisita(listaVisite.stream().limit(18).count(), Time.valueOf( LocalTime.of(20, 35) ) )
            ),
            /// [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10,
            /// v11,  v12, v13, v14, v15, V16, v17, v18, v19]: simulo l'inserimento di v19
            Arguments.of(
                    listaVisite.stream().limit(19).toList(),
                    listaPrestazioni.get(0).getDurataMedia(), // v19 durerà 15 min
                    m1, /// Nota bene: V17 ha la stessa ora fine di V18, quindi per scegliere quale medico assegnare viene scelto il medico avente id minore fra tutti
                    new FineVisita(listaVisite.stream().limit(19).count(), Time.valueOf( LocalTime.of(20, 55) ) )
            )

        );
    }


    @ParameterizedTest
    @MethodSource("provideDatiTestMediciMapAndMediciQueue")
    void mediciMapAndMediciQueue_Works(List<Visita> visiteEsistentiTest, Double durataMediaTest,
                  Medico medicoExpected, FineVisita fineVisitaExpected) {

        /// ACT
        CodaMediciDisponibili codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMedici, visiteEsistentiTest, LocalTime.now(), durataMediaTest, visitaService);

        FineVisita fineVisitaActual = codaMediciDisponibili.getMediciMap().get(medicoExpected);

        // Ricerco il medico in base al valore della entry
        Medico medicoActual = codaMediciDisponibili.getMediciQueue().stream()
                .filter(e -> e.getValue() == fineVisitaActual && Objects.equals(e.getKey().getMatricola(),
                medicoExpected.getMatricola()))
                .findFirst()
                .orElseThrow()
                .getKey();

        /// ASSERT
        assertEquals( Math.min(listaMedici.size(), visiteEsistentiTest.size()), codaMediciDisponibili.getMediciMap().size());
        assertEquals(medicoExpected.getMatricola(), medicoActual.getMatricola());
        assertTrue( codaMediciDisponibili.getMediciMap().containsKey(medicoExpected) );
        assertEquals( fineVisitaExpected.getOraFine(),  fineVisitaActual.getOraFine()  );
        assertEquals( fineVisitaExpected.getIdVisita(), fineVisitaActual.getIdVisita() );
    }




    ///  ########################################
    ///  TEST: getPrimoMedicoDisponibile()
    ///  ########################################

    /** Copre il caso in cui lista visite e' vuota ma lista medici non lo e', quindi ritorna sempre il primo medico! */
    @Test
    void getPrimoMedicoDisponibile_WithEmptyListaVisite_AndNotEmptyListaMedici_AlwaysIsM1() {
        /// []: Simulo la lista di visite vuote, ma la lista di medici non vuota

        /// Arrange & Act
        Double durataTest = listaPrestazioni.get(0).getDurataMedia();
        CodaMediciDisponibili codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMedici, new ArrayList<>(), LocalTime.now(), durataTest, visitaService);

        /// Assert
       assertEquals(1L,  codaMediciDisponibili.getPrimoMedicoDisponibile(durataTest).getKey().getIdAnagrafica() );
    }


    /** Casistica mentre sto aumentando di visite: arrivato alla 18°-esima voglio esattamente il comportamento aspettato. */
    @Test
    void getPrimoMedicoDisponibile_WithNotEmptyListaVisite_AndNotEmptyListaMedici_WorksCorrectly() {
        /// ACT
        CodaMediciDisponibili coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisite.stream().limit(18).toList(), LocalTime.now(),
                0.0, visitaService);
        /// ASSERT
        assertTrue(coda.getPrimoMedicoDisponibile(0.0).getKey().getIdAnagrafica().equals(1L));
    }


    @Test
    void getListaVisiteGiornaliereNextGiornoAmmissibile_ShouldReturnNotNullList() {

        CodaMediciDisponibiliGroovyImpl coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisite, LocalTime.now(),
                0.0, visitaService);
        assertNotNull( coda.getListaVisiteNextGiornoAmmissibile(), "La lista non deve essere null" );
    }

/// #####################################################################
/// #####################################################################
/// #####################################################################

    /**
     * TEST DI SFORAMENTO VISITE GIORNALIERE: CHE SUCCEDE?
     * Per la coda deve essere trasparente che il giorno sia ammissibile o meno, ma deve solo "resettare"
     * e ripartire dal medico 1, 2, 3, ... e dalla mattina.
     * Perchè l'ho disabilitato? (18/06/2025)
     * ==> Perche' il codice soggetto a test fa una chiamata a DB per farsi restituire la lista di visite presenti nel successivo
     *  giorno ammissibile => questo è un comportamento non deterministico (non so quali visite effettivamente io abbia: ci possono essere
     *  cancellazioni, inserimenti vari, etc).
     *  Allora come faccio? Questo si risolve grazie al Mocking e Stubbing, ben gestito dalla classe
     * @see CodaMediciDisponibiliMockingTests
     */
    @Disabled
    @Test
    void testGetPrimoMedicoDisponibile_WithListaVisiteFull_AndListaMediciNotEmpty_AndSforamento_WorksCorrectly_() {

        Double durataTest = 180.0;

        CodaMediciDisponibili coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisite, LocalTime.now(), durataTest, visitaService);

        Map.Entry<Medico, FineVisita> entryMedicoDisponibile = coda.getPrimoMedicoDisponibile(durataTest);

        assertEquals(1L, (long) entryMedicoDisponibile.getKey().getIdAnagrafica());

        assertEquals(entryMedicoDisponibile.getValue().getOraFine(), Time.valueOf(LocalTime.of(20, 35)));

    }


    /** TODO: potrei anche toglierlo: questo comportamento qui è soggetto a variazioni, ad errori non predicibili.
     * Per testare al meglio questo comportamento, devo sempre fare uso del mocking.
     * @see CodaMediciDisponibiliMockingTests */
    @Disabled
    @Test
    void testGetPrimoMedicoDisponibile_WithListaVisiteNotEmpty_AndListaMediciNotEmpty_Sforamento_WorksCorrectly() {

        Double durataTest = 180.0;

        CodaMediciDisponibili coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisite, LocalTime.now(), durataTest, visitaService);

        Map.Entry<Medico, FineVisita> entryMedicoDisponibile = coda.getPrimoMedicoDisponibile(durataTest);

        // Mi aspetto 1L perchè effettivamente, se non ci sono altre visite il successivo (futuro) giorno ammissibile,
        // allora la visita che sta sforando in questo momento sarà l'unica visita presente in tale giorno futuro.
        assertEquals(1L, (long) entryMedicoDisponibile.getKey().getIdAnagrafica());
        assertEquals(Time.valueOf(LocalTime.of(10, 5)), entryMedicoDisponibile.getValue().getOraFine() );
    }


//    @Test
//    void testGetPrimoMedicoDisponibile_WithListaVisiteEmpty() {
//        inizializzaDati();
//        CodaMediciDisponibili coda = new CodaMediciDisponibiliGroovyImpl(listaMedici,
//                new ArrayList<>(), LocalTime.now(), 0.0, visitaService);
//
//        assertEquals(1L, coda.getPrimoMedicoDisponibile(0.0).getKey().getIdAnagrafica() );
//    }




//    /// ###################################################
//    /// TEST CLOSURE: visitaNotAmmissibileInPomeriggio
//    /// ###################################################
//    private Stream<Arguments> provideDatiTestFor_VisitaNotAmmissibileWorkForSingleVisita() {
//
//        Double durataTest = 20.0;
//
//        return Stream.of(
//
//        /// Casi true: (è vero che le visite che hanno questi orari NON sono ammissibili in pomeriggio perchè appunto sono prima di ora pomeriggio)
//                Arguments.of(LocalTime.MIDNIGHT, durataTest, true),
//                Arguments.of(LocalTime.of(3,5), durataTest, true),
//                Arguments.of(Parameters.orarioAperturaMattina, durataTest, true ),
//                Arguments.of(LocalTime.of(10, 0), durataTest, true ),
//                Arguments.of(LocalTime.of(11,39), durataTest, true),
//                Arguments.of(LocalTime.of(11, 40), durataTest, true),
//                Arguments.of(LocalTime.of(12, 41), durataTest, true),
//                Arguments.of(LocalTime.of(13, 39), durataTest, true),
//
//                // Questi non sono ammissibili perche' sforano
//                Arguments.of(LocalTime.of(20, 40), durataTest, true),
//                Arguments.of(LocalTime.of(20, 41), durataTest, true),
//                Arguments.of(LocalTime.of(20, 39), durataTest, true), // true
//                Arguments.of(LocalTime.of(20, 59), durataTest, true),
//                Arguments.of(Parameters.orarioChiusuraPomeriggio, durataTest, true),
//                Arguments.of(LocalTime.of(21, 1), durataTest, true),
//
//
//        /// Casi false: (è vero che le visite che hanno questi orari sono ammissibili in pomeriggio)
//                Arguments.of(LocalTime.of(13, 40), durataTest, false), // da qui in poi, comprese le 14:00, inizia l'orario di lavoro nel pomeriggio
//                Arguments.of(LocalTime.of(13, 41), durataTest, false),
//                Arguments.of(LocalTime.of(19, 0), durataTest, false),
//                Arguments.of(LocalTime.of(20, 1), durataTest, false),
//                Arguments.of(LocalTime.of(20, 15), durataTest, false),
//                Arguments.of(LocalTime.of(20, 34), durataTest, false) // caso limite?
//
//        );
//    }


//    @ParameterizedTest
//    @MethodSource("provideDatiTestFor_VisitaNotAmmissibileWorkForSingleVisita")
//    void testVisitaIsNotAmmissibileInPomeriggioWorkForSingleVisita(LocalTime oraAttuale, Double durataTest, Boolean expected) {
//
//        CodaMediciDisponibiliGroovyImpl coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisite, oraAttuale, durataTest, visitaService);
//        Visita visita = new Visita();
//        Prestazione p = new Prestazione();
//        Anagrafica a = new Anagrafica();
//
//        visita.setIdVisita(1L);
//        visita.setNumAmbulatorio(1);
//        visita.setOra(Time.valueOf(oraAttuale));
//        a.setIdAnagrafica(1L);
//        visita.setAnagrafica(a);
//        p.setDurataMedia(durataTest);
//        visita.setPrestazione(p);
//
//        Closure<Boolean> closure = coda.visitaNotAmmissibileInPomeriggio();
//
//        assertEquals( expected,  closure.call(visita) );
//    }


    /// ###################################################
    /// TEST CLOSURE: IsVisitaAfterChiusuraPomeriggio
    /// ###################################################
    private Stream<Arguments> provideDatiTestFor_IsOrarioVisitaAfterChiusuraPomeriggio() {

        Double durataTest = 20.0;

        return Stream.of(

            /// Casi false: (non è vero che gli orari di queste visite superano l'ora di chiusura del pomeriggio)

            Arguments.of(LocalTime.MIN, durataTest, false),
            Arguments.of(LocalTime.MIDNIGHT, durataTest, false),
            Arguments.of(LocalTime.of(3,5), durataTest,      false),
            Arguments.of(Parameters.orarioAperturaMattina, durataTest,    false ),
            Arguments.of(LocalTime.of(10, 0), durataTest,    false ),
            Arguments.of(LocalTime.of(11,39), durataTest,    false),
            Arguments.of(LocalTime.of(11, 40), durataTest,   false),
            Arguments.of(LocalTime.of(12, 41), durataTest,   false),
            Arguments.of(LocalTime.of(13, 39), durataTest,   false),
            Arguments.of(LocalTime.of(13, 40), durataTest,   false), // da qui in poi, comprese le 14:00, inizia l'orario di lavoro nel pomeriggio
            Arguments.of(LocalTime.of(13, 41), durataTest,   false),
            Arguments.of(LocalTime.of(14, 41), durataTest,   false),
            Arguments.of(LocalTime.of(20, 34), durataTest,   false),
            Arguments.of(LocalTime.of(20, 35), durataTest,   false), // aggiunge la pausa anche (5 min) + 20 = 21:00, che in questo caso sono ancora comprese

            /// Casi true: (è vero che le visite che hanno questi orari non sono ammissibili in pomeriggio perchè appunto sono prima di ora pomeriggio)
            Arguments.of(LocalTime.of(20, 36), durataTest, true), // già da questo non è più ammissibile perchè si va alle 21:01 (5 min + 20 min)
            Arguments.of(LocalTime.of(20, 37), durataTest, true),
            Arguments.of(LocalTime.of(20, 38), durataTest, true),
            Arguments.of(LocalTime.of(20, 40), durataTest,  true),
            Arguments.of(LocalTime.of(20, 41), durataTest,  true),
            Arguments.of(LocalTime.of(20, 39), durataTest, true),// ultimo minuto in cui si lavora al pomeriggio
            Arguments.of(LocalTime.of(20, 59), durataTest, true),
            Arguments.of(Parameters.orarioChiusuraPomeriggio, durataTest, true),
            Arguments.of(LocalTime.of(21, 1), durataTest, true),
            Arguments.of(LocalTime.MAX, 0.0, true) // durata per forza 0, altrimenti è come se si resettasse l'orario
                // e passa la mezzanotte, quindi diventa mattina,e ricado nei casi false.

        );
    }


//    @ParameterizedTest
//    @MethodSource("provideDatiTestFor_IsOrarioVisitaAfterChiusuraPomeriggio")
//    void testIsOrarioVisitaAfterChiusuraPomeriggio(LocalTime oraAttuale, Double durataTest, Boolean expected) {
//        CodaMediciDisponibiliGroovyImpl coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisite, oraAttuale, durataTest, visitaService);
//        Visita visita = new Visita();
//        Prestazione p = new Prestazione();
//        Anagrafica a = new Anagrafica();
//        visita.setIdVisita(1L);
//        visita.setNumAmbulatorio(1);
//        visita.setOra(Time.valueOf(oraAttuale));
//        a.setIdAnagrafica(1L);
//        visita.setAnagrafica(a);
//        p.setDurataMedia(durataTest);
//        visita.setPrestazione(p);
//
//        Closure<Boolean> closure = coda.isOrarioVisitaAfterChiusuraPomeriggio();
//        assertEquals( expected,  closure.call(visita));
//    }




    /// ################################
    ///  test metodo: testValutaNextGiornoAmmissibile
    /// ################################


//
//    @Test
//    void testValutaNextGiornoAmmissibile_ShouldReturnTrue() {
//        // ARRANGE
//        List<Visita> listaVisiteTest = datiVisiteTest.getListaVisiteFullFromCSV("src/test/resources/visiteGiornaliereFullSforamento.csv");
//        Double durataTest = 180.0;
//        CodaMediciDisponibiliGroovyImpl coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisiteTest, LocalTime.now(),
//                0.0, visitaService);
//
//        // ACT & ASSERT
//        assertTrue( coda.valutaNextGiornoAmmissibile() );
//    }



//    @Test
//    void testValutaNextGiornoAmmissibile_ShouldReturnFalse() {
//
//        // ARRANGE
//        List<Visita> listaVisiteTest = datiVisiteTest.getListaVisiteFullFromCSV("src/test/resources/visiteMattinaFull_Caso_E.csv");
//        Double durataTest = 180.0;
//        CodaMediciDisponibiliGroovyImpl coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisiteTest, LocalTime.now(),
//                0.0, visitaService);
//
//        // ACT & ASSERT
//        assertFalse( coda.valutaNextGiornoAmmissibile() );
//
//    }


    // TODO:
    /// ################################
    ///  test metodo: getListaVisiteGiornaliereNonAmmissibili
    /// ################################
//    @Test
//    void testGetListaVisiteGiornaliereNonAmmissibili_ShouldReturnNotEmptyList() {
//        List<Visita> listaVisiteTest = datiVisiteTest.getListaVisiteFullFromCSV("src/test/resources/visiteGiornaliereNotAmmissibili.csv");
//        Double durataTest = 180.0;
//        CodaMediciDisponibiliGroovyImpl coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisiteTest, LocalTime.now(),
//                0.0, visitaService);
//
//        // ACT
//        List<Visita> listaVisiteNonAmmissibili = coda.getListaVisiteGiornaliereNonAmmissibili(durataTest);
//
//
//        // ACT
//        assertNotNull( listaVisiteNonAmmissibili );
//        assertEquals( 4, listaVisiteNonAmmissibili.size() );
//    }






    // #################################################################
    // OTTENIMENTO DATI
    // #################################################################

    protected List<Visita> getAllVisiteByData() {
        return datiVisiteTest.getListaVisiteFullFromCSV();
    }

    protected List<Medico> getAllDatiMediciTests() {
        DatiMediciTest datiMediciTest = new DatiMediciTest();
        return datiMediciTest.getDatiTest();
    }

    protected List<Prestazione> getAllPrestazioniTests() {
        DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
        return datiPrestazioniTest.getDatiTest();
    }

}