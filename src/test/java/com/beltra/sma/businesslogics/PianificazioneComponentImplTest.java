package com.beltra.sma.businesslogics;

import com.beltra.sma.model.Prenotazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.repository.*;
import com.beltra.sma.components.PianificazioneComponentImpl;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class PianificazioneComponentImplTest {


    @Autowired
    private PianificazioneComponentImpl pianificazioneService;

    @Autowired
    private VisitaRepository visitaRepository;


    @Autowired
    private PrestazioneRepository prestazioneRepository;



    @Autowired
    private AnagraficaRepository anagraficaRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;


    private Date dataTest;
    private List<Visita> visiteTest;
    private List<Prenotazione> prenotazioniTest;


    @BeforeEach
    void setup() {

        // Configura una data di test unica
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 16); // 16 gennaio 2025
        dataTest = cal.getTime();


        // Liste per tenere traccia delle entità di test create
        visiteTest = new ArrayList<>();
        prenotazioniTest = new ArrayList<>();

        // Crea una visita di test
        Visita visita1 = new Visita();
        visita1.setAnagrafica(anagraficaRepository.findById(1L).get()); // Medico Mario Rossi
        visita1.setDataVisita(dataTest);
        visita1.setOra(Time.valueOf("08:30:00")); // Visita alle 08:30
        visita1.setPrestazione(prestazioneRepository.findById(2L).get()); // prestazione 2
        visita1.setNumAmbulatorio(4);
        visita1 = visitaRepository.save(visita1);
        visiteTest.add(visita1);

        // Creo un'altra visita
        Visita visita2 = new Visita();
        visita2.setAnagrafica(anagraficaRepository.findById(3L).get()); // Medico Geraldo Mori
        visita2.setDataVisita(dataTest);
        visita2.setOra(Time.valueOf("09:30:00")); // Visita alle 08:90
        visita2.setPrestazione(prestazioneRepository.findById(3L).get()); // prestazione 3
        visita2.setNumAmbulatorio(3);
        visita2 = visitaRepository.save(visita2);
        visiteTest.add(visita1);

        // Crea la prenotazione di test relativa a visita1
        Prenotazione prenotazione1 = new Prenotazione();
        prenotazione1.setAnagrafica(anagraficaRepository.findById(5L).get()); // Paziente Marco Beltrame
        prenotazione1.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
        prenotazione1.setEffettuata(false);
        prenotazione1.setVisita(visita1); // Associa la prenotazione alla visita
        prenotazione1 = prenotazioneRepository.save(prenotazione1);
        prenotazioniTest.add(prenotazione1);

        // Crea la prenotazione di test relativa a visita2
        Prenotazione prenotazione2 = new Prenotazione();
        prenotazione2.setAnagrafica(anagraficaRepository.findById(7L).get()); // Paziente Lina Marchesini
        prenotazione2.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
        prenotazione2.setEffettuata(false);
        prenotazione2.setVisita(visita1); // Associa la prenotazione alla visita
        prenotazione2 = prenotazioneRepository.save(prenotazione2);
        prenotazioniTest.add(prenotazione2);


    }

    @AfterEach
    void cleanup() {
        // Rimuove le visite create durante il test
        visitaRepository.deleteAll(visiteTest);

        // Rimuove le prenotazioni create durante il test
        prenotazioneRepository.deleteAll(prenotazioniTest);
    }

    @Test
    public void testTrovaPrimoSlotDisponibile_Ok() {

        // Durata della visita richiesta
        double durataVisita = 15.0; // 15 minuti

        // Chiama il metodo da testare
        Optional<SlotDisponibile> slotDisponibile = pianificazioneService.trovaPrimoSlotDisponibile( durataVisita, dataTest );

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




    @Test
    public void testTrovaPrimoSlotDisponibile_NoMedicoDisponibile() {

        // Arrange
        // Configura una data di test unica
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 16); // 16 gennaio 2025
        dataTest = cal.getTime();


        // Liste per tenere traccia delle entità di test create
        visiteTest = new ArrayList<>();
        prenotazioniTest = new ArrayList<>();


        // Crea una visita di test
        Visita visita1 = new Visita();
        visita1.setAnagrafica(anagraficaRepository.findById(1L).get()); // Medico Mario Rossi
        visita1.setDataVisita(dataTest);
        visita1.setOra(Time.valueOf("08:30:00")); // Visita alle 08:30
        visita1.setPrestazione(prestazioneRepository.findById(2L).get()); // prestazione 2
        visita1.setNumAmbulatorio(4);
        visita1 = visitaRepository.save(visita1);
        visiteTest.add(visita1);

        // Creo un'altra visita
        Visita visita2 = new Visita();
        visita2.setAnagrafica(anagraficaRepository.findById(3L).get()); // Medico Geraldo Mori
        visita2.setDataVisita(dataTest);
        visita2.setOra(Time.valueOf("09:30:00")); // Visita alle 08:90
        visita2.setPrestazione(prestazioneRepository.findById(3L).get()); // prestazione 3
        visita2.setNumAmbulatorio(3);
        visita2 = visitaRepository.save(visita2);
        visiteTest.add(visita1);

        // Crea la prenotazione di test relativa a visita1
        Prenotazione prenotazione1 = new Prenotazione();
        prenotazione1.setAnagrafica(anagraficaRepository.findById(5L).get()); // Paziente Marco Beltrame
        prenotazione1.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
        prenotazione1.setEffettuata(false);
        prenotazione1.setVisita(visita1); // Associa la prenotazione alla visita
        prenotazione1 = prenotazioneRepository.save(prenotazione1);
        prenotazioniTest.add(prenotazione1);

        // Crea la prenotazione di test relativa a visita2
        Prenotazione prenotazione2 = new Prenotazione();
        prenotazione2.setAnagrafica(anagraficaRepository.findById(7L).get()); // Paziente Lina Marchesini
        prenotazione2.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
        prenotazione2.setEffettuata(false);
        prenotazione2.setVisita(visita1); // Associa la prenotazione alla visita
        prenotazione2 = prenotazioneRepository.save(prenotazione2);
        prenotazioniTest.add(prenotazione2);


        // Crea una visita di test
        Visita visita3 = new Visita();
        visita3.setAnagrafica(anagraficaRepository.findById(2L).get()); // Medico Anna Bianchi
        visita3.setDataVisita(dataTest);
        visita3.setOra(Time.valueOf("10:30:00")); // Visita alle 10:30
        visita3.setPrestazione(prestazioneRepository.findById(2L).get()); // prestazione 2
        visita3.setNumAmbulatorio(4);
        visita3 = visitaRepository.save(visita3);
        visiteTest.add(visita3);

        // Creo un'altra visita
        Visita visita4 = new Visita();
        visita4.setAnagrafica(anagraficaRepository.findById(4L).get()); // Medico Luca Verdi
        visita4.setDataVisita(dataTest);
        visita4.setOra(Time.valueOf("11:30:00")); // Visita alle 11:30
        visita4.setPrestazione(prestazioneRepository.findById(3L).get()); // prestazione 3
        visita4.setNumAmbulatorio(3);
        visita4 = visitaRepository.save(visita4);
        visiteTest.add(visita4);

        // Crea la prenotazione di test relativa a visita1
        Prenotazione prenotazione3 = new Prenotazione();
        prenotazione3.setAnagrafica(anagraficaRepository.findById(6L).get()); // Paziente Stoppa Matteo
        prenotazione3.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
        prenotazione3.setEffettuata(false);
        prenotazione3.setVisita(visita3); // Associa la prenotazione alla visita
        prenotazione3 = prenotazioneRepository.save(prenotazione3);
        prenotazioniTest.add(prenotazione3);

        // Crea la prenotazione di test relativa a visita2
        Prenotazione prenotazione4 = new Prenotazione();
        prenotazione4.setAnagrafica(anagraficaRepository.findById(8L).get()); // Paziente Camisotti Aldo
        prenotazione4.setDataPrenotazione(Calendar.getInstance().getTime()); // Data di oggi
        prenotazione4.setEffettuata(false);
        prenotazione4.setVisita(visita4); // Associa la prenotazione alla visita
        prenotazione4 = prenotazioneRepository.save(prenotazione4);
        prenotazioniTest.add(prenotazione4);

        // Act
        // Durata della visita richiesta
        double durataVisita = 90.0; // 90 minuti

        // Chiama il metodo da testare
        Optional<SlotDisponibile> slotDisponibile = pianificazioneService.trovaPrimoSlotDisponibile( durataVisita, dataTest );



        // Assert
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

}
