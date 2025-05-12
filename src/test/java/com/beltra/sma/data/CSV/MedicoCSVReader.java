package com.beltra.sma.data.CSV;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;

import java.text.ParseException;
import java.util.List;



public class MedicoCSVReader extends CSVAbstractReader<Medico> {

    List<Anagrafica> anagrafiche;

    /** Costruttore: inizializzo con le giuste liste. */
    public MedicoCSVReader() {
        super(Medico.class); // gli passo il nome della classe dell'entita' che MedicoCSVReader sta gestendo, in questo caso si tratta dell'entità Medico

        AnagraficaCSVReader anagraficaReader = new AnagraficaCSVReader();
        anagrafiche = anagraficaReader.leggiCSV( getPathAnagraficheCSV() );
    }

    /** Ricerca la giusta anagrafica da assegnare al medico corrente.<br>
     * La ricerca e' fatta in base ad IDAnagrafica. */
    private Anagrafica getRightAnagrafica(String field) {

        return this.anagrafiche
                .stream()
                .filter(a -> a.getIdAnagrafica() == Long.parseLong(field))
                .findFirst()
                .orElseThrow();
    }



    @Override
    protected Medico popolaEntita(String[] fields, List<?>... liste) throws ParseException {

        Medico medico = new Medico();

        medico.setMatricola(fields[0]);
        medico.setAnagrafica( getRightAnagrafica(fields[1]) );
        medico.setIdAnagrafica( Long.parseLong(fields[1]) );
        medico.setSpecializzazione(fields[2]);

        return medico;
    }



//    public List<Medico> leggiCSV(String filePath) {
//        return super.leggiCSV(filePath, anagrafiche); // N.B.: sto passando la lista di anagrafiche, anzichè il generics
//    }


}
