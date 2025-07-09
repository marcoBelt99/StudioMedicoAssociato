package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.datastructures.CodaMediciDisponibili;
import com.beltra.sma.groovy.datastructures.CodaMediciDisponibiliGroovyImpl;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.Parameters;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
public class PianificazioneComponentDisponibilitaMediciTests extends PianificazioneComponentTests {


    //@Mock
    //VisitaService visitaService;


    /// #################################################################################
    /// #########################   3) DISPONIBILITÀ MEDICI   #########################
    /// #################################################################################




    /// 3.A
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = [v1, v2]
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 06:55 (< oraAperturaMattina)
    @Test
    public  void testTrovaSlotDisponibile_WithOneFreeMedico_WithOraAttualeBeforeOraAperturaMattina() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar.JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 6, 55  );
//        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite();
        List<Visita> listaVisiteTest = getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( LocalTime.of(7, 0).plusMinutes( Parameters.pausaFromVisite) ); // Mi aspetto 07:05
        Medico medicoExpected = listaMediciTest.get(2); // mi aspetto il medico 3

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );


        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        // ACT
        CodaMediciDisponibili codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMediciTest, listaVisiteTest,LocalTime.now(), durataMediaPrestazioneTest, visitaService);

        Medico medicoCheNonLavora = listaMediciTest.stream()
                .filter( med -> !codaMediciDisponibili.getMediciQueue()
                                .stream()
                                .map(Map.Entry::getKey)
                                .toList()
                                .contains(med) )
                .toList()
                .get(0);


        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario().getTime(), risultato.get().getOrario().getTime()); // Uguaglianza tra Time
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), medicoCheNonLavora.getMatricola() ); // risultato.get().getMedico().getMatricola()
    }






    /// 3.B
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = [v1, v2]
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 07:10 (> oraAperturaMattina) && (< fineVisita.oraFine)
    ///
    /// in questo caso fineVisita vale 07:20 (è l'elemento minimo di mediciMap)
    @Test
    public  void testTrovaSlotDisponibile_WithOneFreeMedico_WithOraAttualeInMattina_BeforeOraFineMinima() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 7, 10  ); // Tengo fissa l'ora attuale ( simulo now() )
//        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Visita> listaVisiteTest = getListaDiDueVisite(); // TODO: prendo solo 2 visite

        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( Parameters.pausaFromVisite) ); // TODO: Mi aspetto 07:15, perchè tanto listaVisite.size < listaMedici.size!
        Medico medicoExpected = listaMediciTest.get(2);

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        // ACT
        CodaMediciDisponibili codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMediciTest, listaVisiteTest,LocalTime.now(), durataMediaPrestazioneTest, visitaService);
        Medico medicoCheNonLavora = listaMediciTest.stream()
                .filter( med -> !codaMediciDisponibili.getMediciQueue()
                        .stream()
                        .map(Map.Entry::getKey)
                        .toList()
                        .contains(med) )
                .toList()
                .get(0);



        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        System.out.println( "RISULTATO: " + risultato.get().getOrario() );
        System.out.println("SLOT CHE MI ASPETTO: " + slotDisponibileExpected.getOrario() );
        assertEquals(slotDisponibileExpected.getOrario().getTime(), risultato.get().getOrario().getTime()); // TODO: uguaglianza tra due date
//        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), medicoCheNonLavora.getMatricola() ); // risultato.get().getMedico().getMatricola()

    }

    /// 3.C
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = (v1, v2)
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 07:30 (> oraAperturaMattina)
    @Test
    public  void testTrovaSlotDisponibile_WithOneFreeMedico_WithOraAttualeInMattina_AfterOraFineMinima() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 7, 30  ); // Tengo fissa l'ora attuale ( simulo now() )
//        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite();
        List<Visita> listaVisiteTest = getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( Parameters.pausaFromVisite) ); // Mi aspetto 07:35
        Medico medicoExpected = listaMediciTest.get(2);

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        // ACT
        CodaMediciDisponibili codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMediciTest, listaVisiteTest,LocalTime.now(), durataMediaPrestazioneTest, visitaService);
        Medico medicoCheNonLavora = listaMediciTest.stream()
                .filter( med -> !codaMediciDisponibili.getMediciQueue()
                        .stream()
                        .map(Map.Entry::getKey)
                        .toList()
                        .contains(med) )
                .toList()
                .get(0);


        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        //assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );

        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), medicoCheNonLavora.getMatricola() ); // risultato.get().getMedico().getMatricola()


    }




    /// 3.D
    /// CASO IN CUI CI SONO 2 MEDICI LIBERI (In mattinata)
    /// listaVisite = (v1)
    /// listaMedici = (m1, m2, m3) ==> m2, ed m3 sono liberi
    /// oraAttualeTest = 07:30 (> oraAperturaMattina)
    ///
    /// mi aspetto che venga assegnato m2 !!
    @Test
    public void testTrovaSlotDisponibile_WithTwoFreeMedici() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime(); // TODO: centralizzare parametri di test
        LocalTime oraAttualeTest = LocalTime.of( 7, 30 );
        // List<Visita> listaVisiteTest = datiVisiteTest.getListaDiUnaVisita(); // TODO: prendo solo 1 visita
        List<Visita> listaVisiteTest = getListaDiUnaVisita(); // TODO: prendo solo 1 visita
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1]
        // listaMedici = [m1, m2, m3] ==> m2 ed m3 sono liberi
        // mediciMap = [<1, {1, 07:20}>, <2, {0, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( Parameters.pausaFromVisite) );
        Medico medicoExpected = listaMediciTest.get(1); // mi aspetto m2

        SlotDisponibile slotDisponibileExpected = new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        // ACT
        CodaMediciDisponibili codaMediciDisponibili = new CodaMediciDisponibiliGroovyImpl(listaMediciTest, listaVisiteTest,LocalTime.now(), durataMediaPrestazioneTest, visitaService);
        Medico medicoCheNonLavora = listaMediciTest.stream()
                .filter( med -> !codaMediciDisponibili.getMediciQueue()
                        .stream()
                        .map(Map.Entry::getKey)
                        .toList()
                        .contains(med) )
                .toList()
                .get(0);

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        //assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), medicoCheNonLavora.getMatricola() ); // risultato.get().getMedico().getMatricola()


    }


    /** A partire dalla lista visite giornaliere di default, restituisce una lista lunga 1 dalla lista  */
    public List<Visita> getListaDiUnaVisita() {
        return datiVisiteTest.getListaVisiteTest().stream().limit(1).toList();
    }

    /** A partire dalla lista visite giornaliere di default, ritorna una lista lunga 2 */
    public List<Visita> getListaDiDueVisite() {
        return datiVisiteTest.getListaVisiteTest().stream().limit(2).toList();
    }


}
