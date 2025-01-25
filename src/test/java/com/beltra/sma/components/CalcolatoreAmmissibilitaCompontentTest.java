package com.beltra.sma.components;

import com.beltra.sma.model.Prestazione;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CalcolatoreAmmissibilitaCompontentTest {


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
        assertEquals( Risultato.AMMISSIBILE,
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
}
