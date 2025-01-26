package com.beltra.sma.testdatabase;

import com.beltra.sma.model.*;
import com.beltra.sma.repository.AnagraficaRepository;
import com.beltra.sma.repository.InfermiereRepository;
import com.beltra.sma.repository.RuoloRepository;
import com.beltra.sma.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SpringBootTest
public class CreateInfermiereTests {

    @Autowired
    private AnagraficaRepository anagraficaRepository;

    @Autowired
    private RuoloRepository ruoloRepository;

    @Autowired
    private InfermiereRepository infermiereRepository;

    @Autowired
    private UtenteRepository utenteRepository;


    @Test
    @Transactional // TODO: OBBLIGATORIO USO DI TRANSACTIONAL
    @Commit // per poter effettivamente inserire realmente sul DB
    public void testInsertInfermiere() { // di base --> specializzazione=null


        // 1. Creare l'anagrafica
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setCognome("Piazza");
        anagrafica.setNome("Joemy");
        anagrafica.setDataNascita( new GregorianCalendar(1960, Calendar.JULY, 20).getTime() );
        anagrafica.setGenere("F");

        anagrafica = anagraficaRepository.save(anagrafica);

        // 2. Creare l'utente
        Long nuovoIndiceNumerico = utenteRepository.count()+1;
        Utente utente = new Utente();
        utente.setIdUtente("UT000"+nuovoIndiceNumerico);
        utente.setUsername("jopiaz");
        utente.setPassword("123yoyo");
        utente.setAttivo(true);
        utente.setAnagrafica( anagrafica );

        utente = utenteRepository.save( utente );

        // 3. Creare il ruolo
        Ruolo ruolo = new Ruolo();
        ruolo.setTipo("INFERMIERE");
        ruolo.setUtente( utente );

        ruolo = ruoloRepository.save( ruolo );

        // 4. Creare l'infermiere
        nuovoIndiceNumerico = infermiereRepository.count()+1;
        Infermiere infermiere = new Infermiere();
        infermiere.setAnagrafica( anagrafica );
        infermiere.setNumCartellino("INF000"+nuovoIndiceNumerico);
        infermiere.setTipologia("Strumentale"); // Creazione nuovo medico di base

        infermiere = infermiereRepository.save( infermiere );

        Assertions.assertNotNull( anagrafica );
        Assertions.assertNotNull( utente );
        Assertions.assertNotNull( ruolo );
        Assertions.assertNotNull( infermiere );
    }
}
