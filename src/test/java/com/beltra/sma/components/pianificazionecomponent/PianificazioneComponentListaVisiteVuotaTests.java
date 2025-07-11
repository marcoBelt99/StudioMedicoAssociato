package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.MedicoService;
import com.beltra.sma.utils.Parameters;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Serve per evitare di mettere static il metodo di providing dei dati di test che servirà poi al metodo di test vero e proprio
//@SpringBootTest // Non serve!! => se lo usassi sprecherei memoria per niente, poichè mi carica tutto il contesto Spring. Va usato per Integration Tests!
public class PianificazioneComponentListaVisiteVuotaTests extends PianificazioneComponentTests {



    @BeforeAll
    void initListaVuota() {
        /** La lista di visite di test deve essere rigorosamente vuota */

        listaVisiteTest = new ArrayList<>();


    }

    //listaVisiteTest = new ArrayList<>();


    /// ######################   1) CASO BASE: LISTA VISITE VUOTA   #####################
    /// #########################################################################################
    /// #########################################################################################

    /** Metodo di providing dei dati, necessario per i test con @ParameterizedTest e @MethodSource */
    private Stream<Arguments> provideDatiTestCase_ListaVisiteEmpty() {

        MedicoService medicoServiceMock = mock(MedicoService.class);
        when(medicoServiceMock.getAllMedici()).thenReturn(getAllDatiMediciTests());

        return Stream.of(
                /// 1.A: oraAttuale before oraAperturaMattina
                /// Se (oraAttuale < oraAperturaMattina)
                ///     allora slot.ora = oraAperturaMattina+5min
                Arguments.of(
                        90.0,
                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of( 6, 55 ), // 06:55 < 07:00
                        medicoServiceMock.getAllMedici(), // Medici mockati con la lista di test (medici letti da file)
                        listaVisiteTest, // lista vuota

                        dataVenerdi17Gennaio2025Test,
                        Parameters.orarioAperturaMattina.plusMinutes(Parameters.pausaFromVisite),
                        medicoServiceMock.getAllMedici().get(0)
                ),

                /// 1.B: oraAttuale ammissibile in mattina
                /// Se (oraAttuale >= oraAperturaMattina) && (oraAttuale <= oraChiusuraMattina)
                ///     allora slot.ora = (oraAttuale + durataPrestazione + 5min)
                Arguments.of(
                        90.0,
                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of( 7, 50  ),
                        medicoServiceMock.getAllMedici(), // Medici mockati con la lista di test (medici letti da file)
                        listaVisiteTest,

                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of( 7, 50  ).plusMinutes( (Parameters.pausaFromVisite)),
                        medicoServiceMock.getAllMedici().get(0)
                ),

                /// 1.C: oraAttuale not ammissibile in mattina
                /// Se ( (oraAttuale + durataPrestazione + 5min) >= oraChiusuraMattina )
                ///     allora slot.ora = oraAperturaPomeriggio + 5min
                Arguments.of(
                        120.0, // scelgo una durata
                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of(10, 20), // la durata scelta mi fa sforare oraChiusuraMattina
                        medicoServiceMock.getAllMedici(), // Medici mockati con la lista di test (medici letti da file)
                        listaVisiteTest,

                        dataVenerdi17Gennaio2025Test,
                        Parameters.orarioAperturaPomeriggio.plusMinutes( Parameters.pausaFromVisite), // Mi aspetto 14:05
                        medicoServiceMock.getAllMedici().get(0) // Mi aspetto m1
                ),

                /// 1.D: oraAttuale ammissibile in pomeriggio
                Arguments.of(
                        50.0,
                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of(14, 10),
                        medicoServiceMock.getAllMedici(), // Medici mockati con la lista di test (medici letti da file)
                        listaVisiteTest,

                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of(14, 10).plusMinutes(5),
                        medicoServiceMock.getAllMedici().get(0)
                ),

                /// Testo un bug trovato:
                Arguments.of(
                        45.0,
                        new GregorianCalendar(2025, Calendar.JUNE, 24).getTime(),
                        LocalTime.of(23, 21),
                        medicoServiceMock.getAllMedici(), // Medici mockati con la lista di test (medici letti da file)
                        listaVisiteTest,

                        // Mi aspetto il giorno dopo!
                        new GregorianCalendar(2025, Calendar.JUNE, 25).getTime(),
                        Parameters.orarioAperturaMattina.plusMinutes(5),
                        medicoServiceMock.getAllMedici().get(0)
                )
        );
    }



