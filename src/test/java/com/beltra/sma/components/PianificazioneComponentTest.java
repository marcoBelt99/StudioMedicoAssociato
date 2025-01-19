package com.beltra.sma.components;

import com.beltra.sma.model.*;
import com.beltra.sma.service.MedicoService;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class PianificazioneComponentTest {


    @Autowired
    private PianificazioneComponent pianificazioneComponent;
    @Autowired
    private VisitaService visitaService;

    @Autowired
    private MedicoService medicoService;


    private Date dataTest;
    private List<Visita> visiteTest;
    private List<Prenotazione> prenotazioniTest;





//    @BeforeEach
//    void setup() {
//
//        // Configura una data di test unica
//        Calendar cal = Calendar.getInstance();
//        cal.set(2025, Calendar.JANUARY, 16); // 16 gennaio 2025
//        dataTest = cal.getTime();
//
//
//        // Liste per tenere traccia delle entità di test create
//        visiteTest = new ArrayList<>();
//        prenotazioniTest = new ArrayList<>();
//
//        // Crea una visita di test
//        Visita visita1 = new Visita();
//        visita1.setAnagrafica(anagraficaRepository.findById(1L).get()); // Medico Mario Rossi
//        visita1.setDataVisita(dataTest);
//        visita1.setOra(Time.valueOf("08:30:00")); // Visita alle 08:30
//        visita1.setPrestazione(prestazioneRepository.findById(2L).get()); // prestazione 2
//        visita1.setNumAmbulatorio(4);
//        visita1 = visitaService.salvaVisita(visita1);
//        visiteTest.add(visita1);
//
//        // Creo un'altra visita
//        Visita visita2 = new Visita();
//        visita2.setAnagrafica(anagraficaRepository.findById(3L).get()); // Medico Geraldo Mori
//        visita2.setDataVisita(dataTest);
//        visita2.setOra(Time.valueOf("09:30:00")); // Visita alle 08:90
//        visita2.setPrestazione(prestazioneRepository.findById(3L).get()); // prestazione 3
//        visita2.setNumAmbulatorio(3);
//        visita2 = visitaService.salvaVisita(visita2);
//        visiteTest.add(visita1);
//
//        // Crea la prenotazione di test relativa a visita1
//        Prenotazione prenotazione1 = new Prenotazione();
//        prenotazione1.setAnagrafica(anagraficaRepository.findById(5L).get()); // Paziente Marco Beltrame
//        prenotazione1.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
//        prenotazione1.setEffettuata(false);
//        prenotazione1.setVisita(visita1); // Associa la prenotazione alla visita
//        prenotazione1 = prenotazioneRepository.save(prenotazione1);
//        prenotazioniTest.add(prenotazione1);
//
//        // Crea la prenotazione di test relativa a visita2
//        Prenotazione prenotazione2 = new Prenotazione();
//        prenotazione2.setAnagrafica(anagraficaRepository.findById(7L).get()); // Paziente Lina Marchesini
//        prenotazione2.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
//        prenotazione2.setEffettuata(false);
//        prenotazione2.setVisita(visita1); // Associa la prenotazione alla visita
//        prenotazione2 = prenotazioneRepository.save(prenotazione2);
//        prenotazioniTest.add(prenotazione2);
//
//
//    }
//
//    @AfterEach
//    void cleanup() {
//
//        // Rimuove le visite create durante il test
//        visitaService.getVisitaRepository().deleteAll(visiteTest);
//
//        // Rimuove le prenotazioni create durante il test
//        prenotazioneRepository.deleteAll(prenotazioniTest);
//    }


    /** In questo metodo inserisco i dati di test per i vari casi di test specifici. */
    public List<Visita> getAllVisiteByData() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Data attuale
        Date dataCorrente = calendar.getTime();
        return visitaService.getAllVisiteByData(dataCorrente);
    }

    @Test
    public void testTrovaPrimoSlotDisponibile_Ok() {

        // Durata della visita richiesta
        double durataVisita = 15.0; // 15 minuti

        // Chiama il metodo da testare
        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile( durataVisita, getAllVisiteByData() );

        // Verifica che lo slot sia presente e sia corretto
        assertTrue( slotDisponibile.isPresent(), "Nessuno slot disponibile trovato");
        assertEquals( Time.valueOf("07:00:00"), slotDisponibile.get().getOrario(), "L'orario dello slot non è corretto");
        assertEquals( dataTest, slotDisponibile.get().getData(), "La data dello slot non è corretta");
        assertNotNull( slotDisponibile.get().getMedico(), "Nessun medico assegnato allo slot");

        System.out.println("");
        System.out.println( "Medico da assegnare:\tData:\tOrario:\n"
                + slotDisponibile.get().getMedico().getAnagrafica().getCognome() +"\t"
                + slotDisponibile.get().getData() +"\t"
                + slotDisponibile.get().getOrario()
        );
    }




