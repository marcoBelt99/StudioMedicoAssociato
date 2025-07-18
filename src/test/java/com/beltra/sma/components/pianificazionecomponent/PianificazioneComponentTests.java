package com.beltra.sma.components.pianificazionecomponent;


import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl;
import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.components.PianificazioneComponentImpl;
import com.beltra.sma.data.DatiMediciTest;
import com.beltra.sma.data.DatiPrestazioniTest;
import com.beltra.sma.data.DatiTest;
import com.beltra.sma.data.DatiVisiteTest;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.MedicoService;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.SlotDisponibile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;


import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

import static org.mockito.Mockito.mock;


@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Serve per evitare di mettere static il metodo di providing dei dati di test che servirà poi al metodo di test vero e proprio
//@SpringBootTest
public class PianificazioneComponentTests {

//    @Autowired
    protected PianificazioneComponent pianificazioneComponent;

//    @Autowired
    protected MedicoService medicoService;

    protected VisitaService visitaService; // nuovo

    protected CalcolatoreAmmissibilitaComponentImpl calcolatoreAmmissibilitaComponent; // nuovo

    protected DatiVisiteTest datiVisiteTest = new DatiVisiteTest();


    /// PARAMETRI DI TEST
    protected Double durataMediaPrestazioneTest;
    protected Date dataTest;
    protected Date dataVenerdi17Gennaio2025Test = new GregorianCalendar(2025, Calendar. JANUARY, 17 ).getTime();
    protected Date dataSabato18Gennaio2025Test = new GregorianCalendar(2025, Calendar. JANUARY, 18 ).getTime();
    protected Date dataDomenica19Gennaio2025Test = new GregorianCalendar(2025, Calendar. JANUARY, 19 ).getTime();
    protected Date dataLunedi20Gennaio2025Test = new GregorianCalendar(2025, Calendar. JANUARY, 20 ).getTime();
    protected LocalTime oraAttualeTest;
    protected List<Medico> listaMediciTest;
    protected List<Visita> listaVisiteTest;
    protected SlotDisponibile slotDisponibileExpected = new SlotDisponibile();


    @BeforeEach
    void init() {
        medicoService = mock(MedicoService.class);
        visitaService = mock(VisitaService.class);
        calcolatoreAmmissibilitaComponent = new CalcolatoreAmmissibilitaComponentImpl();

        pianificazioneComponent = new PianificazioneComponentImpl(visitaService, calcolatoreAmmissibilitaComponent);
    }


/// ###################################
/// SEZIONE GENERAZIONE DATI DI TEST
/// ###################################


    /**  In questo metodo inserisco i dati di test per i vari casi di test specifici.
     *   Viene usato solo nelle suite di test con lista visite non vuota. */
    protected List<Visita> getAllVisiteByData() {
        DatiVisiteTest datiVisiteTest = new DatiVisiteTest(); // todo: forse è da togliere

        return datiVisiteTest
                .getListaVisiteTest()
                .stream()
                .filter( v -> v.getDataVisita().after( DatiTest.dataTest_16Gennaio2025 ) )
                .toList();
    }

    protected List<Medico> getAllDatiMediciTests() {
        DatiMediciTest datiMediciTest = new DatiMediciTest();
        return datiMediciTest.getDatiTest();
    }

    protected List<Prestazione> getAllPrestazioniTests() {
        DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
        return datiPrestazioniTest.getDatiTest();
    }




/// ###################################################
/// METODI DI BASE
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
    protected Optional<SlotDisponibile> arrangeAndAct(
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
        return pianificazioneComponent.trovaSlotDisponibile(
                    durataMediaPrestazioneTest,
                    dataTest,
                    oraAttualeTest,
                    listaMediciTest,
                    listaVisiteTest
        );

    }

}