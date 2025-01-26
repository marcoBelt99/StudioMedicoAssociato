package com.beltra.sma.testdatabase;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Utente;
import com.beltra.sma.repository.AnagraficaRepository;
import com.beltra.sma.repository.UtenteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
public class AnagraficaUtenteInsertTests {

    @Autowired
    private AnagraficaRepository anagraficaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Disabled
    @Test
    public void insertNewAnagraficaAndThenNewUtente() {
        Anagrafica anagrafica = new Anagrafica();

        anagrafica.setCognome("Rosa");
        anagrafica.setNome("Maria");
        anagrafica.setDataNascita(new GregorianCalendar(1999, Calendar.FEBRUARY, 10).getTime());
        anagrafica.setGenere("F");

        Anagrafica savedAnagrafica = anagraficaRepository.save( anagrafica );
        Assertions.assertNotNull( savedAnagrafica );

        Utente utente = new Utente();
        AtomicLong id = new AtomicLong();
        utente.setIdUtente("UT000"+ id.addAndGet(anagraficaRepository.count()) );
        utente.setUsername("pippo");
        utente.setPassword("pluto");
        utente.setAttivo(true);

       //utente.addAnagrafica(anagrafica);

        Utente savedUtente = utenteRepository.save(utente);

        Assertions.assertNotNull(savedUtente);


    }

}
