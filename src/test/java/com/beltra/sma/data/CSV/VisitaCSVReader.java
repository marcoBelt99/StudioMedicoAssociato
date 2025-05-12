package com.beltra.sma.data.CSV;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Classe di utilita' avente lo scopo di leggere massivamente le righe di un file CSV e popolare
 *  l'opportuna lista di visite. <br>
 *  Il tutto al fine di ottenere migliori dati di test. <br>
 *  Di default si fa uso del file CSV di default <b>visiteGiornaliereFull.csv</b>
 *  */
public class VisitaCSVReader extends CSVAbstractReader<Visita> {

    List<Anagrafica> anagrafiche;
    List<Prestazione> prestazioni;

    public VisitaCSVReader() {
        super(Visita.class);

        AnagraficaCSVReader anagraficaReader = new AnagraficaCSVReader();
        PrestazioneCSVReader prestazioneReader = new PrestazioneCSVReader();
        anagrafiche = anagraficaReader.leggiCSV( getPathAnagraficheCSV(), anagrafiche );
        prestazioni = prestazioneReader.leggiCSV( getPathPrestazioniCSV(), prestazioni );
    }



    @Override
    protected Visita popolaEntita(String[] fields, List<?>... liste) throws ParseException {
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");

        Visita visita = new Visita();

        Anagrafica anag = getRightAnagraficaMedico(fields[4]);
        Prestazione prest = getRightPrestazione(fields[5]);

        visita.setIdVisita( Long.parseLong(fields[0])); // id
        visita.setDataVisita( formatterDate.parse( fields[1] ) ); // data

        visita.setOra( Time.valueOf(LocalTime.parse(fields[2], formatterTime)) ); // ora
        visita.setNumAmbulatorio( Integer.parseInt(fields[3]) ); // num ambulatorio
        visita.setAnagrafica( anag );
        visita.setPrestazione( prest );

        return visita;
    }


    /** Associa al medico la giusta anagrafica */
    private Anagrafica getRightAnagraficaMedico(String field) {
        MedicoCSVReader medicoReader = new MedicoCSVReader();
        List<Medico> medici = medicoReader.leggiCSV(getPathMediciCSV(), anagrafiche);
        return medici
                .stream().filter(m -> m.getIdAnagrafica() == Long.parseLong((field)))
                .findFirst().orElseThrow()
                .getAnagrafica();
    }

    /** Cerca la prestazione tra quelle esistenti. La ricerca viene eseguita nel CSV delle prestazioni. */
    private Prestazione getRightPrestazione(String field) {
        return prestazioni
               .stream().filter(p -> p.getIdPrestazione() == Long.parseLong((field)))
               .findFirst()
               .orElseThrow();
    }

    @Override
    public List<Visita> leggiCSV(String filePath, List<?>... liste) {
        return super.leggiCSV(filePath, liste );
    }
}