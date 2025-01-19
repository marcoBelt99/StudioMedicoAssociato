package com.beltra.sma.components;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import org.junit.jupiter.api.Test;


import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

/** Classe di utilita' che genera liste di dati utili per la classe di test {@link PianificazioneComponentTest}  */
public class PianificazioneComponentDataTest {

    public static List<Anagrafica> getListaAnagraficheTest() {

        Anagrafica anagrafica1 = new Anagrafica();
        anagrafica1.setIdAnagrafica(1L);
        anagrafica1.setCognome("Forza");
        anagrafica1.setNome("Mattia");
        anagrafica1.setDataNascita( new GregorianCalendar(1999, Calendar.FEBRUARY,8).getTime() );
        anagrafica1.setGenere("M");

        Anagrafica anagrafica2 = new Anagrafica();
        anagrafica2.setIdAnagrafica(2L);
        anagrafica2.setCognome("Lanza");
        anagrafica2.setNome("Eliana");
        anagrafica2.setDataNascita( new GregorianCalendar(1999, Calendar.MARCH,15).getTime() );
        anagrafica2.setGenere("F");

        Anagrafica anagrafica3 = new Anagrafica();
        anagrafica3.setIdAnagrafica(3L);
        anagrafica3.setCognome("Berti");
        anagrafica3.setNome("Federico");
        anagrafica3.setDataNascita( new GregorianCalendar(1999, Calendar.DECEMBER,10).getTime() );
        anagrafica3.setGenere("M");

        return Arrays.asList(anagrafica1, anagrafica2, anagrafica3);
    }

    public static List<Medico> getListaMediciTest() {

        List<Anagrafica> listaAnagrafiche = getListaAnagraficheTest();

        Medico medico1 = new Medico();
        medico1.setMatricola("MED0001");
        medico1.setIdAnagrafica(1L);
        medico1.setSpecializzazione("");

        Medico medico2 = new Medico();
        medico2.setMatricola("MED0002");
        medico2.setIdAnagrafica(2L);
        medico2.setSpecializzazione("Pediatria");

        Medico medico3 = new Medico();
        medico3.setMatricola("MED0003");
        medico3.setIdAnagrafica(3L);
        medico3.setSpecializzazione("Chirurgia");

        List<Medico> listaMedici = Arrays.asList(medico1, medico2, medico3);

        // Ad ogni singolo medico assegno la rispettiva anagrafica
        int i = 0;
        for(i=0;i< listaMedici.size();i++)
            listaMedici.get(i).setAnagrafica(listaAnagrafiche.get(i));

        return listaMedici;
    };

    public static List<Prestazione> getListaPrestazioniTest() {

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


    private final PianificazioneManager pianificazioneManager = new PianificazioneManager();


    @Test
    // public static List<Visita> getListaVisiteTest() {
    public void testListaVisiteTest() {

    /** Scelgo un caso rappresentativo:
     *  Come data attuale scelgo: venerdì 17 gennaio: le visite devono essere schedulate
     *  entro gli orari di apertura di mattina e pomeriggio, e se una visita poi sfora la
     *  giornata di venerdì, deve essere assegnata a lunedì 20.
     * */

        List<Anagrafica> listaAnagrafiche = getListaAnagraficheTest(); // Medici (indici): 0  1  2
        List<Prestazione> listaPrestazioni = getListaPrestazioniTest();
        // listaPrestazioni.durate = { 15min, 2h, 3h, 4h, 4.5h, 5h, 20min }
        //  (indici)                     0     1   2   3    4    5   6


        // Lista di indici per assegnare le prestazioni che desidero alle visite di inizio giornata
        List<Integer> listaIndiciPrestazioniIniziali = Arrays.asList(0, 2, 1);
        List<Prestazione> listaPrestazioniIniziali = new ArrayList<>(); // Prime prestazioni da assegnare alle visite di inizio giornata

        listaIndiciPrestazioniIniziali.forEach(i -> listaPrestazioniIniziali.add( listaPrestazioni.get(i) ) );

        /** Data di test */
        Date dataAttuale = new GregorianCalendar(2025, Calendar.JANUARY, 17 ).getTime();


        // TODO: 1)
        // Se sono alla prima visita del giorno, finchè non si sono occupati tutti i medici in una visita, allora assegno sempre
        // come ora le 07:00, maggiorata dei 5 minuti (quindi 07:05).
        for(int i=0;i < getListaMediciTest().size();i++) {

            Visita visita = new Visita();

            visita.setIdVisita( (long) (i+1) );
            visita.setAnagrafica( listaAnagrafiche.get( i ) );
            visita.setDataVisita(dataAttuale);
            visita.setOra( Time.valueOf( LocalTime.of(7,0).plusMinutes( pianificazioneManager.getPausa5Minuti() ) ));
            visita.setPrestazione( listaPrestazioniIniziali.get( i ) ); // ad ogni iterazione prendi una determinata durata che ho scelto io prima
            visita.setNumAmbulatorio( i+1 );

           // Aggiungo la visita sia alla listaVisite sia alla mappa visiteMap
            pianificazioneManager.aggiungiVisita( visita );

        }


        System.out.println();
        // TODO: 2)
        // Inizio ad inserire le visite


        Visita v4 = new Visita();
        pianificazioneManager.creaNuovaVisita( v4, dataAttuale, listaPrestazioni.get(1) ); // 2 ore

        Visita v5 = new Visita();
        pianificazioneManager.creaNuovaVisita( v5, dataAttuale, listaPrestazioni.get(6) ); // 20 min

        Visita v6 = new Visita();
        pianificazioneManager.creaNuovaVisita( v6, dataAttuale, listaPrestazioni.get(1) ); // 2 ore



        System.out.println("VISITE DI TEST:\n");
        pianificazioneManager.getListaVisite().forEach(v -> {
            System.out.println(v);
            System.out.println();
        });


        System.out.println("\nMAPPA ORDINATA: ");

        // Stampa la mappa ordinata
        pianificazioneManager
                .getMediciMap()
                .forEach( (id, oraFine) -> System.out.println("\nID MEDICO: " + id + ", Ora Fine: " + oraFine) );


    }









}
