// Generatore per Medico
package com.beltra.sma.generator;

import com.beltra.sma.model.Medico;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.Random;

public class MedicoGenerator extends Generator<Medico> {

    public MedicoGenerator() {
        super(Medico.class);
    }

    @Override
    public Medico generate(SourceOfRandomness random, GenerationStatus status) {
        Medico medico = new Medico();

        // Genera ID casuale
        medico.setIdAnagrafica((long) random.nextInt(1, 1000));

        // Genera matricola casuale
        medico.setMatricola("MAT" + random.nextInt(1, 10000));

        // Genera specializzazione casuale
        String[] specializzazioni = {
                "Cardiologia", "Dermatologia", "Neurologia",
                "Ortopedia", "Pediatria", "Ginecologia"
        };
        medico.setSpecializzazione(specializzazioni[random.nextInt(0, specializzazioni.length)]);

        // Se hai bisogno di settare l'anagrafica
        // MockAnagrafica anagrafica = new MockAnagrafica();
        // medico.setAnagrafica(anagrafica);

        return medico;
    }
}
