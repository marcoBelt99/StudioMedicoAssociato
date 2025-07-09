package com.beltra.sma.components.calcolatoreammissibilitacomponent;

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl;
import com.beltra.sma.components.RisultatoAmmissibilita;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.utils.Parameters;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;



import java.time.LocalTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest // non serve per fare i test unitari! Serve solo se voglio fare test di integrazione tra più componenti
public class CalcolatoreAmmissibilitaCompontentTests {

    CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilitaComponent;
    Prestazione prestazioneTest;


    @BeforeAll
    void init() {
        calcolatoreAmmissibilitaComponent = new CalcolatoreAmmissibilitaComponentImpl();
        createPrestazioneTest();
    }


    ///  ########################################
    ///  TEST: isGiornoAmmissibile()
    ///  ########################################


    private static Stream<Arguments>provideDatiForIsGiornoAmmissibile() {
        // Arrange
        return
            Stream.of(
                Arguments.of(Calendar.FRIDAY, true),
                Arguments.of(Calendar.SATURDAY, false),
                Arguments.of(Calendar.SUNDAY, false)
            );
    }


    @ParameterizedTest(name = "{index} => giorno={0}, expected={1}")
    @MethodSource("provideDatiForIsGiornoAmmissibile")
    public void isGiornoAmmissibileWorks( int giornoTest, boolean risultatoExpected) {

        Date dataTest = new GregorianCalendar(2024, Calendar.DECEMBER, giornoTest).getTime();
        // Act + Assert
        assertThat( risultatoExpected ).isEqualTo(calcolatoreAmmissibilitaComponent.isGiornoAmmissibile( dataTest) );
    }


    ///  ########################################
    ///  TEST: condizioneSoddisfacibilita()
    ///  ########################################
    private static Stream<Arguments>provideDatiForCondizioneSoddisfacibilita() {
        // Arrange
        return Stream.of(

                /// Verifico il funzionamento aspettato
                Arguments.of( LocalTime.of(6, 59), false), //   No, perche' prima di orario apertura mattina
                Arguments.of( LocalTime.of(7, 1), true), //     Ok, perche' dopo orario apertura mattina
                Arguments.of(  LocalTime.of(12, 1), false), //  No, perche' dopo orario chiusura mattina
                Arguments.of(  LocalTime.of(13, 59), false), // No, perche' between chiusura mattina and before apertura pomeriggio
                Arguments.of(  LocalTime.of(20, 59), true), //  Ok, perche' prima di chiusura pomeriggio
                Arguments.of(  LocalTime.of(21, 1), false), //  No, perche' dopo orario chiusura pomeriggio


                /// E' bene testare anche le condizioni di confine, infatti lo faccio coi seguenti dati:
                Arguments.of(  Parameters.orarioAperturaMattina, true),
                Arguments.of(  Parameters.orarioChiusuraMattina, true),
                Arguments.of(  Parameters.orarioAperturaPomeriggio, true),
                Arguments.of(  Parameters.orarioChiusuraPomeriggio, true)

        );
    }


    @ParameterizedTest
    @MethodSource("provideDatiForCondizioneSoddisfacibilita")
    public void condizioneSoddisfacibilita_Works( LocalTime orarioTest, boolean risultatoExpected) {
        // Act + Assert
        assertThat( risultatoExpected ).isEqualTo(calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita( orarioTest));
    }



    ///  ########################################
    ///  TEST: isOrarioAmmissibile()
    ///  ########################################


