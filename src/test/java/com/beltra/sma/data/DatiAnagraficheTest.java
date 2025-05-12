package com.beltra.sma.data;

import com.beltra.sma.data.CSV.AnagraficaCSVReader;
import com.beltra.sma.data.CSV.CSVAbstractReader;
import com.beltra.sma.model.Anagrafica;

import java.util.List;


public class DatiAnagraficheTest implements DatiTest<Anagrafica> {

    @Override
    public List<Anagrafica> getDatiTest() {

        CSVAbstractReader<Anagrafica> anagraficaCSVReader = new AnagraficaCSVReader();
        return anagraficaCSVReader.leggiCSV( anagraficaCSVReader.getRightFilePath() );
    }

}