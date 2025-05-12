package com.beltra.sma.data;

import com.beltra.sma.data.CSV.CSVAbstractReader;
import com.beltra.sma.data.CSV.MedicoCSVReader;
import com.beltra.sma.model.Medico;

import java.util.List;


public class DatiMediciTest implements DatiTest<Medico> {

    @Override
    public List<Medico> getDatiTest() {

        CSVAbstractReader<Medico> medicoCSVReader = new MedicoCSVReader();
        return medicoCSVReader.leggiCSV(medicoCSVReader.getRightFilePath());
    }
}