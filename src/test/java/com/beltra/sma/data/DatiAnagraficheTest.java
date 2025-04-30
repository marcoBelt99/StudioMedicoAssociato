package com.beltra.sma.data;

import com.beltra.sma.model.Anagrafica;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DatiAnagraficheTest {

    public List<Anagrafica> getDatiTest() {

        Anagrafica anagrafica1 = new Anagrafica();
        anagrafica1.setIdAnagrafica(1L);
        anagrafica1.setCognome("Forza");
        anagrafica1.setNome("Mattia");
        anagrafica1.setDataNascita( new GregorianCalendar(1999, Calendar.FEBRUARY,8).getTime() );
        anagrafica1.setGenere("M");

        Anagrafica anagrafica2 = new Anagrafica();
        anagrafica2.setIdAnagrafica(2L);
        anagrafica2.setCognome("Lanza");
        anagrafica2.setNome("Eliana");
        anagrafica2.setDataNascita( new GregorianCalendar(1999, Calendar.MARCH,15).getTime() );
        anagrafica2.setGenere("F");

        Anagrafica anagrafica3 = new Anagrafica();
        anagrafica3.setIdAnagrafica(3L);
        anagrafica3.setCognome("Berti");
        anagrafica3.setNome("Federico");
        anagrafica3.setDataNascita( new GregorianCalendar(1999, Calendar.DECEMBER,10).getTime() );
        anagrafica3.setGenere("M");

        return Arrays.asList(anagrafica1, anagrafica2, anagrafica3);
    }

}
