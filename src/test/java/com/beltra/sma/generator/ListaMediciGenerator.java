// Generatore per Lista di Medici
package com.beltra.sma.generator;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.ArrayList;
import java.util.List;

public class ListaMediciGenerator extends Generator<List<Medico>> {

    private final MedicoGenerator medicoGenerator;

    public ListaMediciGenerator() {
        super((Class<List<Medico>>) (Class<?>) List.class);
        this.medicoGenerator = new MedicoGenerator();
    }

    @Override
    public List<Medico> generate(SourceOfRandomness random, GenerationStatus status) {
        List<Medico> medici = new ArrayList<>();

        // Genera tra 1 e 5 medici
        int size = random.nextInt(1, 6);

        for (int i = 0; i < size; i++) {
            medici.add(medicoGenerator.generate(random, status));
        }

        return medici;
    }
}