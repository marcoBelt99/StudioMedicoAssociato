package com.beltra.sma.data.CSV;


import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** Con questa suite di tests provo a chiamare i metodi di lettura CSV dall'esterno.<br>
 *  Inoltre, testo il funzionamento della classe astratta. */
public class CSVAbstractReaderTests {


    /** Metodo di providing dei dati, necessario per i test con @ParameterizedTest e @MethodSource */
    private static Stream<Arguments> provideDatiTest() {

        CSVAbstractReader<Anagrafica> anagraficaCSVReader = new AnagraficaCSVReader();
        CSVAbstractReader<Medico> medicoCSVReader = new MedicoCSVReader();
        CSVAbstractReader<Prestazione> prestazioneCSVReader = new PrestazioneCSVReader();
        CSVAbstractReader<Visita> visitaCSVReader = new VisitaCSVReader();

        return Stream.of(

            // Gli passo: istanza della classe da leggere, filepath da leggere, dimensione lista risultante dopo la lettura
            Arguments.of( anagraficaCSVReader,  anagraficaCSVReader.leggiCSV(anagraficaCSVReader.getRightFilePath()).size() ),
            Arguments.of( medicoCSVReader,  medicoCSVReader.leggiCSV(medicoCSVReader.getRightFilePath()).size() ),
            Arguments.of( prestazioneCSVReader, prestazioneCSVReader.leggiCSV(prestazioneCSVReader.getRightFilePath()).size() ),
            Arguments.of( visitaCSVReader, visitaCSVReader.leggiCSV(visitaCSVReader.getRightFilePath()).size() )

        );
    }


    @ParameterizedTest
    @MethodSource("provideDatiTest")
    void testCSVReader_Works(CSVAbstractReader reader, int expectedListaSize) {


        // La lista risultante dalla lettura non è vuota
        assertFalse( reader.leggiCSV(reader.getRightFilePath()).isEmpty());

        // La lista risultante dalla lettura deve avere la giusta dimensione della lettura della classe astratta...
        assertEquals( expectedListaSize, reader.leggiCSV(reader.getRightFilePath()).size() );

    }




}
