package com.beltra.sma.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.beltra.sma.model.Visita;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Prestazione;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;

/** Classe di utilita' avente lo scopo di leggere massivamente le righe di un file CSV e popolare
 *  l'opportuna lista di visite. <br>
 *  Il tutto al fine di ottenere migliori dati di test.
 *  */
public class VisitaCSVReader {

    /** Metodo di lettura da file CSV */
    public static List<Visita> leggiVisiteDaCSV(String filePath) {
        List<Visita> visite = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();
            rows.remove(0); // Rimuove l'intestazione

            // Scorri le righe del CSV, quelle contenenti i valori
            for (String[] row : rows) {
                Visita visita = new Visita();
                visita.setIdVisita(Long.parseLong(row[0]));
                visita.setDataVisita(dateFormat.parse(row[1]));
                visita.setOra(Time.valueOf(row[2]));
                visita.setNumAmbulatorio(Integer.parseInt(row[3]));

                // Creazione di oggetti fittizi per Anagrafica e Prestazione
                Anagrafica anagrafica = new Anagrafica();
                anagrafica.setIdAnagrafica(Long.parseLong(row[4]));

                Prestazione prestazione = new Prestazione();
                prestazione.setIdPrestazione(Long.parseLong(row[5]));

                visita.setAnagrafica(anagrafica);
                visita.setPrestazione(prestazione);

                visite.add(visita);
            }
        } catch (IOException | CsvException | ParseException e) {
            e.printStackTrace();
        }
        return visite;
    }
}