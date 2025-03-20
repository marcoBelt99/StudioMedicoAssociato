package com.beltra.sma.generator;


import com.beltra.sma.datastructures.Pianificatore;
import com.beltra.sma.model.Visita;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class VisitaGenerator extends Generator<Visita> {

    public VisitaGenerator() {
        super(Visita.class);
    }

    @Override
    public Visita generate(SourceOfRandomness random, GenerationStatus status) {
        Visita visita = new Visita();

        // ID casuale tra 1 e 100
        visita.setIdVisita(random.nextLong(1, 100));

        // Genera un orario casuale tra le 08:00 e le 18:00
        int ora = random.nextInt(6, 18);
        int minuti = random.nextInt(0, 59);
        visita.setOra(new Time(TimeUnit.HOURS.toMillis(ora) + TimeUnit.MINUTES.toMillis(minuti)));

        return visita;
    }
}
// Generator personalizzato per Property Based Testing