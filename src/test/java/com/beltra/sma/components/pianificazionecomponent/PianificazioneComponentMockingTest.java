package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.components.*;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.Parameters;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PianificazioneComponentMockingTest extends PianificazioneComponentTest{

    // Qui posso mockare il fatto che per un certo giorno nel futuro
    // io ho già in programma delle visite,
    // ==> Così posso vedere se le visite vengono pianificate accodate opportunamente
    // TODO: ...

    // Similmente per quanto fatto per la classe CodaMediciDisponibiliMockingTests,
    // anche qui devo simulare il fatto che nel futuro, dal visitaService io ottenga delle visite "fittizie".

    /** ########################################################## */
    /** ########################################################## */
    /** ########################## SFORAMENTO #################### */
    /** ########################################################## */
    /** ########################################################## */


    /** FUNZIONANTEEEEE */
    @Test
    void testTrovaSlotDisponibileConChiamataRicorsiva() {

        /// ARRANGE
        Date dataExpected = dataLunedi20Gennaio2025Test; // TODO: come data di test mi aspetto la prossima disponibile!!
        Time oraExpected = Time.valueOf( Parameters.orarioAperturaMattina           // TODO: mattina, perchè so che per i dati di test che ho (che sto usando ora)
                .plusMinutes(Parameters.pausaFromvisite) );  // sarà la prima visita al mattino
        Medico medicoExpected = getAllDatiMediciTests().get(2); // TODO: mi aspetto m3 come medico


        /// MOCKING / STUBBING
        VisitaService visitaServiceMock = mock(VisitaService.class);
        CalcolatoreAmmissibilitaComponentImpl calcolatore = new CalcolatoreAmmissibilitaComponentImpl();

        // Creo uno spy per poter mockare un metodo interno
        PianificazioneComponentImpl pianificazioneSpy = spy(new PianificazioneComponentImpl(visitaServiceMock, calcolatore));

        AtomicBoolean primaChiamataTrovaSlot = new AtomicBoolean(true);

        /**  */
        doAnswer(invocation -> {
            Date data = invocation.getArgument(0);

            if (primaChiamataTrovaSlot.get()) {
                // Siamo nella prima chiamata a trovaSlot -> ritorna dati veri
                return datiVisiteTest.getListaVisiteFullFromCSV();
            } else {
                // Dopo la prima chiamata -> ritorna visite finte
                return getListaVisiteFinteNextGiornoAmmissibile_1();
            }
        }).when(pianificazioneSpy).getAllVisiteByData(any(Date.class));


// Spy del metodo ricorsivo per modificare il flag alla prima chiamata
        doAnswer(invocation -> {
            // Prima chiamata => disattiva il flag per le successive
            primaChiamataTrovaSlot.set(false);
            // Chiama il metodo reale
            return invocation.callRealMethod();
        }).when(pianificazioneSpy).trovaSlotDisponibile(
                anyDouble(), any(Date.class), any(LocalTime.class),
                anyList(), anyList()
        );


        /// ACT
        //Optional<SlotDisponibile> slot = spyComponent.trovaSlotDisponibile(durata, dataVenerdi17Gennaio2025Test, oraAttualeTest, listaMedici, listaVisiteIniziale);
        Optional<SlotDisponibile> risultato = pianificazioneSpy.trovaSlotDisponibile( // spiato
                getAllPrestazioniTests().get(2).getDurataMedia(),
                dataVenerdi17Gennaio2025Test,
                LocalTime.of( 15, 55  ), // ora ammissibile
                getAllDatiMediciTests(), // I soliti 3 medici (che ho su carta)
                datiVisiteTest.getListaVisiteFullFromCSV()
        );


        /// ASSERT

        /// Verifica date delle chiamate
        verify(pianificazioneSpy, times(1)).getAllVisiteByData(argThat(date ->
                date.after(dataVenerdi17Gennaio2025Test) &&
                        date.equals(dataLunedi20Gennaio2025Test))); // Verifico che almeno una chiamata sia con data successiva


        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(pianificazioneSpy, atLeast(2)).getAllVisiteByData(dateCaptor.capture());
        List<Date> chiamate = dateCaptor.getAllValues();

        assertTrue(chiamate.get(0).equals(dataVenerdi17Gennaio2025Test) ||
                            chiamate.get(0).before(dataLunedi20Gennaio2025Test));


        assertEquals(dataLunedi20Gennaio2025Test, chiamate.get(2));


        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( dataExpected, risultato.get().getData() );
        assertEquals( oraExpected, risultato.get().getOrario() );
        assertEquals( medicoExpected.getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
        assertEquals( medicoExpected.getMatricola(), risultato.get().getMedico().getMatricola() );
    }


//    @Test
//    /// 2.F lista visite full!!.
//    ///     File usato: visiteGiornaliereFull.csv
//    @DisplayName("1 visita diventa non ammissibile, e sarà pianificata nel successivo giorno ammissibile")
//    public void testTrovaSlotDisponibile_WithOraAttualeAmmissibileWithMattinaFull_AndPomeriggioFull_AfterV19() {
//
//        // ARRANGE
//
//        List<Visita> listaVisiteFittizie = getListaVisiteFinteNextGiornoAmmissibile_1(); // lista da usare per falsificare (da usare nello spy)
//
//        // mock della dipendenza interna a PianificazioneComponent legata al VisitaService.
//        VisitaService visitaService = mock(VisitaService.class);
//
//        // spy della classe di test, perchè devo "falsificare" il suo metodo getListaVisiteNextGiornoAmmissibile
//        PianificazioneComponent pianificazioneComponentSpy = spy(super.pianificazioneComponent);
//
//        doReturn( listaVisiteFittizie ).when( pianificazioneComponentSpy ).getAllVisiteByData(any());
//
//        // ACT
//        Optional<SlotDisponibile> risultato = pianificazioneComponentSpy.trovaSlotDisponibile(
//                getAllPrestazioniTests().get(2).getDurataMedia(),
//                dataVenerdi17Gennaio2025Test,
//                LocalTime.of( 15, 55  ), // ora ammissibile
//                getAllDatiMediciTests(),
//                datiVisiteTest.getListaVisiteFullFromCSV()
//                );
//
//
//        Date dataExpected = dataLunedi20Gennaio2025Test; // TODO: come data di test mi aspetto la prossima disponibile!!
//        LocalTime oraExpected =
//                Parameters
//                        .orarioAperturaMattina
//                        .plusMinutes(Parameters.pausaFromvisite);  // TODO: mattina, perchè so che per i dati di test che ho (che sto usando ora) sarà la prima visita al mattino
//        Medico medicoExpected = getAllDatiMediciTests().get(0); // TODO: mi aspetto m1 come medico
//
//
//        // ASSERT
//        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
//        assertEquals( dataExpected, risultato.get().getData() );
//        assertEquals( Time.valueOf(oraExpected), risultato.get().getOrario() );
//        assertEquals( medicoExpected.getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
//        assertEquals( medicoExpected.getMatricola(), risultato.get().getMedico().getMatricola() );
//
//    }
//
//
//
//
//
//    @Test
//    void testTrovaSlotDisponibile_ConChiamateRicorsiveEListeVisiteHardcoded() {
//
//        /// ARRANGE
//        Date dataExpected = dataLunedi20Gennaio2025Test; // TODO: come data di test mi aspetto la prossima disponibile!!
//        Time oraExpected = Time.valueOf( Parameters.orarioAperturaMattina           // TODO: mattina, perchè so che per i dati di test che ho (che sto usando ora)
//                                        .plusMinutes(Parameters.pausaFromvisite) );  // sarà la prima visita al mattino
//        Medico medicoExpected = getAllDatiMediciTests().get(2); // TODO: mi aspetto m2 come medico
//
//        Double durata = 180.0;
//
//
//        // Lista visite hardcoded per la seconda chiamata e successive
//        List<Visita> visiteHardcodedSecondaChiamata = getListaVisiteFinteNextGiornoAmmissibile_1();
//
//        // mock
//        VisitaService visitaServiceMock = mock(VisitaService.class);
////        CalcolatoreAmmissibilitaComponentImpl calcolatoreMock = mock(CalcolatoreAmmissibilitaComponentImpl.class);
//
//        CalcolatoreAmmissibilitaComponentImpl calcolatore = new CalcolatoreAmmissibilitaComponentImpl();
//
//
//
//        // Componente originale, con visitaServiceMock e calcolatore
//        pianificazioneComponent = new PianificazioneComponentImpl(visitaServiceMock, calcolatore);
//
//        // SPY del componente sottoposto a test
//        PianificazioneComponent spyComp = spy(pianificazioneComponent);
//
//        AtomicInteger callCount = new AtomicInteger(0);
//
//        doAnswer(invocation -> {
//            int count = callCount.getAndIncrement();
//            if (count == 0) {
//                return datiVisiteTest.getListaVisiteFullFromCSV(); // Prima chiamata
//            } else if (count == 1) {
//                return visiteHardcodedSecondaChiamata;             // Seconda chiamata
//            } else {
//                fail("Metodo getAllVisiteByData chiamato più di due volte! Chiamato " + count +" volte.");
//                return null;
//            }
//        }).when(spyComp).getAllVisiteByData(any(Date.class));
//
//
//
//        /// ACT
////        Optional<SlotDisponibile> risultato = pianificazioneComponent.trovaSlotDisponibile( // originale
//        Optional<SlotDisponibile> risultato = spyComp.trovaSlotDisponibile( // spiato
//                getAllPrestazioniTests().get(2).getDurataMedia(),
//                dataVenerdi17Gennaio2025Test,
//                LocalTime.of( 15, 55  ), // ora ammissibile
//                getAllDatiMediciTests(), // I soliti 3 medici (che ho su carta)
//                datiVisiteTest.getListaVisiteFullFromCSV()
//        );
//
//
//        /// ASSERT
//
//
//
//        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
//        assertEquals( dataExpected, risultato.get().getData() );
//        assertEquals( oraExpected, risultato.get().getOrario() );
//        assertEquals( medicoExpected.getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
//        assertEquals( medicoExpected.getMatricola(), risultato.get().getMedico().getMatricola() );
//    }
//
//
//
//
//
//
//
//
//
//    @DisplayName("2 visite diventano non ammissibili, e saranno pianificate nel successivo giorno ammissibile.")
//    @Test
//    /// 2.G lista visite full sforamento !!.
//    ///     File usato: visiteGiornaliereFullSforamento.csv
//    public void testTrovaSlotDisponibile_WithOraAttualeAmmissibileWithMattinaFull_AndPomeriggioFull_AfterV20() {
//
//        // ARRANGE & ACT
//        Date dataExpected = dataLunedi20Gennaio2025Test; // TODO: come data di test mi aspetto la prossima disponibile!!
//        LocalTime oraExpected =
//                Parameters
//                        .orarioAperturaMattina
//                        .plusMinutes(Parameters.pausaFromvisite);  // TODO: mattina, perchè so che per i dati di test che ho (che sto usando ora) sarà la prima visita al mattino
//        Medico medicoExpected = getAllDatiMediciTests().get(1); // TODO: mi aspetto m2 come medico!
//
//
//        Optional<SlotDisponibile> risultato = arrangeAndAct(
//                getAllPrestazioniTests().get(2).getDurataMedia(),  // durata = 3 ore,
//                dataVenerdi17Gennaio2025Test,
//                LocalTime.of( 15, 55  ), // ora ammissibile
//                getAllDatiMediciTests(),
//                datiVisiteTest.getListaVisiteFullFromCSV("src/test/resources/visiteGiornaliereFullSforamento.csv"),
//
//                dataExpected,
//                oraExpected,
//                medicoExpected
//        );
//
//
//        // ASSERT
//        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
//        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
//        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
//        assertEquals( slotDisponibileExpected.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
//        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
//
//    }
//
//





    /// #####################################
    /// ########### DATI FITTIZI ############
    /// #####################################
    /** Lista di visite fasulle, necessarie per lo spy del metodo getListaVisiteByData(). */

    private List<Visita> getListaVisiteFinteNextGiornoAmmissibile_1() {
        List<Visita> listaVisiteToReturn = new ArrayList<>();

        Prestazione p1 =  getAllPrestazioniTests().get(3);
        Prestazione p2 = getAllPrestazioniTests().get(6);

        Visita v1 = new Visita();
        Visita v2 = new Visita();

        v1.setIdVisita(11L);
        v1.setPrestazione(p1);
        v1.setOra(Time.valueOf( Parameters.orarioAperturaMattina.plusMinutes(Parameters.pausaFromvisite) ));

        v2.setIdVisita(22L);
        v2.setPrestazione(p2);
        v2.setOra(Time.valueOf( Parameters.orarioAperturaMattina.plusMinutes(Parameters.pausaFromvisite) ));

        listaVisiteToReturn.add(v1);
        listaVisiteToReturn.add(v2);

        return listaVisiteToReturn;
    }
}



