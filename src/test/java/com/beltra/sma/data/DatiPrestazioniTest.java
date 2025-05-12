package com.beltra.sma.data;

import com.beltra.sma.data.CSV.CSVAbstractReader;
import com.beltra.sma.data.CSV.PrestazioneCSVReader;
import com.beltra.sma.model.Prestazione;

import java.util.List;

public class DatiPrestazioniTest implements DatiTest<Prestazione> {


    public List<Prestazione> getDatiTest()  {

        CSVAbstractReader<Prestazione> prestazioneCSVReader = new PrestazioneCSVReader();
        return prestazioneCSVReader.leggiCSV( prestazioneCSVReader.getRightFilePath() );
    }


}
