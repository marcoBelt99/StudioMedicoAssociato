package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PianificazioneComponentDisponibilitaMediciTest extends PianificazioneComponentTest {

    /// #################################################################################
    /// #########################   3) DISPONIBILITÀ MEDICI   #########################
    /// #################################################################################



    /// 3.A
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = (v1, v2)
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 06:55 (< oraAperturaMattina)
    @Test
    public  void testTrovaPrimoSlotDisponibile_WithOneFreeMedico_WithOraAttualeBeforeOraAperturaMattina() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar.JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 6, 55  );
        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( LocalTime.of(7, 0).plusMinutes( PianificazioneComponent.pausaFromvisite ) ); // Mi aspetto 07:05
        Medico medicoExpected = listaMediciTest.get(2); // mi aspetto il medico 3

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertTrue(slotDisponibileExpected.getOrario().getTime() == risultato.get().getOrario().getTime()); // Uguaglianza tra Time
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }






    /// 3.B
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = (v1, v2)
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 07:10 (> oraAperturaMattina) && (< fineVisita.oraFine)
    ///
    /// in questo caso fineVisita vale 07:20 (è l'elemento minimo di mediciMap)
    @Test
    public  void testTrovaPrimoSlotDisponibile_WithOneFreeMedico_WithOraAttualeInMattina_BeforeOraFineMinima() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 7, 10  ); // Tengo fissa l'ora attuale ( simulo now() )
        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( PianificazioneComponent.pausaFromvisite ) ); // TODO: Mi aspetto 07:15, perchè tanto listaVisite.size < listaMedici.size!
        Medico medicoExpected = listaMediciTest.get(2);

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        System.out.println( "RISULTATO: " + risultato.get().getOrario() );
        System.out.println("SLOT CHE MI ASPETTO: " + slotDisponibileExpected.getOrario() );
        assertEquals(true, slotDisponibileExpected.getOrario().getTime() == risultato.get().getOrario().getTime() ); // TODO: uguaglianza tra due date
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }

    /// 3.C
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = (v1, v2)
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 07:30 (> oraAperturaMattina)
    @Test
    public  void testTrovaPrimoSlotDisponibile_WithOneFreeMedico_WithOraAttualeInMattina_AfterOraFineMinima() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 7, 30  ); // Tengo fissa l'ora attuale ( simulo now() )
        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( PianificazioneComponent.pausaFromvisite ) ); // Mi aspetto 07:35
        Medico medicoExpected = listaMediciTest.get(2);

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }




    /// 3.D
    /// CASO IN CUI CI SONO 2 MEDICI LIBERI (In mattinata)
    /// listaVisite = (v1)
    /// listaMedici = (m1, m2, m3) ==> m2, ed m3 sono liberi
    /// oraAttualeTest = 07:30 (> oraAperturaMattina)
    ///
    /// mi aspetto che venga assegnato m2 !!
    @Test
    public void testTrovaPrimoSlotDisponibile_WithTwoFreeMedici() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 7, 30 );
        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiUnaVisita(); // TODO: prendo solo 1 visita
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1]
        // listaMedici = [m1, m2, m3] ==> m2 ed m3 sono liberi
        // mediciMap = [<1, {1, 07:20}>, <2, {0, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( PianificazioneComponent.pausaFromvisite ) );
        Medico medicoExpected = listaMediciTest.get(1); // mi aspetto m2

        SlotDisponibile slotDisponibileExpected = new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );

    }
}
