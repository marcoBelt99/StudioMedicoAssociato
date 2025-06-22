package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PianificazioneComponentAmmissibilitaGiornoTests extends PianificazioneComponentTest {

    // TODO: PARAMETRIZZARE QUESTI 3 TESTS!

    /// CASO GIORNO NON AMMISSIBILE
    @Test
    public void testGiornoNotAmmissibile_BecauseIsSaturday() {

        // ARRANGE & ACT
        Optional<SlotDisponibile> slot = arrangeAndAct(20.0,
                dataSabato18Gennaio2025Test, // sabato ==> non ammissibile
                LocalTime.of(7, 30), // chissene frega, può essere qualsiasi ora del sabato, tanto il sabato non è un giorno ammissibile
                getAllDatiMediciTests(),
                new ArrayList<>(), // chissene frega anche della lista, tanto a me interessa assertare sul giorno

                dataLunedi20Gennaio2025Test, // mi aspetto il 20 (lunedì)
                LocalTime.of(7, 5),
                getAllDatiMediciTests().get(0)
        );

        // ASSERT
        assertTrue( slot.isPresent() );
        assertEquals( dataLunedi20Gennaio2025Test, slot.get().getData() );

    }



    @Test
    public void testGiornoNotAmmissibile_BecauseIsSunday() {

        // ARRANGE & ACT
        Optional<SlotDisponibile> slot = arrangeAndAct(20.0,
                dataDomenica19Gennaio2025Test, // domenica ==> non ammissibile
                LocalTime.of(7, 30), // chissene frega, può essere qualsiasi ora del sabato, tanto il sabato non è un giorno ammissibile
                getAllDatiMediciTests(),
                new ArrayList<>(), // chissene frega anche della lista, tanto a me interessa assertare sul giorno

                dataLunedi20Gennaio2025Test, // mi aspetto il 20 (lunedì)
                LocalTime.of(7, 5),
                getAllDatiMediciTests().get(0)
        );

        // ASSERT
        assertTrue( slot.isPresent() );
        assertEquals( dataLunedi20Gennaio2025Test, slot.get().getData() );

    }



    /// CASO GIORNO AMMISSIBILE
    @Test
    public void testGiornoAmmissibile_BecauseIsFriday() {

        // ARRANGE & ACT
        Optional<SlotDisponibile> slot = arrangeAndAct(20.0,
                dataVenerdi17Gennaio2025Test, // domenica ==> non ammissibile
                LocalTime.of(7, 30), // chissene frega, può essere qualsiasi ora
                getAllDatiMediciTests(),
                new ArrayList<>(), // chissene frega anche della lista, tanto a me interessa assertare sul giorno

                dataVenerdi17Gennaio2025Test,
                LocalTime.of(7, 5),
                getAllDatiMediciTests().get(0)
        );

        // ASSERT
        assertTrue( slot.isPresent() );
        assertEquals( dataVenerdi17Gennaio2025Test, slot.get().getData() );

    }
}
