package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PianificazioneComponentAmmissibilitaGiornoTests extends PianificazioneComponentTest {

    /// CASO GIORNO NON AMMISSIBILE

    @Test
    public void testGiornoNotAmmissibile() {


        // ARRANGE & ACT
        Optional<SlotDisponibile> slot = arrangeAndAct(20.0,
                dataSabato18Gennaio2025Test, // sabato ==> non ammissibile
                LocalTime.of(7, 30), // chissene frega, può essere qualsiasi ora del sabato, tanto il sabato non è un giorno ammissibile
                getAllDatiMediciTests(),
                new ArrayList<>(),

                dataLunedi20Gennaio2025Test, // mi aspetto il 20 (lunedì)
                LocalTime.of(7, 5),
                getAllDatiMediciTests().get(0)
        );


        // MOCKING
        // Creo un oggetto mock per l'interfaccia PianificazioneComponent
        // PianificazioneComponent pc = mock(PianificazioneComponent.class);

        // Verifichiamo che il metodo trovaSlotGiornoSuccessivo sia effettivamente chiamato
        // verify( pc ).trovaSlotGiornoSuccessivo(Calendar.getInstance(), 20.0, getAllDatiMediciTests()).get();

        // ASSERT
        assertTrue( slot.isPresent() );
        assertEquals( dataLunedi20Gennaio2025Test, slot.get().getData() );



    }
}
