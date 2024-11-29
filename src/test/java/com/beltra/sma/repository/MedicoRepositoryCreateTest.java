package com.beltra.sma.repository;

import com.beltra.sma.model.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SpringBootTest
public class MedicoRepositoryCreateTest {

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
    public void testInsertMedico_di_Base() { // di base --> specializzazione=null


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

        medicoRepository.save(medico);

        Assertions.assertNotNull( anagrafica );
        Assertions.assertNotNull( utente );
        Assertions.assertNotNull( ruolo );
        Assertions.assertNotNull( medico );
    }


}
