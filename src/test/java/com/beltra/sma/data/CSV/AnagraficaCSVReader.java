package com.beltra.sma.data.CSV;

import com.beltra.sma.model.Anagrafica;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AnagraficaCSVReader extends CSVAbstractReader<Anagrafica> {

    public AnagraficaCSVReader() {
        super(Anagrafica.class); // dico che AnagraficaCSVReader sta gestendo l'entità Anagrafica
    }

    @Override
    protected Anagrafica popolaEntita(String[] fields, List<?>... liste) throws ParseException {
        Anagrafica anagrafica = new Anagrafica();
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");

        anagrafica.setIdAnagrafica( Long.parseLong(fields[0]));
        anagrafica.setCognome(fields[1]);
        anagrafica.setNome(fields[2]);
        anagrafica.setDataNascita( formatterDate.parse(fields[3]) );
        anagrafica.setGenere(fields[4]);

        return anagrafica;
    }

    @Override
    public List<Anagrafica> leggiCSV(String filePath, List<?>... liste) {
        return super.leggiCSV(filePath, liste );
    }

}
