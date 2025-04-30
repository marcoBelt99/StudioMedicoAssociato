package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.SlotDisponibile;
import com.beltra.sma.utils.VisitaCSVReader;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PianificazioneComponentListaVisiteNotVuotaTests extends PianificazioneComponentTest {


/// ###########################   2) CASO INDUTTIVO: LISTA VISITE NON VUOTA   ############################
/// ######################################################################################################
/// ######################################################################################################


    /// 2.A
    /// Se (isOrarioAmmissibile( oraInizioPrevistaProssimaVisita) )
    ///     allora slot.ora = 09:35
    @Test
    public void testTrovaSlotDisponibile_WithOraAttualeBeforeAperturaMattina_WithMattinaAmmissibileAndPomeriggioVuoto() {

        // ARRANGE & ACT
        Date dataExpected = dataVenerdi17Gennaio2025Test;
        LocalTime oraExpected = LocalTime.of(9, 30).plusMinutes( PianificazioneComponent.pausaFromvisite ); // Mi aspetto 09:35
        Medico medicoExpected = getAllDatiMediciTests().get(2); // prendo medico di id=2 (sta in terza posizione, a partire da 0)

        Optional<SlotDisponibile> risultato = arrangeAndAct(
                90.0,
                dataVenerdi17Gennaio2025Test,
                LocalTime.of(6, 55),
                getAllDatiMediciTests(), // come dati di test mi servono anche i medici (disegnati su carta) (sono != da quelli a DB)
                getAllVisiteByData(), // come dati di test ho le solite 6 visite (quelle disegnate su carta)

                dataExpected,
                oraExpected,
                medicoExpected
        );


        // ASSERT
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }


    /// 2.B
    /// visiteMattino = \[ v1, v2, v3, __], visitePomeriggio = []
    /// La visita che finisce per prima è alle 09:30, quindi: 09:30 + durata (4h) + 5 min = 13:35 > oraChiusuraMattina < oraAperturaPomeriggio
    /// Quindi visto che visitePomeriggio = [] mi aspetto che oraSlot = 14:05
    @Test
    public void testTrovaSlotDisponibile_WithOraAttualeBeforeAperturaMattina_WithMattinaNotAmmissibileAndPomeriggioVuoto() {

        // ARRANGE & ACT
        Date dataExpected = dataVenerdi17Gennaio2025Test;
        LocalTime oraExpected =  // visto che so che: con gli orari sforo la fascia del mattino,
                // e soprattutto che non ci sono visite al pomeriggio, pianifico per 14:05
                PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes(PianificazioneComponent.pausaFromvisite);  //
        Medico medicoExpected = getAllDatiMediciTests().get(0); // prendo il primo medico medico (di id=1), perchè non essendocene di occupati, si riparte dal primo in lista



        // Dopo inserimento di v6 ho:
        // listaVisite = [v1, v2, ..., v6]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<3, 09:30>, <2, 10:05>, <1, 11:30>]


        Optional<SlotDisponibile> risultato = arrangeAndAct(
                // TODO: durata prestazione esagerata, in modo da farmi avere lo slot disponibile alle 14:05
                getAllPrestazioniTests().get(3).getDurataMedia(),  // durata = 4 ore,
                dataVenerdi17Gennaio2025Test, // TODO: 17/01/2025 (mi servono le visite). E' una data rappresentativa (copre molti casi di test).
                LocalTime.of( 6, 55  ),
                getAllDatiMediciTests(), //  come dati di test mi servono anche i medici (sono != da quelli a DB),
                getAllVisiteByData(), // come dati di test ho le solite 6 visite (quelle disegnate su carta)

                dataExpected,
                oraExpected,
                medicoExpected
        );


        // ASSERT
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }


    /// 2.C: ho queste visite, ne prendo una di 2 ore, che sfora, quindi mi aspetto
    /// che venga pianificata al pomeriggio (visto che non ne ho altre)
    @Test
    public void testTrovaSlotDisponibile_WithOraAttualeAmmissibileInMattina_WithMattinaFull_AndPomeriggioVuoto(){
        // ARRANGE & ACT
        Date dataExpected = dataVenerdi17Gennaio2025Test;
        LocalTime oraExpected =  // visto che so che: con gli orari sforo la fascia del mattino,
                // e non ci sono visite al pomeriggio, pianifico per 14:05
                PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes(PianificazioneComponent.pausaFromvisite);  //
        Medico medicoExpected = getAllDatiMediciTests().get(0); // prendo il primo medico medico (di id=1), perchè al pomeriggio non c'è nessuno, quindi si inizia ad occupare il 1° medico

        // TODO:
        // Dopo inserimento di v8 ho:
        // listaVisite = [v1, v2, ..., v8]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<2, 10:05>, <1, 11:35>, <3, 11:55>]


        Optional<SlotDisponibile> risultato = arrangeAndAct(
                getAllPrestazioniTests().get(1).getDurataMedia(),  // durata = 2 ore,
                dataVenerdi17Gennaio2025Test,
                LocalTime.of( 8, 55  ), // ora ammissibile
                getAllDatiMediciTests(), //  come dati di test mi servono anche i medici (sono != da quelli a DB),

                //datiVisiteTest.getListaVisiteWithMattinoFull(), // come dati di test ho le prime 8 visite (tra quelle disegnate su carta)
                VisitaCSVReader.leggiVisiteDaCsv("src/test/resources/visiteMattinaFull_Caso_D.csv", getAllDatiMediciTests(), getAllPrestazioniTests()),

                dataExpected,
                oraExpected,
                medicoExpected
        );


        // ASSERT
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }





    /// 2.D visite sia al mattino che al pomeriggio.
    ///     TODO: come listaVisite da qui in poi leggo da file CSV.
    ///     File usato: visiteMattinaFull_Caso_D.csv
    ///     TODO: simulo l'inserimento di v9
    @Test
    public void testTrovaSlotDisponibile_WithOraAttualeAmmissibileWithMattinaFull_AndPomeriggioNotEmpty_AfterV9() {
        // ARRANGE & ACT
        Date dataExpected = dataVenerdi17Gennaio2025Test;
        LocalTime oraExpected =  // visto che so che: con gli orari sforo la fascia del mattino, e non ci sono visite al pomeriggio, pianifico per 14:05
                PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes(PianificazioneComponent.pausaFromvisite);  //
        Medico medicoExpected = getAllDatiMediciTests().get(0); // mi aspetterei che inizino a ripartire da capo anche i medici finchè non li rioccupo tutti gli assegnerei 14:05!! ==> TODO: implementare questo comportamento

        // TODO:
        // Dopo inserimento di v9 ho:
        // listaVisite = [v1, v2, ..., v9]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, 11:30>, <3, 11:55>, <2, 16:05>]


        Optional<SlotDisponibile> risultato = arrangeAndAct(
                getAllPrestazioniTests().get(1).getDurataMedia(),  // durata = 2 ore,
                dataVenerdi17Gennaio2025Test,
                LocalTime.of( 8, 55  ), // ora ammissibile
                getAllDatiMediciTests(), //  come dati di test mi servono anche i medici (sono != da quelli a DB),
                VisitaCSVReader.leggiVisiteDaCsv("src/test/resources/visiteMattinaFull_Caso_D.csv", getAllDatiMediciTests(), getAllPrestazioniTests()), // come dati di test ho le prime 8 visite (tra quelle disegnate su carta) // TODO: le recupero da CSV

                dataExpected,
                oraExpected,
                medicoExpected
        );


        // ASSERT
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );

    }