    private static Stream<Arguments>provideDatiForIsOrarioAmmissibile() {
        // Arrange
        return Stream.of(

                /// MATTINA
                // Dalle 06:09 + 45 min  ==> 06:54 + "pausa" (5 min)  ==> 06:59 ==> non ammissibile
                Arguments.of(LocalTime.of(6, 9), false, RisultatoAmmissibilita.NO_BECAUSE_BEFORE_APERTURA_MATTINA),
                // Dalle 06:10 + 45 min  ==> 06:55 + "pausa" (5 min)  ==> 07:00 ==> non ammissibile (perche' non vale condizioneSoddisfacibilita su oraAttuale non maggiorata, vale però su oraAttuale maggiorata)
                Arguments.of(LocalTime.of(6, 11), false, RisultatoAmmissibilita.NO_BECAUSE_BEFORE_APERTURA_MATTINA),
                // Dalle 06:11 + 45 min  ==> 06:56 + "pausa" (5 min)  ==> 07:01 ==> non ammissibile (perchè non vale condizioneSoddisfacibilita su oraAttuale non maggiorata)
                Arguments.of(LocalTime.of(6, 10), false, RisultatoAmmissibilita.NO_BECAUSE_BEFORE_APERTURA_MATTINA), // Caso di confine

                // Dalle 07:00 + 45 min  ==> 07:45 + "pausa" (5 min)  ==> 07:50 ==> ammissibile (perchè vale condizioneSoddisfacibilita sia su oraAttuale non maggiorata, che su oraAttuale maggiorata)
                Arguments.of(Parameters.orarioAperturaMattina, true, RisultatoAmmissibilita.AMMISSIBILE), // Caso di confine
                // Dalle 07:01 + 45 min  ==> 07:46 + "pausa" (5 min)  ==> 07:51 ==> ammissibile (perchè vale condizioneSoddisfacibilita sia su oraAttuale non maggiorata, che su oraAttuale maggiorata)
                Arguments.of(LocalTime.of(7, 1), true, RisultatoAmmissibilita.AMMISSIBILE), // Caso di confine
                // Dalle 08:00 + 45 min  ==> 08:45 + "pausa" (5 min)  ==> 8:50 ==> ammissibile
                Arguments.of( LocalTime.of(8,0), true, RisultatoAmmissibilita.AMMISSIBILE ),
                // Dalle 11:09 + 45 min  ==> 11:54 + "pausa" (5 min)  ==> 11:59 ==> ammissibile
                Arguments.of( LocalTime.of(11,9), true, RisultatoAmmissibilita.AMMISSIBILE ), // Caso di confine

                // Dalle 11:10 + 45 min  ==> 11:55 + "pausa" (5 min)  ==> 12:00 ==> ammissibile
                Arguments.of( LocalTime.of(11,10), true, RisultatoAmmissibilita.AMMISSIBILE ), // Caso di confine

                // Dalle 11:11 + 45 min  ==> 11:56 + "pausa" (5 min)  ==> 12:01 ==> ammissibile
                Arguments.of( LocalTime.of(11,11), false, RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ), // Caso di confine
                // Dalle 11:20 + 45 min  ==> 12:05 +  "pausa" (5 min) ==>  12:10 ==> non ammissibile
                Arguments.of( LocalTime.of(11, 20), false, RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ),
                // Dalle 12:45 + 45 min  ==> 13:30 +  "pausa" (5 min) ==>  13:35 ==> non ammissibile
                Arguments.of( LocalTime.of(12, 45), false, RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ),


                /// POMERIGGIO
                // Dalle 13:10 + 45 min  ==> 13:55 +   "pausa" (5 min) ==> 14:00 ==> non ammissibile
                Arguments.of( LocalTime.of(13, 10), false, RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ),
                // Dalle 13:11 + 45 min  ==> 13:56 +   "pausa" (5 min) ==> 14:01 ==> non ammissibile
                Arguments.of( LocalTime.of(13, 11), false, RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO ),

                // Dalle 15:00 + 45 min  ==> 15:45 +   "pausa" (5 min) ==> 15:50 ==> ammissibile
                Arguments.of( LocalTime.of(15, 0), true, RisultatoAmmissibilita.AMMISSIBILE ),
                // Dalle 20:09 + 45 min  ==> 20:54 +   "pausa" (5 min) ==> 20:59 ==> ammissibile
                Arguments.of( LocalTime.of(20, 9), true, RisultatoAmmissibilita.AMMISSIBILE ),
                // Dalle 20:10 + 45 min  ==> 20:55 +   "pausa" (5 min) ==> 21:00 ==> non ammissibile
                Arguments.of( LocalTime.of(20, 10), true, RisultatoAmmissibilita.AMMISSIBILE ), // Caso di confine
                // Dalle 20:11 + 45 min  ==> 20:56 +   "pausa" (5 min) ==> 21:01 ==> non ammissibile
                Arguments.of( LocalTime.of(20, 11), false, RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO ), // Caso di confine
                // Dalle 20:20 + 45 min ==> 21:05 +   "pausa" (5 min) ==> 21:10 ==> non ammissibile
                Arguments.of( LocalTime.of(20, 20), false, RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO )

        );
    }


