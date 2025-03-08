package com.beltra.sma.components;

import com.beltra.sma.components.data.DatiMediciTest;
import com.beltra.sma.components.data.DatiPrestazioniTest;
import com.beltra.sma.components.data.DatiTest;
import com.beltra.sma.components.data.DatiVisiteTest;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.MedicoService;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Serve per evitare di mettere static il metodo di providing dei dati di test che servirà poi al metodo di test vero e proprio
@SpringBootTest
public class PianificazioneComponentTest {


    @Autowired
    private PianificazioneComponent pianificazioneComponent;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private DatiVisiteTest datiVisiteTest;


    /// PARAMETRI DI TEST
    Double durataMediaPrestazioneTest;
    Date dataTest;
    Date dataVenerdi17Gennaio2025Test = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
    Date dataSabato18Gennaio2025Test = new GregorianCalendar(2025, Calendar. JANUARY, 18 ).getTime();
    Date dataLunedi20Gennaio2025Test = new GregorianCalendar(2025, Calendar. JANUARY, 20 ).getTime();
    LocalTime oraAttualeTest;
    List<Medico> listaMediciTest;
    List<Visita> listaVisiteTest;
    SlotDisponibile slotDisponibileExpected = new SlotDisponibile();


/// ###################################
/// ###################################
/// SEZIONE GENERAZIONE DATI DI TEST
/// ###################################
/// ###################################


    /**  TODO: In questo metodo inserisco i dati di test per i vari casi di test specifici. */
    private List<Visita> getAllVisiteByData() {
        DatiVisiteTest datiVisiteTest = new DatiVisiteTest();
        return datiVisiteTest
                .getListaVisiteTest()
                .stream()
                .filter( v -> v.getDataVisita().after( DatiTest.dataTest_16Gennaio2025 ) ).toList();
    }

    private List<Medico> getAllDatiMediciTests() {
        DatiMediciTest datiMediciTest = new DatiMediciTest();
        return datiMediciTest.getDatiTest();
    }

    private List<Prestazione> getAllPrestazioniTests() {
        DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
        return datiPrestazioniTest.getDatiTest();
    }



/// ###################################################
/// ###################################################
/// ###################################################



