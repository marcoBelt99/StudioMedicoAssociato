package com.beltra.sma.repository;

import com.beltra.sma.model.Utente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
public class UtenteInsertTest {

    @Autowired
    private AnagraficaRepository anagraficaRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    @Disabled
    @Test
    public void insertNewUtente() {
        Utente utente = new Utente();
        AtomicLong id = new AtomicLong();
        utente.setIdUtente("UT000"+ id.addAndGet(anagraficaRepository.count()) );
        utente.setUsername("pippo");
        utente.setPassword("pluto");
        utente.setAttivo(true);

        Utente savedUtente = utenteRepository.save(utente);

        Assertions.assertNotNull(savedUtente);
    }
}