package com.beltra.sma.testdatabase;

import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.model.*;
import com.beltra.sma.repository.PrestazioneRepository;
import com.beltra.sma.service.PrestazioneService;
import com.beltra.sma.service.UtenteService;
import com.beltra.sma.service.VisitaService;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;


import java.util.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/** TODO: Ciclo di vita TRANSAZIONALE di:
 *          1) check medico disponibile: non deve essere occupato
 *          2) creazione di una nuova visita
 *          3) scelta di un medico disponibile da assegnare --> check disponibilità medico
 *          4) creazione di una nuova prenotazione (con DataPrenotazione=DataAttuale),
 *          5)
 *
 *                          */


/** TODO: ricordati che molto probabilmente, tutti questi test presenti in questa classe li dovrai trasferire
 *   Nella classe VisitaServiceTest
 * */
@SpringBootTest
public class CreatePrenotazioneVisitaTests {

    // Vari Code Injection:
    @Autowired
    private UtenteService utenteService;

    @Autowired
    private PrestazioneService prestazioneService;

    @Autowired
    private VisitaService visitaService;


    @Autowired
    private PianificazioneComponent calcolatoreAmmissibilitaComponent;



    // Parametri di test:
    private String username = "marcobeltra";
    private Utente utente;

    List<Prestazione> prestazioniDisponibili = new ArrayList<Prestazione>();

    Date dataVisitaDesiderata = new GregorianCalendar().getTime();
    @Autowired
    private PrestazioneRepository prestazioneRepository;


    @BeforeEach
    public void setUp() {
        utente = utenteService.getUtenteByUsername( username );
        prestazioniDisponibili = prestazioneService.getAllPrestazioniDisponibili();
        dataVisitaDesiderata = new GregorianCalendar(2025, Calendar.FEBRUARY, 20).getTime();
    }




//    // TODO: TEST FINALE
//    @Disabled
//    @Test
//    @Transactional
//    @Commit
//    public void testCreateNewVisitaAndThenNewPrenotazione() {
//
//        // 1) Creazione di una nuova visita
//        Visita visita = new Visita();
//
//        String usernameUtente = "marcobeltra";
//        Utente utente = utenteService.getUtenteByUsername( usernameUtente );
//
//        Optional<Prestazione> prestazione = prestazioneRepository.findById(2L);
//        //  visita.setOra( );
//        // Data visita desiderata
//        // ...
//
//
//        /** Check disponibilita' di medici in quella data */
//
//        // 2) Verifico che ci siano medici liberi
//
//
//
//        assertEquals( Optional.empty(), visitaService.createVisita( usernameUtente , prestazione.get() ) );
//
//
//    }

}