    /**  Fattorizzo in una singola funzione le fasi di ARRANGE e ACT. <br>
     *   Antepongo "Param" al nome dei parametri formali per distinguerle dalle variabili globali di test.
     * @param durataMediaPrestazioneTestParam durata della prestazione
     * @param dataTestParam data di test
     * @param oraAttualeTestParam ora attuale per testing
     * @param listaMediciTestParam lista dei medici di test
     * @param listaVisiteTestParam lista delle visite di test
     * <br>
     * @param dataExpectedParam data slot che mi aspetto
     * @param oraExpectedParam ora slot che mi aspetto
     * @param medicoExpectedParam medico slot che mi aspetto
     * @return lo slot disponibile che mi aspetto
     * */
    private Optional<SlotDisponibile> arrangeAndAct(
                         Double durataMediaPrestazioneTestParam,
                         Date dataTestParam,
                         LocalTime oraAttualeTestParam,
                         List<Medico> listaMediciTestParam,
                         List<Visita> listaVisiteTestParam,

                         Date dataExpectedParam,
                         LocalTime oraExpectedParam,
                         Medico medicoExpectedParam) {

        // ARRANGE
        durataMediaPrestazioneTest = durataMediaPrestazioneTestParam;
        dataTest = dataTestParam;
        oraAttualeTest = oraAttualeTestParam;
        listaMediciTest = listaMediciTestParam;
        listaVisiteTest = listaVisiteTestParam;

        slotDisponibileExpected.setData( dataExpectedParam );
        slotDisponibileExpected.setOrario( Time.valueOf( oraExpectedParam ) );
        slotDisponibileExpected.setMedico( medicoExpectedParam );


        // ACT
        return pianificazioneComponent.trovaPrimoSlotDisponibile(
                    durataMediaPrestazioneTest,
                    dataTest,
                    oraAttualeTest,
                    listaMediciTest,
                    listaVisiteTest
                );

    }



/// ###################################
/// ###################################
/// SEZIONE METODI DI TEST
/// ###################################
/// ###################################




/// TODO: PARAMETRIZZO TUTTI I TESTS CON junit-params


/// CASO GIORNO NON AMMISSIBILE

@Test
public void testGiornoNotAmmissibile() {


    // ARRANGE & ACT
    Optional<SlotDisponibile> slot = arrangeAndAct(20.0,
            dataSabato18Gennaio2025Test, // sabato ==> non ammissibile
            LocalTime.of(7, 30), // chissene frega, può essere qualsiasi ora del sabato, tanto il sabato non è un giorno ammissibile
            getAllDatiMediciTests(),
            new ArrayList<>(),

            dataLunedi20Gennaio2025Test, // mi aspetto il 20 (lunedì)
            LocalTime.of(7, 5),
            getAllDatiMediciTests().get(0)
    );


    // MOCKING
    // Creo un oggetto mock per l'interfaccia PianificazioneComponent
    // PianificazioneComponent pc = mock(PianificazioneComponent.class);

    // Verifichiamo che il metodo trovaSlotGiornoSuccessivo sia effettivamente chiamato
    // verify( pc ).trovaSlotGiornoSuccessivo(Calendar.getInstance(), 20.0, getAllDatiMediciTests()).get();

    // ASSERT
    assertTrue( slot.isPresent() );
    assertEquals( dataLunedi20Gennaio2025Test, slot.get().getData() );



}




/// ######################   1) CASO BASE: LISTA VISITE VUOTA   #####################
/// #########################################################################################
/// #########################################################################################

/** Metodo di providing dei dati, necessario per i test con @ParameterizedTest e @MethodSource */
    private Stream<Arguments> provideDatiTestCase_ListaVisiteEmpty() {



    return Stream.of(
    /// 1.A: oraAttuale before oraAperturaMattina
    /// Se (oraAttuale < oraAperturaMattina)
    ///     allora slot.ora = oraAperturaMattina+5min
            Arguments.of(
                    90.0,
                    dataVenerdi17Gennaio2025Test,
                    LocalTime.of( 6, 55 ), // 06:55 < 07:00
                    medicoService.getAllMedici(),
                    new ArrayList<Visita>(), // lista vuota

                    dataVenerdi17Gennaio2025Test,
                    PianificazioneComponent.orarioAperturaMattina.plusMinutes(PianificazioneComponent.pausaFromvisite),
                    medicoService.getAllMedici().get(0)
            ),

    /// 1.B: oraAttuale ammissibile in mattina
    /// Se (oraAttuale >= oraAperturaMattina) && (oraAttuale <= oraChiusuraMattina)
    ///     allora slot.ora = (oraAttuale + durataPrestazione + 5min)
            Arguments.of(
                    90.0,
                    dataVenerdi17Gennaio2025Test,
                    LocalTime.of( 7, 50  ),
                    medicoService.getAllMedici(),
                    new ArrayList<Visita>(),

                    dataVenerdi17Gennaio2025Test,
                    LocalTime.of( 7, 50  ).plusMinutes( (PianificazioneComponent.pausaFromvisite)),
                    medicoService.getAllMedici().get(0)
            ),

    /// 1.C: oraAttuale not ammissibile in mattina
    /// Se ( (oraAttuale + durataPrestazione + 5min) >= oraChiusuraMattina )
    ///     allora slot.ora = oraAperturaPomeriggio + 5min
            Arguments.of(
                    120.0, // scelgo una durata
                    dataVenerdi17Gennaio2025Test,
                    LocalTime.of(10, 20), // la durata scelta mi fa sforare oraChiusuraMattina
                    medicoService.getAllMedici(),
                    new ArrayList<Visita>(),

                    dataVenerdi17Gennaio2025Test,
                    PianificazioneComponent.orarioAperturaPomeriggio.plusMinutes( PianificazioneComponent.pausaFromvisite ), // Mi aspetto 14:05
                    medicoService.getAllMedici().get(0) // Mi aspetto m1
            ),

    /// 1.D: oraAttuale ammissibile in pomeriggio
            Arguments.of(
                    50.0,
                    dataVenerdi17Gennaio2025Test,
                    LocalTime.of(14, 10),
                    medicoService.getAllMedici(),
                    new ArrayList<Visita>(),

                    dataVenerdi17Gennaio2025Test,
                    LocalTime.of(14, 10).plusMinutes(5),
                    medicoService.getAllMedici().get(0)
            )
    );
}



