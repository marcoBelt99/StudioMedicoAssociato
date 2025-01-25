package com.beltra.sma.components;

import com.beltra.sma.components.data.DatiMediciTest;
import com.beltra.sma.components.data.DatiPrestazioniTest;
import com.beltra.sma.components.data.DatiTest;
import com.beltra.sma.components.data.DatiVisiteTest;
import com.beltra.sma.model.*;
import com.beltra.sma.service.MedicoService;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
public class PianificazioneComponentTest {


    @Autowired
    private PianificazioneComponent pianificazioneComponent;

    @Autowired
    private VisitaService visitaService;

    @Autowired
    private MedicoService medicoService;


    /**  TODO: In questo metodo inserisco i dati di test per i vari casi di test specifici. */
    public List<Visita> getAllVisiteByData() {
        DatiVisiteTest datiVisiteTest = new DatiVisiteTest();
        return datiVisiteTest
                .getListaVisiteTest()
                .stream()
                .filter( v -> v.getDataVisita().after( DatiTest.dataTest_16Gennaio2025 ) ).toList();
    }

    public List<Medico> getAllDatiMediciTests() {
        DatiMediciTest datiMediciTest = new DatiMediciTest();
        return datiMediciTest.getDatiTest();
    }

    public List<Prestazione> getAllPrestazioniTests() {
        DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
        return datiPrestazioniTest.getDatiTest();
    }



    /// #################################################################################
    /// CASO PIÙ SEMPLICE: LISTA VISITE VUOTA


    /// slot.data = dataAttuale, slot.ora=07:05, medico=m1
    /// dataTest = 17/01/2025 = venerdì
    /// oraAttualeTest = 06:55
    @Test
    public void testTrovaPrimoSlotDisponibile_WithListaVisiteEmpty_WithOrarioBeforeAperturaMattina() {
        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();  //
        LocalTime oraAttualeTest = LocalTime.of( 6, 55  ); // Tengo fissa l'ora attuale ( simulo now() )
        List<Medico> listaMediciTest = medicoService.getAllMedici();
        List<Visita> listaVisiteTest = visitaService.getAllVisiteByData( dataTest );



        SlotDisponibile slotDisponibileTest = new SlotDisponibile();
        slotDisponibileTest.setData( dataTest );
        slotDisponibileTest.setOrario( Time.valueOf(
                PianificazioneComponent.orarioAperturaMattina.plusMinutes(PianificazioneComponent.pausaFromvisite) )
        );
        slotDisponibileTest.setMedico( medicoService.getAllMedici().get(0) );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                    durataMediaPrestazioneTest,
                    dataTest,
                    oraAttualeTest,
                    listaMediciTest,
                    listaVisiteTest

        );

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileTest.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileTest.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() ); // non riuscivo ad eguagliare gli oggetti, quindi controllo tramite id
        assertEquals( slotDisponibileTest.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() ); // e tramite matricola
        assertEquals( slotDisponibileTest.getOrario(), risultato.get().getOrario() );

        // assertSame( slotDisponibileTest, risultato.get() );
    }


    /// slot.data = dataAttuale, slot.ora=09:35, medico=m1
    /// dataTest = 17/01/2025 = venerdì
    /// oraAttualeTest = 07:50
    @Test
    public void testTrovaPrimoSlotDisponibile_WithListaVisiteEmpty_WithOrarioAmmissibileInMattina() {
        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();  //
        LocalTime oraAttualeTest = LocalTime.of( 7, 50  ); // Tengo fissa l'ora attuale ( simulo now() )
        List<Medico> listaMediciTest = medicoService.getAllMedici();
        List<Visita> listaVisiteTest = visitaService.getAllVisiteByData( dataTest );


        SlotDisponibile slotDisponibileTest = new SlotDisponibile();
        slotDisponibileTest.setData( dataTest );
        slotDisponibileTest.setOrario( Time.valueOf(
               oraAttualeTest.plusMinutes( durataMediaPrestazioneTest.intValue() ).plusMinutes(PianificazioneComponent.pausaFromvisite) )
        );
        slotDisponibileTest.setMedico( medicoService.getAllMedici().get(0) );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest

                );

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileTest.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileTest.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() ); // non riuscivo ad eguagliare gli oggetti, quindi controllo tramite id
        assertEquals( slotDisponibileTest.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() ); // e tramite matricola
        assertEquals( slotDisponibileTest.getOrario(), risultato.get().getOrario() );

        // assertSame( slotDisponibileTest, risultato.get() );
    }


    /** dataTest = 17/01/2025 = venerdì
     *  oraAttualeTest = 06:55 (prima dell'orario di apertura)
     *  */
    @Test
    public void testTrovaPrimoSlotDisponibile_WithMattinoAmmissibileAndPomeriggioVuoto() {
        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 6, 55  ); // Tengo fissa l'ora attuale ( simulo now() )
        List<Visita> listaVisiteTest = getAllVisiteByData(  ); // come dati di test ho le solite 6 visite (quelle disegnate su carta)
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)

        // Dopo inserimento di v6 ho:
        // listaVisite = [v1, v2, ..., v6]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<3, 09:30>, <2, 10:05>, <1, 11:30>]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( LocalTime.of(9, 30)
                .plusMinutes( PianificazioneComponent.pausaFromvisite ) ); // Mi aspetto 09:35
        Medico medicoExpected = listaMediciTest.get(2); // prendo medico di id=3 (sta in terza posizione, a partire da 0)

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

        // ASSERT
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }




    // visiteMattino = [v1, v2, v3, __], visitePomeriggio = []
    @Test
    public void testTrovaPrimoSlotDisponibile_WithMattinoNotAmmissibileAndPomeriggioVuoto() {
        // ARRANGE
        // TODO: durata presetazione esagerata, in modo da farmi avere lo slot disponibile alle 14:05
        Double durataMediaPrestazioneTest = getAllPrestazioniTests().get(3).getDurataMedia();  // durata = 4 ore
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime(); // TODO: 17/01/2025 (mi servono le visite). E' una data rappresentativa (copre molti casi di test).
        LocalTime oraAttualeTest = LocalTime.of( 6, 55  ); // Tengo fissa l'ora attuale ( simulo now() )
        List<Visita> listaVisiteTest = getAllVisiteByData(  ); // come dati di test ho le solite 6 visite (quelle disegnate su carta)
        List<Medico> listaMediciTest = getAllDatiMediciTests(); //  come dati di test mi servono anche i medici (sono != da quelli a DB)

        // Dopo inserimento di v6 ho:
        // listaVisite = [v1, v2, ..., v6]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<3, 09:30>, <2, 10:05>, <1, 11:30>]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf(
                PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes(PianificazioneComponent.pausaFromvisite)
        );  //
        Medico medicoExpected = listaMediciTest.get(0); // prendo il primo medico medico (di id=1), perchè non essendocene di occupati, si riparte dal primo in lista

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
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }


