package com.beltra.sma.testdatabase;

import com.beltra.sma.model.*;
import com.beltra.sma.repository.AnagraficaRepository;
import com.beltra.sma.repository.MedicoRepository;
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
public class CreateMedicoTests {

    @Autowired
    private AnagraficaRepository anagraficaRepository;

    @Autowired
    private RuoloRepository ruoloRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UtenteRepository utenteRepository;


    @Test
    @Transactional // TODO: OBBLIGATORIO USO DI TRANSACTIONAL
    @Commit // per poter effettivamente inserire realmente sul DB
    public void testInsertMedicoDiBase() { // di base --> specializzazione=null


        // 1. Creare l'anagrafica
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setCognome("Danieli");
        anagrafica.setNome("Pino");
        anagrafica.setDataNascita( new GregorianCalendar(1960, Calendar.JULY, 20).getTime() );
        anagrafica.setGenere("M");

        anagrafica = anagraficaRepository.save(anagrafica);

        // 2. Creare l'utente
        Long nuovoIndiceNumerico = utenteRepository.count()+1;
        Utente utente = new Utente();
        utente.setIdUtente("UT000"+nuovoIndiceNumerico);
        utente.setUsername("pinoda");
        utente.setPassword("1234provetta");
        utente.setAttivo(true);
        utente.setAnagrafica(anagrafica);

        utente = utenteRepository.save(utente);

        // 3. Creare il ruolo
        Ruolo ruolo = new Ruolo();
        ruolo.setTipo("MEDICO");
        ruolo.setUtente( utente );

        ruolo = ruoloRepository.save(ruolo);

        // 4. Creare il medico
        nuovoIndiceNumerico = medicoRepository.count()+1;
        Medico medico = new Medico();
        medico.setAnagrafica( anagrafica );
        medico.setMatricola("MED000"+nuovoIndiceNumerico);
        medico.setSpecializzazione(null); // Creazione nuovo medico di base

        medico = medicoRepository.save(medico);

        Assertions.assertNotNull( anagrafica );
        Assertions.assertNotNull( utente );
        Assertions.assertNotNull( ruolo );
        Assertions.assertNotNull( medico );
    }


}