    @ParameterizedTest
    @MethodSource("provideDatiTestCase_ListaVisiteEmpty") // ARRANGE me la fa questo metodo di providing dei dati
    public void testTrovaPrimoSlotDisponibile_ListaVisiteVuota(Double durataMediaPrestazioneTest, Date dataTest, LocalTime oraAttualeTest,
                                                         List<Medico> listaMediciTest, List<Visita> listaVisiteTest,
                                                         Date dataExpected, LocalTime oraExpected, Medico medicoExpected) {

        // ACT
        Optional<SlotDisponibile> risultato = pianificazioneComponent.trovaPrimoSlotDisponibile(
                durataMediaPrestazioneTest,
                dataTest,
                oraAttualeTest,
                listaMediciTest,
                listaVisiteTest
        );

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( dataExpected, risultato.get().getData() );
        assertEquals( oraExpected, risultato.get().getOrario().toLocalTime() ); // converto risultato.orario da Date a LocalTime per avere eguaglianza nei tipi per la Assert
        assertEquals( medicoExpected.getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() ); // non riuscivo ad eguagliare gli oggetti, quindi controllo tramite id
        assertEquals( medicoExpected.getMatricola(), risultato.get().getMedico().getMatricola() ); // e tramite matricola
    }



    /// 1.E: oraAttuale not ammissibile in pomeriggio ==> considera il/i giorno/i successivo/i
    /// Se (oraAttuale+durataMedia+5min) > oraChiusuraPomeriggio
    ///     allora considera il giorno dopo di dataAttuale
    /// TODO:   N.B: io non so nulla sul giorno successivo (o sui giorni successivi)
    ///            perchè potrei avere già visite nel/nei giorno/i successivo/i o magari non averne, ma non posso saperlo
    ///             devo in qualche modo richiamare il metodo trovaPrimoSlotDisponibile() con una dataSuccessiva > dataAttuale  controllando:
    ///             Se listaVisiteGiornoSuccesivo = []
    ///                 allora oraSlot=07:05
    ///             Altrimenti:
    ///                 se c'è spazio nella listaVisiteMattino allora, se non sforo (oraUltimaVisita+durata+5min è ammissibile) accodo in listaVisiteMattino
    ///                 altrimenti considero il pomeriggio
    ///
    @Test
    public void testTrovaPrimoSlotDisponibile_ListaVisiteVuota_WithOraAttualeNotAmmissibileInPomeriggio() {

        // ARRANGE & ACT
        Optional<SlotDisponibile> risultato = arrangeAndAct(
            100.0, // durata
                dataVenerdi17Gennaio2025Test,
                LocalTime.of(20, 0), // la durata di test scelta + questo orario di test superano oraChiusuraPomeriggio
                medicoService.getAllMedici(),
                new ArrayList<Visita>(),

                dataLunedi20Gennaio2025Test, // Quello che so per certo (che voglio) è che passo al/ai giorno/i successivo/i
                LocalTime.of(5,0), // non lo so e non mi interessa in questo test specifico
                new Medico() // non lo so e non mi interessa in questo test specifico
        );

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( dataLunedi20Gennaio2025Test, risultato.get().getData() );
        // Non posso dir nulla a priori se nei giorni successivi ci saranno visite, quindi non posso asserire sugli orari

    }


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
    public void testTrovaPrimoSlotDisponibile_WithOraAttualeBeforeAperturaMattina_WithMattinoNotAmmissibileAndPomeriggioVuoto() {

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


        // ACT
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

        System.out.println(risultato.get());

        // ASSERT
        assertTrue(risultato.isPresent(), "Il risultato dovrebbe essere presente");
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getIdAnagrafica(), risultato.get().getMedico().getIdAnagrafica() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }





    /// #################################################################################
    /// #########################   3) DISPONIBILITÀ MEDICI   #########################
    /// #################################################################################



