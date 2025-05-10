package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Serve per evitare di mettere static il metodo di providing dei dati di test che servirà poi al metodo di test vero e proprio
@SpringBootTest
public class PianificazioneComponentListaVisiteVuotaTests extends PianificazioneComponentTest {


    // TODO: per rafforzare il test, usa il verify() che, visto che sto gestendo il caso lista vuota,
    // verify() calcolaSlotDisponibileConListaVisiteGiornaliereVuota() è chiamato, in tutti i test

    /// ######################   1) CASO BASE: LISTA VISITE VUOTA   #####################
    /// #########################################################################################
    /// #########################################################################################

    /** Metodo di providing dei dati, necessario per i test con @ParameterizedTest e @MethodSource */
    private Stream<Arguments> provideDatiTestCase_ListaVisiteEmpty() {



        return Stream.of(
                /// 1.A: oraAttuale before oraAperturaMattina
                /// Se (oraAttuale < oraAperturaMattina)
                ///     allora slot.ora = oraAperturaMattina+5min
                Arguments.of(
                        90.0,
                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of( 6, 55 ), // 06:55 < 07:00
                        medicoService.getAllMedici(),
                        new ArrayList<Visita>(), // lista vuota

                        dataVenerdi17Gennaio2025Test,
                        PianificazioneComponent.orarioAperturaMattina.plusMinutes(PianificazioneComponent.pausaFromvisite),
                        medicoService.getAllMedici().get(0)
                ),

                /// 1.B: oraAttuale ammissibile in mattina
                /// Se (oraAttuale >= oraAperturaMattina) && (oraAttuale <= oraChiusuraMattina)
                ///     allora slot.ora = (oraAttuale + durataPrestazione + 5min)
                Arguments.of(
                        90.0,
                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of( 7, 50  ),
                        medicoService.getAllMedici(),
                        new ArrayList<Visita>(),

                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of( 7, 50  ).plusMinutes( (PianificazioneComponent.pausaFromvisite)),
                        medicoService.getAllMedici().get(0)
                ),

                /// 1.C: oraAttuale not ammissibile in mattina
                /// Se ( (oraAttuale + durataPrestazione + 5min) >= oraChiusuraMattina )
                ///     allora slot.ora = oraAperturaPomeriggio + 5min
                Arguments.of(
                        120.0, // scelgo una durata
                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of(10, 20), // la durata scelta mi fa sforare oraChiusuraMattina
                        medicoService.getAllMedici(),
                        new ArrayList<Visita>(),

                        dataVenerdi17Gennaio2025Test,
                        PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes( PianificazioneComponent.pausaFromvisite ), // Mi aspetto 14:05
                        medicoService.getAllMedici().get(0) // Mi aspetto m1
                ),

                /// 1.D: oraAttuale ammissibile in pomeriggio
                Arguments.of(
                        50.0,
                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of(14, 10),
                        medicoService.getAllMedici(),
                        new ArrayList<Visita>(),

                        dataVenerdi17Gennaio2025Test,
                        LocalTime.of(14, 10).plusMinutes(5),
                        medicoService.getAllMedici().get(0)
                )
        );
    }



    @ParameterizedTest
    @MethodSource("provideDatiTestCase_ListaVisiteEmpty") // ARRANGE me la fa questo metodo di providing dei dati
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
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( dataExpected, risultato.get().getData() );
        assertEquals( oraExpected, risultato.get().getOrario().toLocalTime() ); // converto risultato.orario da Date a LocalTime per avere eguaglianza nei tipi per la Assert
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

        // ARRANGE & ACT
        Optional<SlotDisponibile> risultato = arrangeAndAct(
                100.0, // durata
                dataVenerdi17Gennaio2025Test,
                LocalTime.of(20, 0), // la durata di test scelta + questo orario di test superano oraChiusuraPomeriggio
                medicoService.getAllMedici(),
                new ArrayList<Visita>(),

                dataLunedi20Gennaio2025Test, // Quello che so per certo (che voglio) è che passo al/ai giorno/i successivo/i
                LocalTime.of(5,0), // non lo so e non mi interessa in questo test specifico
                new Medico() // non lo so e non mi interessa in questo test specifico
        );

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( dataLunedi20Gennaio2025Test, risultato.get().getData() );
        // Non posso dir nulla a priori se nei giorni successivi ci saranno visite, quindi non posso asserire sugli orari

    }


}
