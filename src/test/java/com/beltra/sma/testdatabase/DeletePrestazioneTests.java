package com.beltra.sma.testdatabase;

import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.repository.PrestazioneRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class DeletePrestazioneTests {

    static Long idPrestazioneDaEliminare;

    @Autowired
    PrestazioneRepository prestazioneRepository;


    @BeforeAll
    static void setup() {
        idPrestazioneDaEliminare = 4L;
    }


    @Test
    void deletePrestazioneLogicallyMustMantainAssociatedVisite() {

        // Arrange
        Prestazione prestazione = prestazioneRepository.findById(idPrestazioneDaEliminare).orElseThrow();
        Set<Visita> visiteOfThisPrestazioneBeforeElimination = prestazione.getVisite();

        // Act
        prestazione.setDeleted(true);
        Set<Visita> visiteOfThisPrestazioneAfterElimination = prestazione.getVisite();

        // Assert
        assertEquals( true, prestazione.getDeleted() );

        assertIterableEquals(
                visiteOfThisPrestazioneAfterElimination,
                visiteOfThisPrestazioneBeforeElimination
        );
    }

}
