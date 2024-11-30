package com.beltra.sma.repository;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Paziente;
import com.beltra.sma.model.Ruolo;
import com.beltra.sma.model.Utente;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SpringBootTest
public class CreatePazienteTest {

    @Autowired
    private AnagraficaRepository anagraficaRepository;

    @Autowired
    private RuoloRepository ruoloRepository;

    @Autowired
    private PazienteRepository pazienteRepository;

    @Autowired
    private UtenteRepository utenteRepository;


    @Test
    @Transactional // TODO: OBBLIGATORIO USO DI TRANSACTIONAL
    @Commit // per poter effettivamente inserire realmente sul DB
    public void testInsertPaziente() {


        // 1. Creare l'anagrafica
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setCognome("Veronese");
        anagrafica.setNome("Andrea");
        anagrafica.setDataNascita( new GregorianCalendar(1992, Calendar.JULY, 5).getTime() );
        anagrafica.setGenere("M");

        anagrafica = anagraficaRepository.save(anagrafica);

        // 2. Creare l'utente
        Long nuovoIndiceNumerico = utenteRepository.count()+1;
        Utente utente = new Utente();
        utente.setIdUtente("UT000"+nuovoIndiceNumerico);
        utente.setUsername("veroandry"); // Username uguale al codice fiscale
        utente.setPassword("123prova"); // Cambiare in un sistema reale
        utente.setAttivo(true);
        utente.setAnagrafica(anagrafica);

        utente = utenteRepository.save(utente);

        // 3. Creare il ruolo
        Ruolo ruolo = new Ruolo();
        ruolo.setTipo("PAZIENTE");
        ruolo.setUtente(utente);

        ruolo = ruoloRepository.save(ruolo);

        // 4. Creare il paziente
        Paziente paziente = new Paziente();
        paziente.setAnagrafica( anagrafica );
        paziente.setCodiceFiscale("VRSNDR92M05A059D");
        paziente.setTelefono("3411549635");
        paziente.setEmail("veroandry@example.com");
        paziente.setResidenza("Via delle Prove 12, Cavarzere (VE).");

        paziente = pazienteRepository.save(paziente);

        Assertions.assertNotNull(anagrafica);
        Assertions.assertNotNull(utente);
        Assertions.assertNotNull(ruolo);
        Assertions.assertNotNull(paziente);
    }


}
