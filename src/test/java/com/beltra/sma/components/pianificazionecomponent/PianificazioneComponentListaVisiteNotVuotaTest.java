package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.model.Medico;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PianificazioneComponentListaVisiteNotVuotaTest extends PianificazioneComponentTest {


/// ###########################   2) CASO INDUTTIVO: LISTA VISITE NON VUOTA   ############################
/// ######################################################################################################
/// ######################################################################################################


    /// 2.A
    /// Se (isOrarioAmmissibile( oraInizioPrevistaProssimaVisita) )
    ///     allora slot.ora = 09:35
    @Test
    public void testTrovaPrimoSlotDisponibile_WithOraAttualeBeforeAperturaMattina_WithMattinaAmmissibileAndPomeriggioVuoto() {

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
    public void testTrovaPrimoSlotDisponibile_WithOraAttualeBeforeAperturaMattina_WithMattinaNotAmmissibileAndPomeriggioVuoto() {

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
    public void testTrovaPrimoSlotDisponibile_WithOraAttualeAmmissibileInMattina_WithMattinaFull_AndPomeriggioVuoto(){
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
                datiVisiteTest.getListaVisiteWithMattinoFull(), // come dati di test ho le prime 8 visite (tra quelle disegnate su carta)

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


    // TODO:
    /// 2.D visite sia al mattino che al pomeriggio
    @CsvFileSource(resources = "/visiteMattinaFull.csv", numLinesToSkip = 1)
    public void provideDatiTestCase_ListaVisiteNotEmpty() {

    }





}
