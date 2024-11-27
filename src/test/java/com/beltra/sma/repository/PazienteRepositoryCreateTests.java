package com.beltra.sma.repository;

import com.beltra.sma.domain.Anagrafica;
import com.beltra.sma.domain.Paziente;
import com.beltra.sma.domain.Ruolo;
import com.beltra.sma.domain.Utente;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ActiveProfiles("test") // Dico a Spring di usare il profilo di configurazione di test per questa classe
@DataJpaTest // attivo il testing dei dati gestiti con Spring Data Jpa
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // uso un db fasullo in memory anzichè quello originale
public class PazienteRepositoryCreateTests {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private AnagraficaRepository anagraficaRepository;

    @Autowired
    private PazienteRepository pazienteRepository;

    @Autowired
    private RuoloRepository ruoloRepository;


    @BeforeEach
    void setup() {
        utenteRepository.deleteAll();
        ruoloRepository.deleteAll();
    }

    @Transactional
    @Test
    public void insertNewUtenteAndRuoloAndAnagraficaAndPaziente() {

        // 1. Creo l'Anagrafica
        // 2. Creo l'Utente
        // 3. Assegno all'utente l'anagrafica appena creata
        // 4. Creo il ruolo (in questo caso "PAZIENTE")
        // 5. Assegno al ruolo l'utente creato
        // 6. Creo il Paziente
        // 4. Assegno al paziente l'anagrafica



        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setCognome("Marongiu");
        anagrafica.setNome("Melissa");
        anagrafica.setDataNascita( new GregorianCalendar(1997, Calendar.OCTOBER, 20).getTime() );
        anagrafica.setGenere("F");

        anagraficaRepository.save( anagrafica );


        Utente utente = new Utente();
        utente.setIdUtente( "UT000" + (utenteRepository.count()+1) );
        utente.setUsername("melimaro");
        utente.setPassword("$2a$10$aeiIdL4eZYQviVIyjEzd6.DstgTRhUEJsZ4IMm7RGQt0p2x0dTpby"); // ilovepastaalforno99
        utente.setAttivo(true);
        utente.setIdAnagrafica( anagrafica );

        //utenteRepository.save( utente ); // necessario metterlo prima di fare ruolo.setIdUtente( utente ); !!!!!!

        Ruolo ruolo = new Ruolo();
        ruolo.setTipo("PAZIENTE");
        ruolo.setIdUtente( utente );
        ruoloRepository.save( ruolo );


        Paziente paziente = new Paziente();
        paziente.setCodiceFiscale("MRGMLS97N21U876P");
        paziente.setTelefono("3403192932");
        paziente.setEmail("melissamgiu@gmail.com");
        paziente.setResidenza("Via degli alpini 17, Adria (RO)");

        // TODO: Associazione bidirezionale tra anagrafica e paziente
        paziente.setAnagrafica( anagrafica );

        // TODO: Persistenza delle entità
        //anagraficaRepository.save( anagrafica );
        //utenteRepository.save( utente );  // in teoria basta questo save per salvare anche le altre due entita'
        //ruoloRepository.save( ruolo );
        pazienteRepository.save( paziente );

        // Verifica
        assertNotNull(  utenteRepository.findById( utente.getIdUtente() ).orElse(null) );
        assertNotNull(  anagraficaRepository.findById( anagrafica.getId().longValue() ).orElse(null) );
        assertNotNull(  pazienteRepository.findById( paziente.getId().longValue() ).orElse(null) );


        System.out.println("\nUTENTE:\n" + utente.toString());
        System.out.println("\n");
        System.out.println("\n\nRUOLO:\n" + ruolo.toString());
        System.out.println("\n");
        System.out.println("\n\nANAGRAFICA:\n" + anagrafica.toString());
        System.out.println("\n");
        System.out.println("\n\nPAZIENTE:\n" + paziente.toString());
        System.out.println("\n");

    }

//    }

}
