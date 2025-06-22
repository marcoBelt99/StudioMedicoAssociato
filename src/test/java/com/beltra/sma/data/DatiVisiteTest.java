package com.beltra.sma.data;


import com.beltra.sma.data.CSV.CSVAbstractReader;
import com.beltra.sma.data.CSV.VisitaCSVReader;

import com.beltra.sma.model.Visita;


import org.springframework.stereotype.Component;



import java.util.List;


@Component
public class DatiVisiteTest implements DatiTest<Visita> {


    public List<Visita> getDatiTest()  {
        return getListaVisiteTest();
    }


    public List<Visita> getListaVisiteTest() {

        /** Scelgo un caso rappresentativo:
         *  Come data attuale scelgo: venerdì 17 gennaio (cioè della variabile dataAttualeDiTest): le visite devono essere schedulate
         *  entro gli orari di apertura di mattina e pomeriggio, e se una visita poi sfora la
         *  giornata di venerdì, deve essere assegnata a lunedì 20 gennaio.
         * */
        return getListaVisiteFullFromCSV().stream().limit(6).toList();
    }


    /** Legge le visite dal relativo file CSV passato nel parametro <b>path</b>.<br>
     *  Vengono considerate la lista dei medici e la lista delle prestazioni designate per i test (quelle su carta).
     * */
    public List<Visita> getListaVisiteFullFromCSV(String path) {
        CSVAbstractReader<Visita> csvReader = new VisitaCSVReader();
        return csvReader.leggiCSV( path );
    }

    /** Legge le visite dal file CSV di default delle visite giornaliere <b>path</b>.<br>
     *  Vengono considerate la lista dei medici e la lista delle prestazioni designate per i test (quelle su carta).
     * */
    public List<Visita> getListaVisiteFullFromCSV() {
        CSVAbstractReader<Visita> csvReader = new VisitaCSVReader();
        return csvReader.leggiCSV(csvReader.getRightFilePath());
    }



}