    @ParameterizedTest
    @MethodSource("provideDatiForIsOrarioAmmissibile")
    public void isOrarioAmmissibile_Works( LocalTime orarioTest, boolean risultatoExpected, RisultatoAmmissibilita risultatoAmmissibilitaExpected) {

        // Act + Assert
        assertThat( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia() ) ).isEqualTo(risultatoExpected);
        assertThat( risultatoAmmissibilitaExpected ).isEqualTo(calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()));

    }





/// #############################################################################################
/// #############################################################################################
/// #############################################################################################
// TODO: da questi test, emerge :
//       - orario 00:00 (compresa) è considerato in pomeriggio
//       - orario 00:01 è in mattina
//       - orario dalle 12:00 (comprese) è considerato pomeriggio

    private static Stream<Arguments>provideDatiForIsOrarioInMattina() {
        return Stream.of(
    Arguments.of(LocalTime.of(23, 1), false), // 23:01
            Arguments.of(LocalTime.MAX, false), // 23:59:59.99999999
            Arguments.of(LocalTime.MIDNIGHT, false), // 00:00
            Arguments.of(Parameters.orarioChiusuraMattina, false), // 12:00
            Arguments.of(LocalTime.of(0, 1), true), // 00:01
            Arguments.of(LocalTime.of(8, 35), true), // 08:35
            Arguments.of(LocalTime.of(11, 59), true) // 11:59
        );
    }

    @ParameterizedTest
    @MethodSource("provideDatiForIsOrarioInMattina")
    public void testIsOrarioInMattina( LocalTime orarioInput, boolean expected) {
        assertEquals(calcolatoreAmmissibilitaComponent.isOrarioInMattina(orarioInput), expected);
    }


    private static Stream<Arguments> provideDatiForIsOrarioInPomeriggio() {
        return Stream.of(
        Arguments.of(LocalTime.of(23, 1), true),
                Arguments.of(LocalTime.MAX, true), // 23:59:59.999999999
                Arguments.of(LocalTime.MIDNIGHT, true), // 00:00
                Arguments.of(LocalTime.of(0,1), false), // 00:01 (da qui compreso inizia la mattina)
                Arguments.of(LocalTime.of(11,59), false), // siamo ancora in mattina
                Arguments.of(Parameters.orarioChiusuraMattina, true), // 12:00 (da qui compreso inizia il pomeriggio)
                Arguments.of(LocalTime.of(12, 1), true), // 12:01
                Arguments.of(Parameters.orarioAperturaPomeriggio, true), // 14:00
                Arguments.of(Parameters.orarioChiusuraPomeriggio, true) // 21:00
        );
    }

    @ParameterizedTest
    @MethodSource("provideDatiForIsOrarioInPomeriggio")
    public void testIsOrarioInPomeriggio(LocalTime orarioInput, boolean expected) {
        assertEquals(expected, !calcolatoreAmmissibilitaComponent.isOrarioInMattina(orarioInput));
    }

    @Test
    void testIsDurataMediaContenuta_ShouldReturnTrue(){

        LocalTime orarioFineTest = LocalTime.of(11, 35);
        LocalTime orarioChiusuraTest = LocalTime.of(12,0);
        Double durataTest = 19.0;
        boolean risultato = calcolatoreAmmissibilitaComponent.isDurataMediaContenuta(durataTest, orarioFineTest, orarioChiusuraTest);

        assertTrue(risultato);

    }

    @Test
    void testIsDurataMediaContenuta_WithCondizioneDiConfine_ShouldReturnTrue(){

        LocalTime orarioFineTest = LocalTime.of(11, 35);
        LocalTime orarioChiusuraTest = LocalTime.of(12,0);
        Double durataTest = 20.0;
        boolean risultato = calcolatoreAmmissibilitaComponent.isDurataMediaContenuta(durataTest, orarioFineTest, orarioChiusuraTest);

        assertTrue(risultato);
    }


    @Test
    void testIsDurataMediaContenuta_ShouldReturnFalse(){

        LocalTime orarioFineTest = LocalTime.of(11, 35);
        LocalTime orarioChiusuraTest = LocalTime.of(12,0);
        Double durataTest = 21.0;
        boolean risultato = calcolatoreAmmissibilitaComponent.isDurataMediaContenuta(durataTest, orarioFineTest, orarioChiusuraTest);

        assertFalse(risultato);

    }


    @Test
    void testIsDurataMediaContenuta_ShouldReturnYes(){

        LocalTime orarioFineTest = LocalTime.of(9, 55);
        LocalTime orarioChiusuraTest = LocalTime.of(12,0);
        Double durataTest = 120.0;
        boolean risultato = calcolatoreAmmissibilitaComponent.isDurataMediaContenuta(durataTest, orarioFineTest, orarioChiusuraTest);

        assertTrue(risultato);

    }


    // TODO: magari posso parametrizzare questi test relativi al metodo findNextGiornoAmmissibile():
    @Test
    void testFindNextGiornoAmmissibile_FromFriday_ShouldReturnMonday(){
        // Arrange
        Date dataTest = new GregorianCalendar(2025, Calendar.MAY, 16).getTime(); // Venerdì
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataTest);

        Date dataExpected = new GregorianCalendar(2025, Calendar.MAY, 19).getTime();


        // Act
        Date dataActual = calcolatoreAmmissibilitaComponent.findNextGiornoAmmissibile(dataTest, calendar );

        // Assert
        assertEquals(dataExpected, dataActual);
    }


    @Test
    void testFindNextGiornoAmmissibile_FromSaturday_ShouldReturnMonday(){
        // Arrange
        Date dataTest = new GregorianCalendar(2025, Calendar.MAY, 17).getTime(); // Sabato
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataTest);

        Date dataExpected = new GregorianCalendar(2025, Calendar.MAY, 19).getTime();


        // Act
        Date dataActual = calcolatoreAmmissibilitaComponent.findNextGiornoAmmissibile(dataTest, calendar );

        // Assert
        assertEquals(dataExpected, dataActual);
    }


    @Test
    void testFindNextGiornoAmmissibile_FromSunday_ShouldReturnMonday(){
        // Arrange
        Date dataTest = new GregorianCalendar(2025, Calendar.MAY, 18).getTime(); // Domenica
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataTest);

        Date dataExpected = new GregorianCalendar(2025, Calendar.MAY, 19).getTime();


        // Act
        Date dataActual = calcolatoreAmmissibilitaComponent.findNextGiornoAmmissibile(dataTest, calendar );

        // Assert
        assertEquals(dataExpected, dataActual);
    }


    @Test
    void testFindNextGiornoAmmissibile_FromMonday_ShouldReturnTuesday(){
        // Arrange
        Date dataTest = new GregorianCalendar(2025, Calendar.MAY, 19).getTime(); // Lunedì
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataTest);

        Date dataExpected = new GregorianCalendar(2025, Calendar.MAY, 20).getTime();


        // Act
        Date dataActual = calcolatoreAmmissibilitaComponent.findNextGiornoAmmissibile(dataTest, calendar );

        // Assert
        assertEquals(dataExpected, dataActual);
    }


