package com.beltra.sma.utils;

import com.beltra.sma.datastructures.Pianificatore;
import com.beltra.sma.model.Medico;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.beltra.sma.model.Visita;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Prestazione;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;

/** Classe di utilita' avente lo scopo di leggere massivamente le righe di un file CSV e popolare
 *  l'opportuna lista di visite. <br>
 *  Il tutto al fine di ottenere migliori dati di test.
 *  */
public class VisitaCSVReader {

    /** Metodo di lettura da file CSV */
    public static List<Visita> leggiVisiteDaCsv(String filePath, List<Medico> medici, List<Prestazione> prestazioni) {
        List<Visita> visite = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Salta la prima riga (intestazione del CSV)
            br.readLine();

            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");

            // Leggi ogni riga e map nelle visite
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(","); // Dividi per virgola

                Visita visita = new Visita();


                Anagrafica anag = medici.stream().filter(m -> m.getIdAnagrafica() == Long.parseLong((fields[4]))).findFirst().get().getAnagrafica();
                Prestazione prest = prestazioni.stream().filter(p -> p.getIdPrestazione() == Long.parseLong((fields[5]))).findFirst().get();

                visita.setIdVisita( Long.parseLong(fields[0])); // id
                visita.setDataVisita( formatterDate.parse( fields[1] ) ); // data

                visita.setOra( Time.valueOf(LocalTime.parse(fields[2], formatterTime)) ); // ora
                visita.setNumAmbulatorio( Integer.parseInt(fields[3]) ); // num ambulatorio
                visita.setAnagrafica( anag );
                visita.setPrestazione( prest );

                visite.add(visita);
                // pianificatore.setListaVisite( visite);
                // pianificatore.aggiornaMediciMap();
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Errore nel formato dei numeri nel CSV: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Errore di parsing della data nel CSV: " + e.getMessage());
        }

        return visite;
    }
}