    /// 3.A
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = (v1, v2)
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 06:55 (< oraAperturaMattina)
    @Test
    public  void testTrovaPrimoSlotDisponibile_WithOneFreeMedico_WithOraAttualeBeforeOraAperturaMattina() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar.JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 6, 55  );
        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( LocalTime.of(7, 0).plusMinutes( PianificazioneComponent.pausaFromvisite ) ); // Mi aspetto 07:05
        Medico medicoExpected = listaMediciTest.get(2); // mi aspetto il medico 3

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }




    /// 3.B
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = (v1, v2)
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 07:10 (> oraAperturaMattina) && (< fineVisita.oraFine)
    ///
    /// in questo caso fineVisita vale 07:20 (è l'elemento minimo di mediciMap)
    @Test
    public  void testTrovaPrimoSlotDisponibile_WithOneFreeMedico_WithOraAttualeInMattina_BeforeOraFineMinima() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 7, 10  ); // Tengo fissa l'ora attuale ( simulo now() )
        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( PianificazioneComponent.pausaFromvisite ) ); // Mi aspetto 07:15
        Medico medicoExpected = listaMediciTest.get(2);

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }



    /// 3.C
    /// CASO IN CUI C'È UN MEDICO LIBERO
    /// listaVisite = (v1, v2)
    /// listaMedici = (m1, m2, m3) ==> m3 è libero
    /// oraAttualeTest = 07:30 (> oraAperturaMattina)
    @Test
    public  void testTrovaPrimoSlotDisponibile_WithOneFreeMedico_WithOraAttualeInMattina_AfterOraFineMinima() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 7, 30  ); // Tengo fissa l'ora attuale ( simulo now() )
        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiDueVisite(); // TODO: prendo solo 2 visite
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1, v2]
        // listaMedici = [m1, m2, m3]
        // mediciMap = [<1, {1, 07:20}>, <2, {2, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( PianificazioneComponent.pausaFromvisite ) ); // Mi aspetto 07:35
        Medico medicoExpected = listaMediciTest.get(2);

        SlotDisponibile slotDisponibileExpected =
                new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );
    }




    /// 3.D
    /// CASO IN CUI CI SONO 2 MEDICI LIBERI (In mattinata)
    /// listaVisite = (v1)
    /// listaMedici = (m1, m2, m3) ==> m2, ed m3 sono liberi
    /// oraAttualeTest = 07:30 (> oraAperturaMattina)
    ///
    /// mi aspetto che venga assegnato m2 !!
    @Test
    public void testTrovaPrimoSlotDisponibile_WithTwoFreeMedici() {

        // ARRANGE
        Double durataMediaPrestazioneTest = 90.0;
        Date dataTest = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
        LocalTime oraAttualeTest = LocalTime.of( 7, 30 );
        List<Visita> listaVisiteTest = datiVisiteTest.getListaDiUnaVisita(); // TODO: prendo solo 1 visita
        List<Medico> listaMediciTest = getAllDatiMediciTests(); // come dati di test mi servono anche i medici (sono != da quelli a DB)


        // listaVisite = [v1]
        // listaMedici = [m1, m2, m3] ==> m2 ed m3 sono liberi
        // mediciMap = [<1, {1, 07:20}>, <2, {0, 09:05}>, <3, {0, 09:05}]
        Date dataExpected = dataTest;
        Time oraExpected = Time.valueOf( oraAttualeTest.plusMinutes( PianificazioneComponent.pausaFromvisite ) );
        Medico medicoExpected = listaMediciTest.get(1); // mi aspetto m2

        SlotDisponibile slotDisponibileExpected = new SlotDisponibile( dataExpected, oraExpected , medicoExpected );

        // ACT
        Optional<SlotDisponibile> risultato =
                pianificazioneComponent.trovaPrimoSlotDisponibile(
                        durataMediaPrestazioneTest,
                        dataTest,
                        oraAttualeTest,
                        listaMediciTest,
                        listaVisiteTest
                );

        System.out.println(risultato.get());

        // ASSERT
        assertTrue( risultato.isPresent(), "Il risultato dovrebbe essere presente" );
        assertEquals( slotDisponibileExpected.getData(), risultato.get().getData() );
        assertEquals( slotDisponibileExpected.getOrario(), risultato.get().getOrario() );
        assertEquals( slotDisponibileExpected.getMedico().getMatricola(), risultato.get().getMedico().getMatricola() );

    }

}