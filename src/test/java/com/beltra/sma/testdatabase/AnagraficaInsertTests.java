package com.beltra.sma.testdatabase;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.repository.AnagraficaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.mock;


public class AnagraficaInsertTests {


    private AnagraficaRepository anagraficaRepository;

    @BeforeEach
    void init()  {
        anagraficaRepository = mock(AnagraficaRepository.class);
    }

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
