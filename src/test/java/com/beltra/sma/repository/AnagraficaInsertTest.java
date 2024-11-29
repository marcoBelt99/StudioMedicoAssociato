package com.beltra.sma.repository;

import com.beltra.sma.model.Anagrafica;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SpringBootTest
public class AnagraficaInsertTest {

    @Autowired
    private AnagraficaRepository anagraficaRepository;

    @Disabled
    @Test
    public void insertNewAnagrafica() {
        Anagrafica anagrafica = new Anagrafica();

        anagrafica.setCognome("Rosa");
        anagrafica.setNome("Maria");
        anagrafica.setDataNascita(new GregorianCalendar(1999, Calendar.FEBRUARY, 10).getTime());
        anagrafica.setGenere("F");

        Anagrafica savedAnagrafica = anagraficaRepository.save( anagrafica );
        Assertions.assertNotNull( savedAnagrafica );

    }
}
