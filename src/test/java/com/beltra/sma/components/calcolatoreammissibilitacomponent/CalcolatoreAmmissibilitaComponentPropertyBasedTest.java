//package com.beltra.sma.components.calcolatoreammissibilitacomponent;
//
//import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
//import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl;
//import com.beltra.sma.components.PianificazioneComponent;
//import com.pholser.junit.quickcheck.Property;
//import com.pholser.junit.quickcheck.generator.InRange;
//import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
//import org.junit.jupiter.api.Assertions;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalTime;
//import java.util.Calendar;
//import java.util.Date;
//
//@RunWith(JUnitQuickcheck.class)
//@SpringBootTest
//public class CalcolatoreAmmissibilitaComponentPropertyBasedTest {
//
//    @Autowired
//    CalcolatoreAmmissibilitaComponentImpl calcolatore;
//
//    // Property Test per isOrarioAmmissibile
//    @Property
//    public void testIsOrarioAmmissibile(
//            @InRange(min = "00:00", max = "23:59") LocalTime ora,
//            @InRange(minDouble = 0.0, maxDouble = 120.0) Double durata) {
//        boolean risultato = calcolatore.isOrarioAmmissibile(ora, durata);
//
//        // Proprietà da verificare:
//        // Deve essere ammissibile o nella mattina o nel pomeriggio
//        boolean ammissibile = calcolatore.isOrarioAmmissibileInMattina(ora, durata)
//                || calcolatore.isOrarioAmmissibileInPomeriggio(ora, durata);
//
//        Assertions.assertEquals(ammissibile, risultato);
//    }
//
//    // Property Test per aggiungiDurataAndPausa
//    @Property
//    public void testAggiungiDurataAndPausa(
//            @InRange(min = "00:00", max = "23:55") LocalTime ora,
//            @InRange(minDouble = 0.0, maxDouble = 120.0) Double durata) {
//        LocalTime risultato = calcolatore.aggiungiDurataAndPausa(ora, durata);
//
//        // Proprietà da verificare:
//        // - Deve includere la durata e la pausa di 5 minuti
//        Assertions.assertEquals(ora.plusMinutes(durata.intValue()+ 5), risultato);
//    }
//
//    // Property Test per isStessoGiorno
//    @Property
//    public void testIsStessoGiorno(
//            Date data1,
//            Date data2) {
//        boolean stessoGiorno = calcolatore.isStessoGiorno.test(data1, data2);
//
//        // Proprietà da verificare:
//        // Gli stessi giorni devono restituire true
//        Assertions.assertEquals(isSameDay(data1, data2), stessoGiorno);
//    }
//
//    // Property Test per isOrarioAmmissibileInMattina
//    @Property
//    public void testIsOrarioAmmissibileInMattina(
//            @InRange(min = "06:00", max = "12:00") LocalTime ora,
//            @InRange(minDouble = 0.0, maxDouble = 120.0) Double durata) {
//        boolean risultato = calcolatore.isOrarioAmmissibileInMattina(ora, durata);
//
//        // Proprietà da verificare:
//        // - L'orario deve rientrare nella fascia mattutina
//        boolean contenutoInMattina = !ora.isBefore(PianificazioneComponent.orarioAperturaMattina)
//                && !ora.plusMinutes(durata.intValue()).isAfter(PianificazioneComponent.orarioChiusuraMattina);
//
//        Assertions.assertEquals(contenutoInMattina, risultato);
//    }
//
//    // Property Test per isOrarioAmmissibileInPomeriggio
//    @Property
//    public void testIsOrarioAmmissibileInPomeriggio(
//            @InRange(min = "12:01", max = "18:00") LocalTime ora,
//            @InRange(minDouble = 0.0, maxDouble = 120.0) Double durata) {
//        boolean risultato = calcolatore.isOrarioAmmissibileInPomeriggio(ora, durata);
//
//        // Proprietà da verificare:
//        // L'orario deve rientrare nella fascia pomeridiana
//        boolean contenutoInPomeriggio = !ora.isBefore(PianificazioneComponent.orarioAperturaPomeriggio)
//                && !ora.plusMinutes(durata.intValue()).isAfter(PianificazioneComponent.orarioChiusuraPomeriggio);
//
//        Assertions.assertEquals(contenutoInPomeriggio, risultato);
//    }
//
//    // Funzione di utilità per confrontare il giorno delle date
//    private boolean isSameDay(Date data1, Date data2) {
//        Calendar cal1 = Calendar.getInstance();
//        cal1.setTime(data1);
//
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTime(data2);
//
//        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
//    }
//}
