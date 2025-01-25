package com.beltra.sma.components.data;

import com.beltra.sma.model.Prestazione;

import java.util.Arrays;
import java.util.List;

public class DatiPrestazioniTest implements DatiTest<Prestazione> {


    public List<Prestazione> getDatiTest()  {

        Prestazione prestBreve = new Prestazione();
        prestBreve.setIdPrestazione(1L);
        prestBreve.setTitolo("PRESTAZIONE 15min");
        prestBreve.setDescrizione("DESCRIZIONE PRESTAZIONE CHE DURA 15 MINUTI");
        prestBreve.setDurataMedia( 15.0 ); // durata: 15 minuti
        prestBreve.setCosto( 60.0 );
        prestBreve.setTicket( 5.0 );
        prestBreve.setDeleted(false);

        Prestazione prestDueOre = new Prestazione();
        prestDueOre.setIdPrestazione(2L);
        prestDueOre.setTitolo("PRESTAZIONE 2H");
        prestDueOre.setDescrizione("DESCRIZIONE PRESTAZIONE CHE DURA 2 ORE");
        prestDueOre.setDurataMedia( 120.0 ); // durata: 2 ore
        prestDueOre.setCosto( 60.0 );
        prestDueOre.setTicket( 5.0 );
        prestDueOre.setDeleted(false);

        Prestazione prestTreOre = new Prestazione();
        prestTreOre.setIdPrestazione(3L);
        prestTreOre.setTitolo("PRESTAZIONE 3H");
        prestTreOre.setDescrizione("DESCRIZIONE PRESTAZIONE CHE DURA 3 ORE");
        prestTreOre.setDurataMedia( 180.0 ); // durata: 3 ore
        prestTreOre.setCosto( 100.0 );
        prestTreOre.setTicket( 0.0 );
        prestTreOre.setDeleted(false);

        Prestazione prestQuattroOre = new Prestazione();
        prestQuattroOre.setIdPrestazione(4L);
        prestQuattroOre.setTitolo("PRESTAZIONE 4H");
        prestQuattroOre.setDescrizione("DESCRIZIONE PRESTAZIONE CHE DURA 4 ORE");
        prestQuattroOre.setDurataMedia( 240.0 ); // durata: 4 ore
        prestQuattroOre.setCosto( 150.0 );
        prestQuattroOre.setTicket( 25.0 );
        prestQuattroOre.setDeleted(false);

        Prestazione prestQuattroOreEMezzo = new Prestazione();
        prestQuattroOreEMezzo.setIdPrestazione(5L);
        prestQuattroOreEMezzo.setTitolo("PRESTAZIONE 4.5H");
        prestQuattroOreEMezzo.setDescrizione("DESCRIZIONE PRESTAZIONE CHE DURA 4.5 ORE");
        prestQuattroOreEMezzo.setDurataMedia( 270.0 ); // durata: 4.5 ore
        prestQuattroOreEMezzo.setCosto( 150.0 );
        prestQuattroOreEMezzo.setTicket( 25.0 );
        prestQuattroOreEMezzo.setDeleted(false);

        Prestazione prestCinqueOre = new Prestazione();
        prestCinqueOre.setIdPrestazione(6L);
        prestCinqueOre.setTitolo("PRESTAZIONE 5H");
        prestCinqueOre.setDescrizione("DESCRIZIONE PRESTAZIONE CHE DURA 5 ORE");
        prestCinqueOre.setDurataMedia( 300.0 ); // durata: 5 ore
        prestCinqueOre.setCosto( 500.0 );
        prestCinqueOre.setTicket( 45.0 );
        prestCinqueOre.setDeleted(false);

        Prestazione prestVentiMinuti = new Prestazione();
        prestVentiMinuti.setIdPrestazione(7L);
        prestVentiMinuti.setTitolo("PRESTAZIONE 20min");
        prestVentiMinuti.setDescrizione("DESCRIZIONE PRESTAZIONE CHE DURA 20 MINUTI");
        prestVentiMinuti.setDurataMedia( 20.0 ); // durata: 20min
        prestVentiMinuti.setCosto( 50.0 );
        prestVentiMinuti.setTicket( 4.5 );
        prestVentiMinuti.setDeleted(false);

        return Arrays.asList(prestBreve, prestDueOre, prestTreOre, prestQuattroOre, prestQuattroOreEMezzo, prestCinqueOre, prestVentiMinuti);
    }


}
