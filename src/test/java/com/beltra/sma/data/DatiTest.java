package com.beltra.sma.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/** Ospita vari dati di test (variabili) utili per questo package */
public interface DatiTest<T> {

    /** Data di test in cui ci sono dati dentro a DATABASE: ci dovrebbero essere 20 records. */
    Date dataTest_16Gennaio2025 = new GregorianCalendar(2025, Calendar.JANUARY, 16).getTime();

    /** Data in cui non ci sono dati dentro a DATABASE: (ci dovrebbero essere 0 records).<br>
     *  E' molto rappresentativa come data di test perche' e' venerdi'.  */
    Date dataAttualeDiTest = new GregorianCalendar(2025, Calendar.JANUARY, 17 ).getTime();

    List<T> getDatiTest();
}