/*
    @Ignore
    /// 2.E visite sia al mattino che al pomeriggio.
    ///     TODO: come listaVisite da qui in poi leggo da file CSV.
    ///     File usato: visiteMattinaFull.csv
    ///     TODO: simulo l'inserimento di v10: Mi aspetto di ottenere lo stesso comportamento di quanto fatto nei fogli di carta
    ///
    @Test
    public void testTrovaSlotDisponibile_WithOraAttualeAmmissibileWithMattinaFull_AndPomeriggioNotEmpty_AfterV10() {
        // ARRANGE & ACT
        Date dataExpected = dataVenerdi17Gennaio2025Test;
        LocalTime oraExpected =  // visto che so che: con gli orari sforo la fascia del mattino, e non ci sono visite al pomeriggio, pianifico per 14:05
                PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes(PianificazioneComponent.pausaFromvisite);  //
        Medico medicoExpected = getAllDatiMediciTests().get(2); // mi aspetterei che inizino a ripartire da capo anche i medici finchè non li rioccupo tutti gli assegnerei 14:05!! ==> TODO: implementare questo comportamento

        // TODO:
        // Dopo inserimento di v10 ho:
        // listaVisite = [v1, v2, ..., v10]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<3, 11:55>, <2, 16:05>, <1, 17:05>]


        Optional<SlotDisponibile> risultato = arrangeAndAct(
                getAllPrestazioniTests().get(2).getDurataMedia(),  // durata = 3 ore,
                dataVenerdi17Gennaio2025Test,
                LocalTime.of( 8, 55  ), // ora ammissibile
                getAllDatiMediciTests(), //  come dati di test mi servono anche i medici (sono != da quelli a DB),
                VisitaCSVReader.leggiVisiteDaCsv("src/test/resources/visiteMattinaFull_9visite.csv", getAllDatiMediciTests(), getAllPrestazioniTests()
                        ), // come dati di test ho le prime 9 visite (tra quelle disegnate su carta) // TODO: le recupero da CSV

                dataExpected,
                oraExpected,
                medicoExpected
        );


        // ASSERT
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );

    }

    */



    /// Bugfix: mi entra dentro alla lista visite:
    /// per fixare il bug per cui oraAttuale senza durata non è ammissibile because after chiusura pomeriggio
    ///              mentre invece (oraAttuale + durata) non è ammissibile because before apertura mattina
    ///         Esempio: il 26/03/2025 alle ore 23:17 ==> non è ammissibile perchè after chiusura pomeriggio
    ///                  facendo (23:17 + 45min) = 00:02 ==> non è ammissibile because before apertura mattina del giorno 27/03/2025
    @Test
    public void testTrovaSlotDisponibile_BugFix() {
        // ARRANGE & ACT
        Date dataExpected = new GregorianCalendar(2025, Calendar.MARCH, 27).getTime();
        LocalTime oraExpected =
                PianificazioneComponent.orarioAperturaMattina.plusMinutes(PianificazioneComponent.pausaFromvisite);  //
        Medico medicoExpected = getAllDatiMediciTests().get(0); // prendo il primo medico medico (di id=1), perchè al pomeriggio non c'è nessuno, quindi si inizia ad occupare il 1° medico

        // TODO:
        // Dopo inserimento di v8 ho:
        // listaVisite = [v1, v2, ..., v8]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<2, 10:05>, <1, 11:35>, <3, 11:55>]


        Optional<SlotDisponibile> risultato = arrangeAndAct(
                45.0,  // durata = 45 min
                new GregorianCalendar(2025, Calendar.MARCH, 26).getTime(),
                LocalTime.of( 23, 17  ), // ora non ammissibile
                getAllDatiMediciTests(), //  medici che ho su carta (sono != da quelli a DB),
                //datiVisiteTest.getListaVisiteWithMattinoFull(), // come dati di test ho le prime 8 visite (tra quelle disegnate su carta)
                VisitaCSVReader.leggiVisiteDaCsv("src/test/resources/visiteMattinaFull_Caso_D.csv", getAllDatiMediciTests(), getAllPrestazioniTests()), // come dati di test ho le prime 8 visite (tra quelle disegnate su carta) // TODO: le recupero da CSV

                dataExpected,
                oraExpected,
                medicoExpected
        );


        // ASSERT
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }



}
