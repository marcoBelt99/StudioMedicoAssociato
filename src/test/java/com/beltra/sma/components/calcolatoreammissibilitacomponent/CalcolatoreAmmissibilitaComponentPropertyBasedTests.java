package com.beltra.sma.components.calcolatoreammissibilitacomponent;

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl;

import com.beltra.sma.utils.Parameters;
import net.jqwik.api.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;



public class CalcolatoreAmmissibilitaComponentPropertyBasedTests {

    CalcolatoreAmmissibilitaComponent calcolatore;

    @Property
    boolean giorniFerialiSonoAmmissibili(@ForAll("validDates") LocalDate date) {
        calcolatore = new CalcolatoreAmmissibilitaComponentImpl();
        Date legacyDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return calcolatore.isGiornoAmmissibile(legacyDate);
    }


    @Property
    boolean orariInFasciAperturaSonoSoddisfacibili(@ForAll("orariInFasciaApertura") LocalTime orario) {
        calcolatore = new CalcolatoreAmmissibilitaComponentImpl();
        return calcolatore.condizioneSoddisfacibilita(orario);
    }

    @Property
    boolean orariInFasciaChiusuraNonSonoSoddisfacibili(@ForAll("orariInFasciaChiusura") LocalTime orario) {
        calcolatore = new CalcolatoreAmmissibilitaComponentImpl();
        return !calcolatore.condizioneSoddisfacibilita(orario);
    }


    @Property
    boolean orariEstremiAperturaSonoSoddisfacibili(@ForAll("orariEstremiApertura") LocalTime orario) {
        calcolatore = new CalcolatoreAmmissibilitaComponentImpl();
        return calcolatore.condizioneSoddisfacibilita(orario);
    }

    @Property
    boolean orariEstremiChiusuraNonSonoSoddisfacibili(@ForAll("orariEstremiChiusura") LocalTime orario) {
        calcolatore = new CalcolatoreAmmissibilitaComponentImpl();
        return !calcolatore.condizioneSoddisfacibilita(orario);
    }

    // Proprietà di simmetria: se un orario è soddisfacibile,
    // dovrebbe essere nella fascia mattutina O pomeridiana
    @Property
    boolean orariSoddisfacibiliSonoInUnaDelleDueFasce(@ForAll("orariGenerici") LocalTime orario) {
        calcolatore = new CalcolatoreAmmissibilitaComponentImpl();
        boolean isSoddisfacibile = calcolatore.condizioneSoddisfacibilita(orario);

        boolean isInFasciaMattina = !orario.isBefore(Parameters.orarioAperturaMattina) &&
                !orario.isAfter(Parameters.orarioChiusuraMattina);
        boolean isInFasciaPomeriggio = !orario.isBefore(Parameters.orarioAperturaPomeriggio) &&
                !orario.isAfter(Parameters.orarioChiusuraPomeriggio);

        // Se è soddisfacibile, deve essere in una delle due fasce
        return !isSoddisfacibile || (isInFasciaMattina || isInFasciaPomeriggio);
    }


/// TODO: non ho capito bene cosa faccia questo metodo
// Test di monotonia: se un orario è soddisfacibile e ne aggiungiamo uno prima e uno dopo
// nell'intervallo di apertura, anche quelli dovrebbero essere soddisfacibili
    @Property
    boolean monotonicityInAperturaIntervals(@ForAll("orariInFasciaApertura") LocalTime orarioBase) {
        calcolatore = new CalcolatoreAmmissibilitaComponentImpl();

        // Assumiamo che l'orario base sia soddisfacibile
        Assume.that(calcolatore.condizioneSoddisfacibilita(orarioBase));

        // Testiamo orari vicini (±5 minuti) se rimangono nella fascia
        LocalTime orarioPrecedente = orarioBase.minusMinutes(5);
        LocalTime orarioSuccessivo = orarioBase.plusMinutes(5);

        boolean precedenteValido = true;
        boolean successivoValido = true;

        // Verifica se l'orario precedente è ancora in fascia apertura
        if (isInFasciaApertura(orarioPrecedente)) {
            precedenteValido = calcolatore.condizioneSoddisfacibilita(orarioPrecedente);
        }

        // Verifica se l'orario successivo è ancora in fascia apertura
        if (isInFasciaApertura(orarioSuccessivo)) {
            successivoValido = calcolatore.condizioneSoddisfacibilita(orarioSuccessivo);
        }

        return precedenteValido && successivoValido;
    }

