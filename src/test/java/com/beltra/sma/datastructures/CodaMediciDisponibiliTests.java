package com.beltra.sma.datastructures;


import com.beltra.sma.data.DatiMediciTest;
import com.beltra.sma.data.DatiPrestazioniTest;
import com.beltra.sma.data.DatiVisiteTest;
import com.beltra.sma.groovy.datastructures.CodaMediciDisponibiliGroovyImpl;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;

import com.beltra.sma.utils.FineVisita;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;



import java.sql.Time;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class CodaMediciDisponibiliTests {

    List<Visita> listaVisite = new ArrayList<>();
    List<Medico> listaMedici = new ArrayList<>();
    List<Prestazione> listaPrestazioni = new ArrayList<>();
    DatiVisiteTest datiVisiteTest = new DatiVisiteTest();

    Medico m1;
    Medico m2;
    Medico m3;


    final String pathCSV_visiteGiornaliereFull = "src/test/resources/visiteGiornaliereFull.csv";


    void inizializzaDati() {
        listaVisite = getAllVisiteByData();
        listaMedici = getAllDatiMediciTests();
        listaPrestazioni = getAllPrestazioniTests();

        m1 = listaMedici.get(0);
        m2 = listaMedici.get(1);
        m3 = listaMedici.get(2);
    }



    /** Metodo di providing dei dati, necessario per i test con @ParameterizedTest e @MethodSource */
    private Stream<Arguments> provideDatiTestMediciMapAndMediciQueue() {

        inizializzaDati();

        /// Voglio simulare di inserire visite via via crescenti
        return Stream.of(

//            /// []: Simulo la lista di visite vuote, ma la lista di medici non vuota
//            Arguments.of(
//                    new ArrayList<Visita>(),
//                    listaPrestazioni.get(0).getDurataMedia(),
//
//                    m1,
//                    new FineVisita(1L, Time.valueOf( LocalTime.of(7,20) ) )
//            ),

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
    void testFunzionamentoMediciMapAndMediciQueue(List<Visita> visiteEsistentiTest, Double durataMediaTest,
                  Medico medicoExpected, FineVisita fineVisitaExpected) {

        // ACT
        CodaMediciDisponibili codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMedici, visiteEsistentiTest, LocalTime.now(), durataMediaTest);

        FineVisita fineVisitaActual = codaMediciDisponibili.getMediciMap().get(medicoExpected);

        // Ricerco il medico in base al valore della entry
        Medico medicoActual = codaMediciDisponibili.getMediciQueue().stream()
                .filter(e -> e.getValue() == fineVisitaActual && Objects.equals(e.getKey().getMatricola(),
                medicoExpected.getMatricola()))
                .findFirst()
                .orElseThrow()
                .getKey();

        // ASSERT
        assertEquals(Math.min(listaMedici.size(), visiteEsistentiTest.size()), codaMediciDisponibili.getMediciMap().size());
        assertEquals(medicoExpected.getMatricola(), medicoActual.getMatricola());
        assertTrue( codaMediciDisponibili.getMediciMap().containsKey(medicoExpected) );
        assertEquals( fineVisitaExpected.getOraFine(),  fineVisitaActual.getOraFine()  );
        assertEquals( fineVisitaExpected.getIdVisita(), fineVisitaActual.getIdVisita() );
    }


    /** Copre il caso in cui lista visite e' vuota ma lista medici non lo e',
     *  quindi ritorna sempre il primo medico! */
    @Test
    void testGetPrimoMedicoDisponibile_WithListaVisiteEmptyAndListaMediciNotEmpty_IsM1() {

        // Arrange
        List<Medico> listaMedici = getAllDatiMediciTests();

        // Act
        CodaMediciDisponibili codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMedici, new ArrayList<>(), LocalTime.now(), 20.0);

        // Assert
       assertEquals(1L,  codaMediciDisponibili.getPrimoMedicoDisponibile(20.0).getKey().getIdAnagrafica() );
    }


    /** Casistica mentre sto aumentando di visite: arrivato alla 18°-esima voglio esattamente il comportamento aspettato */
    @Test
    void testGetPrimoMedicoDisponibile_WithListaVisiteNotEmptyAndListaMediciNotEmpty_WorksCorrectly() {
        inizializzaDati();
        CodaMediciDisponibili coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisite.stream().limit(18).toList(), LocalTime.now(), 0.0);

        assertTrue(coda.getPrimoMedicoDisponibile(0.0).getKey().getIdAnagrafica().equals(1L));
    }




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


    // TODO:
    //  TEST DI SFORAMENTO VISITE GIORNALIERE: CHE SUCCEDE?
    //  Per la coda deve essere trasparente che il giorno sia ammissibile o meno, ma deve solo "resettare"
    //  e ripartire dal medico 1, 2, 3, ... e dalla mattina.






}