    @ParameterizedTest
    @MethodSource("provideDatiTestCase_ListaVisiteEmpty")
    public void testTrovaSlotDisponibile_ListaVisiteVuota(Double durataMediaPrestazioneTest, Date dataTest, LocalTime oraAttualeTest,
                                                          List<Medico> listaMediciTest, List<Visita> listaVisiteTest,
                                                          Date dataExpected, LocalTime oraExpected, Medico medicoExpected) {


        // ACT
        Optional<SlotDisponibile> risultato = pianificazioneComponent.trovaSlotDisponibile(
                durataMediaPrestazioneTest,
                dataTest,
                oraAttualeTest,
                listaMediciTest,
                listaVisiteTest
        );


        // ASSERT
        assertThat(risultato).isNotNull();
        assertThat(risultato).isPresent();
        assertThat( dataExpected ).isEqualTo(risultato.get().getData());
        assertThat( oraExpected).isEqualTo(risultato.get().getOrario().toLocalTime()); // converto risultato.orario da Date a LocalTime per avere eguaglianza nei tipi per la Assert
        assertEquals( medicoExpected.getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() ); // non riuscivo ad eguagliare gli oggetti, quindi controllo tramite id
        assertEquals( medicoExpected.getMatricola(), risultato.get().getMedico().getMatricola() ); // e tramite matricola
    }



    /// 1.E: oraAttuale not ammissibile in pomeriggio ==> considera il/i giorno/i successivo/i
    /// Se (oraAttuale+durataMedia+5min) > oraChiusuraPomeriggio
    ///     allora considera il giorno dopo di dataAttuale
    /// TODO:   N.B: io non so nulla sul giorno successivo (o sui giorni successivi)
    ///            perchè potrei avere già visite nel/nei giorno/i successivo/i o magari non averne, ma non posso saperlo
    ///             devo in qualche modo richiamare il metodo trovaPrimoSlotDisponibile() con una dataSuccessiva > dataAttuale  controllando:
    ///             Se listaVisiteGiornoSuccesivo = []
    ///                 allora oraSlot=07:05
    ///             Altrimenti:
    ///                 se c'è spazio nella listaVisiteMattino allora, se non sforo (oraUltimaVisita+durata+5min è ammissibile) accodo in listaVisiteMattino
    ///                 altrimenti considero il pomeriggio
    ///
    @Test
    public void testTrovaSlotDisponibile_ListaVisiteVuota_WithOraAttualeNotAmmissibileInPomeriggio() {
        MedicoService medicoServiceMock = mock(MedicoService.class);
        when(medicoServiceMock.getAllMedici()).thenReturn(getAllDatiMediciTests());

        // ARRANGE & ACT
        Optional<SlotDisponibile> risultato = arrangeAndAct(
                100.0, // durata
                dataVenerdi17Gennaio2025Test,
                LocalTime.of(20, 0), // la durata di test scelta + questo orario di test superano oraChiusuraPomeriggio
                medicoServiceMock.getAllMedici(), // Medici veri!
                listaVisiteTest,

                dataLunedi20Gennaio2025Test, // Quello che so per certo (che voglio) è che passo al/ai giorno/i successivo/i
                LocalTime.of(5,0), // non lo so e non mi interessa in questo test specifico
                new Medico() // non lo so e non mi interessa in questo test specifico
        );

        // ASSERT
        assertThat(risultato).isNotNull();
        assertThat(risultato).isPresent();
        assertThat(dataVenerdi17Gennaio2025Test).isNotSameAs(risultato.get().getData()); // Mi aspetto che la data sia superiore a quella di partenza
        assertThat(dataLunedi20Gennaio2025Test).isInSameDayAs(risultato.get().getData()); // Mi aspetto il risultato sia esattamente questo

        // Non posso dir nulla a priori se nei giorni successivi ci saranno visite, quindi non posso asserire sugli orari

    }


}
