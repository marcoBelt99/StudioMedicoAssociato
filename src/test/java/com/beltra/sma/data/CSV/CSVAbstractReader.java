package com.beltra.sma.data.CSV;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/** Classe astratta avente come unico scopo la lettura di un generico file CSV le cui colonne contengono gli attributi di una generica entita',
 *  mentre ogni riga del file CSV rappresenta un "record" di tale entita', ovvero una specifica collezione di valori per i suoi attributi.<br>
 *  Nota bene che non si conosce a priori il tipo dell'entita' che si vuole leggere e popolare, e non si conosce a priori neppure il numero di
 *  colonne che saranno presenti nel file CSV.
 *  */
public abstract class CSVAbstractReader<T> {

    private final String SEPARATOR = ",";

    @Getter
    private final String pathAnagraficheCSV = "src/test/resources/anagrafiche.csv";

    @Getter
    private final String pathPrestazioniCSV = "src/test/resources/prestazioni.csv";

    @Getter
    private final String pathMediciCSV = "src/test/resources/medici.csv";

    /** N.B.: come CSV di default si legge il file "visiteGiornaliereFull.csv" */
    @Getter
    private final String pathVisiteGiornaliereFullCSV = "src/test/resources/visiteGiornaliereFull.csv";


    private final Class<T> classeEntita;

    public CSVAbstractReader(Class<T> classeEntita) {
        this.classeEntita = classeEntita;
    }

    /** Necessario per i test, per sapere a runtime quale path chiamare, in base al tipo di entità
     *  che sto analizzando.<br>
     *  @apiNote Nota bene: viene richiamato il path di default. Se e' necessario un path specifico per la lettura di un file, chiamando il metodo
     *  leggiCSV() e' possibile specificare il path specifico.
     *  */
    public String getRightFilePath() {
        if (classeEntita.equals(Anagrafica.class)) {
            return pathAnagraficheCSV;
        } else if (classeEntita.equals(Medico.class)) {
            return pathMediciCSV;
        } else if (classeEntita.equals(Prestazione.class)) {
            return pathPrestazioniCSV;
        } else if(classeEntita.equals(Visita.class)) {
            return pathVisiteGiornaliereFullCSV;
        }
        else {
            throw new IllegalArgumentException("Tipo di entità non supportato: " + classeEntita.getName());
        }
    }

    /** Metodo di lettura da file CSV. <br>
     * @param filePath percorso dello specifico file da leggere.
     * @param liste Parametro delegato alle sottoclassi.
     *              Rappresenta una lista di (eventuali) liste da considerare per il popolamento
     *              della specifica anagrafica associata alla sottoclasse.
     * */
    public List<T> leggiCSV(String filePath, List<?>... liste) {

        // Lista (struttura dati) su cui salvare tutti i records
        List<T> listaRecords = new ArrayList<>();

        // Riga corrente del file
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Salto la prima riga (che contiene le intestazione del CSV)
            br.readLine();

            // Leggo ogni riga e mappo nella generica entita' T
            while ((line = br.readLine()) != null) {

                // Splitto con la virgola
                String[] fields = line.split(SEPARATOR);

                // Da qui in poi codice relativo al parsing e al popolamento dell'entita' in questione
                // e' delegato alla specifica sottoclasse
                T entity = popolaEntita(fields, liste);

                // Aggiungo in lista il record appena letto
                listaRecords.add( entity );
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.err.println("Errore nel formato dei numeri nel CSV: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Errore di parsing della data nel CSV: " + e.getMessage());
        }
        return listaRecords;
    }


    /** Metodo astratto da implementare/concretizzare in maniera specifica (e custom) nelle varie sottoclassi. */
    protected abstract T popolaEntita(String[] fields, List<?>... liste) throws ParseException;


}
