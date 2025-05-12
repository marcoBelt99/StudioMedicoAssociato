package com.beltra.sma.data.CSV;

import com.beltra.sma.model.Prestazione;

import java.text.ParseException;
import java.util.List;

public class PrestazioneCSVReader extends CSVAbstractReader<Prestazione> {

    public PrestazioneCSVReader() {
        super(Prestazione.class); // dico che PrestazioneCSVReader sta gestendo l'entità Prestazione
    }

    @Override
    protected Prestazione popolaEntita(String[] fields, List<?>... liste) throws ParseException{

        Prestazione prestazione = new Prestazione();

        prestazione.setIdPrestazione(Long.parseLong(fields[0]));
        prestazione.setTitolo(fields[1]);
        prestazione.setDescrizione(fields[2]);
        prestazione.setDurataMedia(Double.parseDouble(fields[3]));
        prestazione.setCosto(Double.parseDouble(fields[4]));
        prestazione.setTicket(Double.parseDouble(fields[5]));
        prestazione.setDeleted(Boolean.parseBoolean(fields[6]));

        return prestazione;
    }

    @Override
    public List<Prestazione> leggiCSV(String filePath, List<?>... liste) {
        return super.leggiCSV(filePath, liste);
    }

}