// ##################################################################
    // Voglio che le "21:00" sia ancora in pomeriggio:
    //  i seguenti test dovrebbero catturare questa casistica.

    @Test
    void testIsOrarioAfterChiusuraPomeriggio_ShouldReturnTrue() {
       assertTrue( calcolatoreAmmissibilitaComponent.isOrarioAfterChiusuraPomeriggio(LocalTime.of(21, 1)) );
    }

    @Test
    void testIsOrarioAfterChiusuraPomeriggio_ShouldReturnFalseBecauseBeforeBoundaryValue(){
        assertFalse( calcolatoreAmmissibilitaComponent.isOrarioAfterChiusuraPomeriggio(LocalTime.of(20, 59)) );
    }

    @Test
    void testIsOrarioAfterChiusuraPomeriggio_ExactlyBoundaryValue_ShouldReturnFalse(){
        assertFalse( calcolatoreAmmissibilitaComponent.isOrarioAfterChiusuraPomeriggio(LocalTime.of(21, 0)) );
    }


// #############################################################################################
// ######################### test metodo: isOrarioAmmissibileInMattina ###################
// ##########################################################################################

@Test
void testIsOrarioAmmissibileInMattinaShouldReturnTrue() {
    assertTrue (calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInMattina( LocalTime.of(10, 0), 20.0 ) );
}


