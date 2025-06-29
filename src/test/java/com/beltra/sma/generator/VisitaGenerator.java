// Generatore per Visita
package com.beltra.sma.generator;

import com.beltra.sma.model.Visita;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class VisitaGenerator extends Generator<Visita> {

    public VisitaGenerator() {
        super(Visita.class);
    }

    @Override
    public Visita generate(SourceOfRandomness random, GenerationStatus status) {
        Visita visita = new Visita();

        // ID visita
        visita.setIdVisita((long) random.nextInt(1, 10000));

        // Orario casuale in fascia lavorativa
        LocalTime orario = generateWorkingHourTime(random);
        visita.setOra(Time.valueOf(orario));

        // Numero ambulatorio
        visita.setNumAmbulatorio(random.nextInt(1, 11));

        // Data casuale nei prossimi giorni
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, random.nextInt(0, 7));
        visita.setDataVisita(cal.getTime());

        // Se hai bisogno di settare anagrafica e prestazione
        // MockAnagrafica anagrafica = new MockAnagrafica();
        // visita.setAnagrafica(anagrafica);
        // MockPrestazione prestazione = new MockPrestazione();
        // visita.setPrestazione(prestazione);

        return visita;
    }

    private LocalTime generateWorkingHourTime(SourceOfRandomness random) {
        boolean mattina = random.nextBoolean();

        if (mattina) {
            // Orario mattutino: 8:00 - 12:00
            int hour = random.nextInt(8, 12);
            int minute = random.nextInt(0, 60);
            return LocalTime.of(hour, minute);
        } else {
            // Orario pomeridiano: 14:00 - 18:00
            int hour = random.nextInt(14, 18);
            int minute = random.nextInt(0, 60);
            return LocalTime.of(hour, minute);
        }
    }
}