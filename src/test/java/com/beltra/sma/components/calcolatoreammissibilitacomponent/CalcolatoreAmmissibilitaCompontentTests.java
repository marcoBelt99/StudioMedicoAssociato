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


import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest // non serve per fare i test unitari! Serve solo se voglio fare test di integrazione tra più componenti
public class CalcolatoreAmmissibilitaCompontentTests {

    CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilitaComponent;
    Prestazione prestazioneTest;

    //@Autowired
    @BeforeAll
    void init() {
        calcolatoreAmmissibilitaComponent = new CalcolatoreAmmissibilitaComponentImpl();
        createPrestazioneTest();
    }


    @Test
    public void testIsGiornoAmmissibile_Ok() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2024, Calendar.DECEMBER, Calendar.FRIDAY);
        Date data = gregorianCalendar.getTime();
        assertTrue(  calcolatoreAmmissibilitaComponent.isGiornoAmmissibile( data) );
    }

    @Test
    public void testIsGiornoAmmissibile_NoBecauseIsSaturday() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2024, Calendar.DECEMBER, Calendar.SATURDAY);
        Date data = gregorianCalendar.getTime();
        assertFalse(  calcolatoreAmmissibilitaComponent.isGiornoAmmissibile( data ) );
    }

    @Test
    public void testIsGiornoAmmissibile_NoBecauseIsSunday() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2024, Calendar.DECEMBER, Calendar.SUNDAY);
        Date data = gregorianCalendar.getTime();
        assertFalse(  calcolatoreAmmissibilitaComponent.isGiornoAmmissibile( data) );
    }



    @Test
    public void testIsOrarioAmmissibile_MattinaOk() {

        LocalTime orarioTest = LocalTime.of(8, 0); // alle 8
        // 8:45 + 5 minuti di "pausa" = 8:50

        assertTrue( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia() ) );
        assertEquals( RisultatoAmmissibilita.AMMISSIBILE,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()) );

    }

    @Test
    public void testIsOrarioAmmissibile_MattinaFails() {

        LocalTime orarioTest = LocalTime.of(11, 20); // alle 8
        // 11:45 + 5 minuti di "pausa" = 11:50


        assertFalse( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia()) );

        assertEquals( RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()) );

    }



    @Test
    public void testIsOrarioAmmissibile_PomeriggioOk() {

        LocalTime orarioTest = LocalTime.of(15, 0); // alle 15
        // 15:45 + 5 minuti di "pausa" = 15:50


        assertTrue( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest, prestazioneTest.getDurataMedia()) );

        assertEquals( RisultatoAmmissibilita.AMMISSIBILE,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()) );
    }


    @Test
    public void testIsOrarioAmmissibile_PomeriggioFails() {
        LocalTime orarioTest = LocalTime.of(20, 20);
        // 20:20 + 45 (durata) + 5 (pausa) = 21:10

        // Questo test deve essere false: orarioTest + durataMedia sfora orario di chiusura
        assertFalse( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia()) );
        assertEquals( RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()) );
    }

    @Test
    public void testIsOrarioAmmissibile_BetweenChiusuraMattinaAndAperturaPomeriggioFails() {

        LocalTime orarioTest = LocalTime.of(12, 45);
        assertFalse( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia()) );
        assertEquals( RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario( orarioTest, prestazioneTest.getDurataMedia() )
        );
    }



    @Test
    public void testCondizioneSoddisfacibilita_Ok() {
        LocalTime orarioDaControllare = LocalTime.of(7, 1);
        assertEquals( true, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita( orarioDaControllare) );
    }


    @Test
    public void testCondizioneSoddisfacibilita_NoBecauseBeforeOpeningMattina() {
        LocalTime orarioDaControllare = LocalTime.of(6, 59);
        assertEquals( false, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita(orarioDaControllare) );
    }



    @Test
    public void testCondizioneSoddisfacibilita_NoBecauseAfterClosingMattina() {
        LocalTime orarioDaControllare = LocalTime.of(12, 1);
        assertEquals( false, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita(orarioDaControllare) );
    }


    @Test
    public void testCondizioneSoddisfacibilita_NoBecauseBeforeOpeningPomeriggio() {
        LocalTime orarioDaControllare = LocalTime.of(13, 59);
        assertEquals( false, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita(orarioDaControllare) );
    }


    @Test
    public void testCondizioneSoddisfacibilita_NoBecauseAfterClosingPomeriggio() {
        LocalTime orarioDaControllare = LocalTime.of(21, 1);
        assertEquals( false, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita(orarioDaControllare) );
    }


    // TODO: E' bene testare anche le condizioni di confine, infatti lo faccio nei seguenti metodi:
    @Test
    public void testCondizioneSoddisfacibilita_BoundaryOpeningMattina() {
        LocalTime orarioDaControllare = LocalTime.of(7, 0);
        assertEquals(true, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita(orarioDaControllare));
    }

    @Test
    public void testCondizioneSoddisfacibilita_BoundaryClosingMattina() {
        LocalTime orarioDaControllare = LocalTime.of(12, 0);
        assertEquals(true, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita(orarioDaControllare));
    }


    @Test
    public void testCondizioneSoddisfacibilita_BoundaryOpeningPomeriggio() {
        LocalTime orarioDaControllare = LocalTime.of(14, 0);
        assertEquals(true, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita(orarioDaControllare));
    }


    @Test
    public void testCondizioneSoddisfacibilita_BoundaryClosingPomeriggio() {
        LocalTime orarioDaControllare = LocalTime.of(21, 0);
        assertEquals(true, calcolatoreAmmissibilitaComponent.condizioneSoddisfacibilita(orarioDaControllare));
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
    void testIsOrarioAfterChiusuraPomeriggio_ShouldReturnTrue(){
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