/*
    @Test
    public void testTrovaPrimoSlotDisponibile_Ok() {

        // Durata della visita richiesta
        double durataVisita = 15.0; // 15 minuti

        // Chiama il metodo da testare
        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile( durataVisita, new Date(), LocalTime.now(), getAllDatiMediciTests(), getAllVisiteByData() );

        // Verifica che lo slot sia presente e sia corretto
        assertTrue( slotDisponibile.isPresent(), "Nessuno slot disponibile trovato");
        assertEquals( Time.valueOf("07:00:00"), slotDisponibile.get().getOrario(), "L'orario dello slot non è corretto");
        assertEquals( PianificazioneManagerTest.dataAttualeDiTest, slotDisponibile.get().getData(), "La data dello slot non è corretta");
        assertNotNull( slotDisponibile.get().getMedico(), "Nessun medico assegnato allo slot");

        System.out.println("");
        System.out.println( "Medico da assegnare:\tData:\tOrario:\n"
                + slotDisponibile.get().getMedico().getAnagrafica().getCognome() +"\t"
                + slotDisponibile.get().getData() +"\t"
                + slotDisponibile.get().getOrario()
        );
    }





    @Test
    public void testTrovaPrimoSlotDisponibile_WithDurataPrestazioneQuindiciMinuti() {

        DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();

        List<Visita> listaVisiteTest = visitaService.getAllVisiteStartingFromNow();



        // Mi invento alcune prestazione con durata lunghissima
        // così facendo riesco a "spostarmi" di più tra un giorno lavorativo e il successivo
//        List<Prestazione> listaPrestazioniTest = PianificazioneManagerTest.getListaPrestazioniTest();
        List<Prestazione> listaPrestazioniTest = datiPrestazioniTest.getDatiTest();

        Prestazione prestazioneBreve = listaPrestazioniTest.get( 4 ); // 15 min


        // TODO: Mi domando: Quando schedulo una visita avente prestazione con durata n ?
        //  ===> Me lo deve dire il metodo trovaPrimoSlotDisponibile()

        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile (
                        prestazioneBreve.getDurataMedia(),
                        new Date(),
                        LocalTime.now(),
                        getAllDatiMediciTests(),
                        getAllVisiteByData()
                );

        slotDisponibile.ifPresent( sd -> {
            System.out.println("\n");
            System.out.println( "**** SLOT TROVATO:" + sd + " ****" );
            System.out.println("\n");
        } );

        System.out.println("\n");

        System.out.println("VISITE COMPRESE TRA ADESSO E LA DATA DELL'ULTIMA VISITA:\n");
        visitaService.getAllVisiteStartingFromNow().forEach(System.out::println);

        System.out.println("\n");



    }

    @Test
    public void testTrovaPrimoSlotDisponibile_WithDurataPrestazioneDueOre() {

        DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();

        List<Visita> listaVisiteTest = visitaService.getAllVisiteStartingFromNow();


        // Mi invento alcune prestazione con durata lunghissima
        // così facendo riesco a "spostarmi" di più tra un giorno lavorativo e il successivo
//        List<Prestazione> listaPrestazioniTest = PianificazioneManagerTest.getListaPrestazioniTest();
        List<Prestazione> listaPrestazioniTest = datiPrestazioniTest.getDatiTest();


        Prestazione prestazione2Ore = listaPrestazioniTest.get( 5); // 2 ore



        // TODO: Mi domando: Quando schedulo una visita avente prestazione con durata n ?
        //  ===> Me lo deve dire il metodo trovaPrimoSlotDisponibile()

        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile (
                        prestazione2Ore.getDurataMedia(),
                        new Date(),
                        LocalTime.now(),
                        getAllDatiMediciTests(),
                        getAllVisiteByData()
                );

        slotDisponibile.ifPresent( sd -> {
            System.out.println("\n");
            System.out.println( "**** SLOT TROVATO:" + sd + " ****" );
            System.out.println("\n");
        } );

        System.out.println("\n");

        System.out.println("VISITE COMPRESE TRA ADESSO E LA DATA DELL'ULTIMA VISITA:\n");
        visitaService.getAllVisiteStartingFromNow().forEach(System.out::println);

        System.out.println("\n");

    }

    @Test
    public void testTrovaPrimoSlotDisponibile_WithDurataPrestazioneQuattroOre() {

        DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
        List<Visita> listaVisiteTest = visitaService.getAllVisiteStartingFromNow();

        // Mi invento alcune prestazione con durata lunghissima
        // così facendo riesco a "spostarmi" di più tra un giorno lavorativo e il successivo
//        List<Prestazione> listaPrestazioniTest = PianificazioneManagerTest.getListaPrestazioniTest();
        List<Prestazione> listaPrestazioniTest = datiPrestazioniTest.getDatiTest();


        Prestazione prestazioneLunga = listaPrestazioniTest.get( 1 ); // 4 ore



        // TODO: Mi domando: Quando schedulo una visita avente prestazione con durata n ?
        //  ===> Me lo deve dire il metodo trovaPrimoSlotDisponibile()

        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile (
                        prestazioneLunga.getDurataMedia(),
                        new Date(),
                        LocalTime.now(),
                        getAllDatiMediciTests(),
                        getAllVisiteByData()
                );

        slotDisponibile.ifPresent( sd -> {
            System.out.println("\n");
            System.out.println( "**** SLOT TROVATO:" + sd + " ****" );
            System.out.println("\n");
        } );

        System.out.println("\n");

        System.out.println("VISITE COMPRESE TRA ADESSO E LA DATA DELL'ULTIMA VISITA:\n");
        visitaService.getAllVisiteStartingFromNow().forEach(System.out::println);

        System.out.println("\n");

    }

 */


}