@Test
void testIsOrarioAmmissibileInMattinaShouldReturnFalse() {
    assertFalse (calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInMattina( LocalTime.of(13, 0), 20.0 ) );
}

@Test
void testIsOrarioAmmissibileInMattina_BoundaryValue_ShouldReturnTrue() {
    // si da per scontato in isOrarioAmmissibileInMattina che venga aggiunta anche la pausa
    assertTrue (calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInMattina( LocalTime.of(11, 35), 20.0 ) );
}

@Test
void testIsOrarioAmmissibileInMattina_BoundaryValue_ShouldReturnFalse() {
    assertFalse (calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInMattina( LocalTime.of(11, 36), 20.0 ) );
}


// #############################################################################################
// ######################### test metodo: isOrarioAmmissibileInPomeriggio ###################
// ##########################################################################################

    @Test
    void testOrarioValidoInPomeriggio() {
        // Ad esempio 15:00, con durata 20 min è entro 21:00
        LocalTime orario = LocalTime.of(15, 0);
        boolean risultato = calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInPomeriggio(orario, 20.0);
        assertTrue(risultato);
    }

    @Test
    void testOrarioNonAmmissibileInPomeriggio() {
        // Dopo la chiusura pomeridiana, es: 21:30
        LocalTime orario = LocalTime.of(21, 30);
        boolean risultato = calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInPomeriggio(orario, 20.0);
        assertFalse(risultato);
    }

    @Test
    void testOrarioAlLimitePomeriggio() {
        // Esattamente alle 21:00 (chiusura), che dovrebbe essere fuori
        boolean risultato = calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInPomeriggio(Parameters.orarioChiusuraPomeriggio, 20.0);
        assertFalse(risultato);
    }

    @Test
    void testIsOrarioInMattina() {
        // Es. 10:00, chiaramente mattina
        LocalTime orario = LocalTime.of(10, 0);
        boolean risultato = calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInPomeriggio(orario, 20.0);
        assertFalse(risultato);
    }

    @Test
    void testOrarioTraMattinaEPomeriggio() {
        // Es. 13:00 (fascia di chiusura pranzo)
        boolean risultato = calcolatoreAmmissibilitaComponent.isOrarioAmmissibileInPomeriggio(Parameters.orarioChiusuraMattina, 20.0);
        assertFalse(risultato);
    }




    /// #############################
    /// ##### Metodi di utilità #####
    /// #############################

    void createPrestazioneTest() {

        prestazioneTest = new Prestazione();

        prestazioneTest.setIdPrestazione(10L);
        prestazioneTest.setCosto(20.00);
        prestazioneTest.setTicket(0.00);
        prestazioneTest.setTitolo("Prova");

        prestazioneTest.setDurataMedia(45.00);

    }


}