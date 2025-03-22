package com.beltra.sma.components.calcolatoreammissibilitacomponent;

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.components.Risultato;
import com.beltra.sma.model.Prestazione;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CalcolatoreAmmissibilitaCompontentTests {


    @Autowired
    CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilitaComponent;

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





    Prestazione createPrestazioneTest() {

        Prestazione prestazioneTest = new Prestazione();

        prestazioneTest.setIdPrestazione(10L);
        prestazioneTest.setCosto(20.00);
        prestazioneTest.setTicket(0.00);
        prestazioneTest.setTitolo("Prova");

        prestazioneTest.setDurataMedia(45.00);

        return prestazioneTest;
    }


    @Test
    public void testIsOrarioAmmissibile_MattinaOk() {


        LocalTime orarioTest = LocalTime.of(8, 0); // alle 8
        // 8:45 + 5 minuti di "pausa" = 8:50

        Prestazione prestazioneTest = createPrestazioneTest();

        assertTrue( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia() ) );
        Assertions.assertEquals( Risultato.AMMISSIBILE,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()) );

    }

    @Test
    public void testIsOrarioAmmissibile_MattinaFails() {

        LocalTime orarioTest = LocalTime.of(11, 20); // alle 8
        // 11:45 + 5 minuti di "pausa" = 11:50

        Prestazione prestazioneTest = createPrestazioneTest();

        assertFalse( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia()) );

        assertEquals( Risultato.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()) );

    }





    @Test
    public void testIsOrarioAmmissibile_PomeriggioOk() {

        LocalTime orarioTest = LocalTime.of(15, 0); // alle 15
        // 15:45 + 5 minuti di "pausa" = 15:50

        Prestazione prestazioneTest = createPrestazioneTest();


        assertTrue( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest, prestazioneTest.getDurataMedia()) );

        assertEquals( Risultato.AMMISSIBILE,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()) );
    }


    @Test
    public void testIsOrarioAmmissibile_PomeriggioFails() {
        LocalTime orarioTest = LocalTime.of(20, 20);
        // 20:20 + 45 (durata) + 5 (pausa) = 21:10

        Prestazione prestazioneTest = createPrestazioneTest();

        // Questo test deve essere false: orarioTest + durataMedia sfora orario di chiusura
        assertFalse( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia()) );

        assertEquals( Risultato.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO,
                      calcolatoreAmmissibilitaComponent.getRisultatoCalcoloAmmissibilitaOrario(orarioTest, prestazioneTest.getDurataMedia()) );
    }

    @Test
    public void testIsOrarioAmmissibile_BetweenChiusuraMattinaAndAperturaPomeriggioFails() {
        Prestazione prestazioneTest = createPrestazioneTest();
        LocalTime orarioTest = LocalTime.of(12, 45);

        assertFalse( calcolatoreAmmissibilitaComponent.isOrarioAmmissibile( orarioTest , prestazioneTest.getDurataMedia()) );
        assertEquals( Risultato.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO,
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
            Arguments.of(PianificazioneComponent.orarioChiusuraMattina, false), // 12:00
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
                Arguments.of(PianificazioneComponent.orarioChiusuraMattina, true), // 12:00 (da qui compreso inizia il pomeriggio)
                Arguments.of(LocalTime.of(12, 1), true), // 12:01
                Arguments.of(PianificazioneComponent.orarioAperturaPomeriggio, true), // 14:00
                Arguments.of(PianificazioneComponent.orarioChiusuraPomeriggio, true) // 21:00
        );
    }

    @ParameterizedTest
    @MethodSource("provideDatiForIsOrarioInPomeriggio")
    public void testIsOrarioInPomeriggio(LocalTime orarioInput, boolean expected) {
        assertEquals(!calcolatoreAmmissibilitaComponent.isOrarioInMattina(orarioInput), expected);
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



}