//    @Test
//    public void testTrovaPrimoSlotDisponibile_NoMedicoDisponibile() {
//
//        // Arrange
//        // Configura una data di test unica
//        Calendar cal = Calendar.getInstance();
//        cal.set(2025, Calendar.JANUARY, 16); // 16 gennaio 2025
//        dataTest = cal.getTime();
//
//
//        // Liste per tenere traccia delle entità di test create
//        visiteTest = new ArrayList<>();
//        prenotazioniTest = new ArrayList<>();
//
//
//        // Crea una visita di test
//        Visita visita1 = new Visita();
//        visita1.setAnagrafica(anagraficaRepository.findById(1L).get()); // Medico Mario Rossi
//        visita1.setDataVisita(dataTest);
//        visita1.setOra(Time.valueOf("08:30:00")); // Visita alle 08:30
//        visita1.setPrestazione(prestazioneRepository.findById(2L).get()); // prestazione 2
//        visita1.setNumAmbulatorio(4);
//        visita1 = visitaService.getVisitaRepository().save(visita1);
//        visiteTest.add(visita1);
//
//        // Creo un'altra visita
//        Visita visita2 = new Visita();
//        visita2.setAnagrafica(anagraficaRepository.findById(3L).get()); // Medico Geraldo Mori
//        visita2.setDataVisita(dataTest);
//        visita2.setOra(Time.valueOf("09:30:00")); // Visita alle 08:90
//        visita2.setPrestazione(prestazioneRepository.findById(3L).get()); // prestazione 3
//        visita2.setNumAmbulatorio(3);
//        visita2 = visitaService.getVisitaRepository().save(visita2);
//        visiteTest.add(visita1);
//
//        // Crea la prenotazione di test relativa a visita1
//        Prenotazione prenotazione1 = new Prenotazione();
//        prenotazione1.setAnagrafica(anagraficaRepository.findById(5L).get()); // Paziente Marco Beltrame
//        prenotazione1.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
//        prenotazione1.setEffettuata(false);
//        prenotazione1.setVisita(visita1); // Associa la prenotazione alla visita
//        prenotazione1 = prenotazioneRepository.save(prenotazione1);
//        prenotazioniTest.add(prenotazione1);
//
//        // Crea la prenotazione di test relativa a visita2
//        Prenotazione prenotazione2 = new Prenotazione();
//        prenotazione2.setAnagrafica(anagraficaRepository.findById(7L).get()); // Paziente Lina Marchesini
//        prenotazione2.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
//        prenotazione2.setEffettuata(false);
//        prenotazione2.setVisita(visita1); // Associa la prenotazione alla visita
//        prenotazione2 = prenotazioneRepository.save(prenotazione2);
//        prenotazioniTest.add(prenotazione2);
//
//
//        // Crea una visita di test
//        Visita visita3 = new Visita();
//        visita3.setAnagrafica(anagraficaRepository.findById(2L).get()); // Medico Anna Bianchi
//        visita3.setDataVisita(dataTest);
//        visita3.setOra(Time.valueOf("10:30:00")); // Visita alle 10:30
//        visita3.setPrestazione(prestazioneRepository.findById(2L).get()); // prestazione 2
//        visita3.setNumAmbulatorio(4);
//        visita3 = visitaService.getVisitaRepository().save(visita3);
//        visiteTest.add(visita3);
//
//        // Creo un'altra visita
//        Visita visita4 = new Visita();
//        visita4.setAnagrafica(anagraficaRepository.findById(4L).get()); // Medico Luca Verdi
//        visita4.setDataVisita(dataTest);
//        visita4.setOra(Time.valueOf("11:30:00")); // Visita alle 11:30
//        visita4.setPrestazione(prestazioneRepository.findById(3L).get()); // prestazione 3
//        visita4.setNumAmbulatorio(3);
//        visita4 = visitaService.getVisitaRepository().save(visita4);
//        visiteTest.add(visita4);
//
//        // Crea la prenotazione di test relativa a visita1
//        Prenotazione prenotazione3 = new Prenotazione();
//        prenotazione3.setAnagrafica(anagraficaRepository.findById(6L).get()); // Paziente Stoppa Matteo
//        prenotazione3.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
//        prenotazione3.setEffettuata(false);
//        prenotazione3.setVisita(visita3); // Associa la prenotazione alla visita
//        prenotazione3 = prenotazioneRepository.save(prenotazione3);
//        prenotazioniTest.add(prenotazione3);
//
//        // Crea la prenotazione di test relativa a visita2
//        Prenotazione prenotazione4 = new Prenotazione();
//        prenotazione4.setAnagrafica(anagraficaRepository.findById(8L).get()); // Paziente Camisotti Aldo
//        prenotazione4.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
//        prenotazione4.setEffettuata(false);
//        prenotazione4.setVisita(visita4); // Associa la prenotazione alla visita
//        prenotazione4 = prenotazioneRepository.save(prenotazione4);
//        prenotazioniTest.add(prenotazione4);
//
//        // Act
//        // Durata della visita richiesta
//        double durataVisita = 90.0; // 90 minuti
//
//        // Chiama il metodo da testare
//        Optional<SlotDisponibile> slotDisponibile =
//                pianificazioneComponent.trovaPrimoSlotDisponibile( durataVisita, getAllVisiteByData() );
//
//
//
//        // Assert
//        // Verifica che lo slot sia presente e sia corretto
//        assertTrue( slotDisponibile.isPresent(), "Nessuno slot disponibile trovato");
//        assertEquals( Time.valueOf("07:00:00"), slotDisponibile.get().getOrario(), "L'orario dello slot non è corretto");
//        assertEquals( dataTest, slotDisponibile.get().getData(), "La data dello slot non è corretta");
//        assertNotNull( slotDisponibile.get().getMedico(), "Nessun medico assegnato allo slot");
//
//        System.out.println("");
//        System.out.println( "Medico da assegnare:\tData:\tOrario:\n"
//                + slotDisponibile.get().getMedico().getAnagrafica().getCognome() +"\t"
//                + slotDisponibile.get().getData() +"\t"
//                + slotDisponibile.get().getOrario()
//        );
//    }



    @Test
    public void testTrovaPrimoSlotDisponibile_WithDurataPrestazioneQuindiciMinuti() {

        List<Visita> listaVisiteTest = visitaService.getAllVisiteStartingFromNow();

        List<Medico> listaDiTuttiMediciDelSistema = medicoService.getAllMedici();

        // Mi invento alcune prestazione con durata lunghissima
        // così facendo riesco a "spostarmi" di più tra un giorno lavorativo e il successivo
        List<Prestazione> listaPrestazioniTest = PianificazioneComponentDataTest.getListaPrestazioniTest();

        Prestazione prestazioneBreve = listaPrestazioniTest.get( 4); // 15 min


        // TODO: Mi domando: Quando schedulo una visita avente prestazione con durata n ?
        //  ===> Me lo deve dire il metodo trovaPrimoSlotDisponibile()

        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile (
                        prestazioneBreve.getDurataMedia(),
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

        List<Visita> listaVisiteTest = visitaService.getAllVisiteStartingFromNow();

        List<Medico> listaDiTuttiMediciDelSistema = medicoService.getAllMedici();

        // Mi invento alcune prestazione con durata lunghissima
        // così facendo riesco a "spostarmi" di più tra un giorno lavorativo e il successivo
        List<Prestazione> listaPrestazioniTest = PianificazioneComponentDataTest.getListaPrestazioniTest();


        Prestazione prestazione2Ore = listaPrestazioniTest.get( 5); // 2 ore



        // TODO: Mi domando: Quando schedulo una visita avente prestazione con durata n ?
        //  ===> Me lo deve dire il metodo trovaPrimoSlotDisponibile()

        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile (
                        prestazione2Ore.getDurataMedia(),
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

        List<Visita> listaVisiteTest = visitaService.getAllVisiteStartingFromNow();

        List<Medico> listaDiTuttiMediciDelSistema = medicoService.getAllMedici();

        // Mi invento alcune prestazione con durata lunghissima
        // così facendo riesco a "spostarmi" di più tra un giorno lavorativo e il successivo
        List<Prestazione> listaPrestazioniTest = PianificazioneComponentDataTest.getListaPrestazioniTest();


        Prestazione prestazioneLunga = listaPrestazioniTest.get( 1 ); // 4 ore



        // TODO: Mi domando: Quando schedulo una visita avente prestazione con durata n ?
        //  ===> Me lo deve dire il metodo trovaPrimoSlotDisponibile()

        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile (
                        prestazioneLunga.getDurataMedia(),
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




}