// Generatore per Lista di Visite Giornaliere
package com.beltra.sma.generator;

import com.beltra.sma.model.Visita;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.ArrayList;
import java.util.List;

public class VisiteGiornaliereGenerator extends Generator<List<Visita>> {

    private final VisitaGenerator visitaGenerator;

    public VisiteGiornaliereGenerator() {
        super((Class<List<Visita>>) (Class<?>) List.class);
        this.visitaGenerator = new VisitaGenerator();
    }

    @Override
    public List<Visita> generate(SourceOfRandomness random, GenerationStatus status) {
        List<Visita> visite = new ArrayList<>();

        // Genera tra 0 e 10 visite per la giornata
        int size = random.nextInt(0, 11);

        for (int i = 0; i < size; i++) {
            visite.add(visitaGenerator.generate(random, status));
        }

        return visite;
    }
}
