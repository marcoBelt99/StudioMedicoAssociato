package com.beltra.sma.utils;

import java.time.LocalTime;

public class Parameters {

    /** Orari di apertura e chisura dello SMA */
    public static LocalTime orarioAperturaMattina =    LocalTime.of(7, 0);
    public static LocalTime orarioChiusuraMattina =    LocalTime.of(12, 0);
    public static LocalTime orarioAperturaPomeriggio = LocalTime.of(14, 0);
    public static LocalTime orarioChiusuraPomeriggio = LocalTime.of(21, 0);


    /** Tolleranza / Pausa in minuti tra una visita e l'altra. */
    public static long pausaFromvisite = 5;


}
