package com.beltra.sma.components.data;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/** Classe in cui sono presenti tutti i dati di test (variabili) utili per questo package */
public interface DatiTest<T> {

    List<T> getDatiTest();

    Date dataTest_16Gennaio2025 = new GregorianCalendar(2025, Calendar.JANUARY, 16).getTime();
    Date dataAttualeDiTest = new GregorianCalendar(2025, Calendar.JANUARY, 17 ).getTime();

}