    // Metodo helper per verificare se un orario è in fascia apertura
    private boolean isInFasciaApertura(LocalTime orario) {
        boolean isInFasciaMattina = !orario.isBefore(Parameters.orarioAperturaMattina) &&
                !orario.isAfter(Parameters.orarioChiusuraMattina);
        boolean isInFasciaPomeriggio = !orario.isBefore(Parameters.orarioAperturaPomeriggio) &&
                !orario.isAfter(Parameters.orarioChiusuraPomeriggio);
        return isInFasciaMattina || isInFasciaPomeriggio;
    }






    // #################################################
    // #################################################
    // #################################################
    // #################################################
    // #################################################
    // #################################################
    // #################################################
    @Provide
    Arbitrary<LocalDate> validDates() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        long startEpochDay = start.toEpochDay();
        long endEpochDay = end.toEpochDay();

        return Arbitraries.longs()
                .between(startEpochDay, endEpochDay)
                .map(LocalDate::ofEpochDay)
                .filter(d -> {
                    DayOfWeek day = d.getDayOfWeek();
                    return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
                });
    }




    // ===================== PROVIDER METHODS =====================

    @Provide
    Arbitrary<LocalTime> orariInFasciaApertura() {
        Arbitrary<LocalTime> orariMattina = createLocalTimesBetween(
                Parameters.orarioAperturaMattina, Parameters.orarioChiusuraMattina);

        Arbitrary<LocalTime> orariPomeriggio = createLocalTimesBetween(
                Parameters.orarioAperturaPomeriggio, Parameters.orarioChiusuraPomeriggio);

        return Arbitraries.oneOf(orariMattina, orariPomeriggio);
    }

    @Provide
    Arbitrary<LocalTime> orariInFasciaChiusura() {
        // Orari tra chiusura mattina e apertura pomeriggio
        Arbitrary<LocalTime> orariPausaPranzo = createLocalTimesBetween(
                Parameters.orarioChiusuraMattina.plusMinutes(1),
                Parameters.orarioAperturaPomeriggio.minusMinutes(1));

        // Orari prima dell'apertura mattutina
        Arbitrary<LocalTime> orariPrimaApertura = createLocalTimesBetween(
                LocalTime.MIDNIGHT, Parameters.orarioAperturaMattina.minusMinutes(1));

        // Orari dopo la chiusura pomeridiana
        Arbitrary<LocalTime> orariDopoChiusura = createLocalTimesBetween(
                Parameters.orarioChiusuraPomeriggio.plusMinutes(1), LocalTime.MAX);

        return Arbitraries.oneOf(orariPausaPranzo, orariPrimaApertura, orariDopoChiusura);
    }

    @Provide
    Arbitrary<LocalTime> orariEstremiApertura() {
        return Arbitraries.of(
                Parameters.orarioAperturaMattina,
                Parameters.orarioChiusuraMattina,
                Parameters.orarioAperturaPomeriggio,
                Parameters.orarioChiusuraPomeriggio
        );
    }

    @Provide
    Arbitrary<LocalTime> orariEstremiChiusura() {
        return Arbitraries.of(
                Parameters.orarioChiusuraMattina.plusMinutes(1),
                Parameters.orarioAperturaPomeriggio.minusMinutes(1),
                Parameters.orarioAperturaMattina.minusMinutes(1),
                Parameters.orarioChiusuraPomeriggio.plusMinutes(1)
        ).filter(orario -> orario.isAfter(LocalTime.MIDNIGHT) && orario.isBefore(LocalTime.MAX));
    }

    @Provide
    Arbitrary<LocalTime> orariGenerici() {
        return createLocalTimesBetween(LocalTime.MIDNIGHT, LocalTime.MAX);
    }

    // Metodo helper per creare LocalTime arbitrari tra due orari
    private Arbitrary<LocalTime> createLocalTimesBetween(LocalTime start, LocalTime end) {
        int startSeconds = start.toSecondOfDay();
        int endSeconds = end.toSecondOfDay();

        // Gestione caso speciale: se end è prima di start (attraversa la mezzanotte)
        if (endSeconds < startSeconds) {
            // Caso che attraversa la mezzanotte - generiamo due range separati
            Arbitrary<LocalTime> beforeMidnight = Arbitraries.integers()
                    .between(startSeconds, 86399) // fino a 23:59:59
                    .map(LocalTime::ofSecondOfDay);

            Arbitrary<LocalTime> afterMidnight = Arbitraries.integers()
                    .between(0, endSeconds)
                    .map(LocalTime::ofSecondOfDay);

            return Arbitraries.oneOf(beforeMidnight, afterMidnight);
        } else {
            // Caso normale
            return Arbitraries.integers()
                    .between(startSeconds, endSeconds)
                    .map(LocalTime::ofSecondOfDay);
        }

    }


}
