package com.beltra.sma.testdatabase;

import com.beltra.sma.model.Utente;
import com.beltra.sma.repository.AnagraficaRepository;
import com.beltra.sma.repository.UtenteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.Mockito.mock;

//@SpringBootTest
public class UtenteInsertTests {

    //@Autowired
    private AnagraficaRepository anagraficaRepository;
   // @Autowired
    private UtenteRepository utenteRepository;

    @BeforeEach
    void init()  {
        anagraficaRepository = mock(AnagraficaRepository.class);
    }


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
