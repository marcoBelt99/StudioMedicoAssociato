package com.beltra.sma.datastructures;



import com.beltra.sma.data.DatiMediciTest;
import com.beltra.sma.data.DatiPrestazioniTest;
import com.beltra.sma.data.DatiVisiteTest;
import com.beltra.sma.groovy.datastructures.CodaMediciDisponibiliGroovyImpl;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.FineVisita;
import com.beltra.sma.utils.Parameters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import java.sql.Time;
import java.time.LocalTime;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/** Con questa classe voglio simulare quello che accade sforando gli orari ammissibili, usando una lista di visite fisse
 *  fasulle rappresentanti le visite del successivo giorno ammissibile.
 *  */
//@ExtendWith(MockitoExtension.class) // pare non serva. Se lo usassi caricherei contesto (memoria) per niente
public class CodaMediciDisponibiliMockingTests {

    List<Visita> listaVisite = new ArrayList<>();
    List<Medico> listaMedici = new ArrayList<>();
    List<Prestazione> listaPrestazioni = new ArrayList<>();
    DatiVisiteTest datiVisiteTest = new DatiVisiteTest();

    Medico m1;
    Medico m2;
    Medico m3;


    @BeforeEach
    void inizializzaDati() {
        listaVisite = getAllVisiteByData();
        listaMedici = getAllMediciTests();
        listaPrestazioni = getAllPrestazioniTests();

        m1 = listaMedici.get(0);
        m2 = listaMedici.get(1);
        m3 = listaMedici.get(2);
    }


    /** Usare un mock di visitaService (con Mockito), e dire al mock cosa restituire quando viene chiamato con un
     *  certo parametro. */


    @Test
    void testGetPrimoMedicoDisponibile_MustReturn_M3_Mockito() {

        /// ARRANGE
        LocalTime oraAttuale = LocalTime.of(22, 0);
        Double durataMedia = 180.0;
        List<Visita> listaVisiteFittizie = getListaVisiteFinteNextGiornoAmmissibile(); // lista da usare per falsificare (da usare nello spy)

        /// ACT
        // mock della dipendenza interna a CodaMediciDisponibiliTests legata al VisitaService.
        VisitaService visitaService = mock(VisitaService.class);

        // questa istruzione posso provare a spostarla dentro il @BeforeEach
        CodaMediciDisponibiliGroovyImpl coda = new CodaMediciDisponibiliGroovyImpl(listaMedici, listaVisite, oraAttuale, durataMedia, visitaService);

        // spy della classe di test, perchè devo "falsificare" il suo metodo getListaVisiteNextGiornoAmmissibile
        CodaMediciDisponibiliGroovyImpl codaSpy = spy(coda);

        doReturn( listaVisiteFittizie ).when(codaSpy).getListaVisiteNextGiornoAmmissibile();

        Map.Entry<Medico, FineVisita> primoMedicoDisponibile = codaSpy.getPrimoMedicoDisponibile(durataMedia);


        /// ASSERT
        assertNotNull(primoMedicoDisponibile);

        // In questo caso mi devi prendere M3!
        assertEquals(m3.getMatricola(), primoMedicoDisponibile.getKey().getMatricola());

        // Mi aspetto che il primo medico disponibile si libererà alle 10:05. (Poi la visita futura inizierà dopo 5 minuti, ossia alle 10:10).
        assertEquals(Time.valueOf(LocalTime.of(10,5)), primoMedicoDisponibile.getValue().getOraFine());

    }


    // #################################################################
    // OTTENIMENTO DATI
    // #################################################################

    protected List<Visita> getAllVisiteByData() {
        return datiVisiteTest.getListaVisiteFullFromCSV();
    }

    /** Lista di visite fasulle, necessarie per lo spy del metodo getListaVisiteByData(). */
    private List<Visita> getListaVisiteFinteNextGiornoAmmissibile() {
        List<Visita> listaVisiteToReturn = new ArrayList<>();

        Prestazione p1 = getAllPrestazioniTests().get(3);
        Prestazione p2 = getAllPrestazioniTests().get(6);

        Visita v1 = new Visita();
        Visita v2 = new Visita();

        v1.setIdVisita(11L);
        v1.setPrestazione(p1);
        v1.setOra(Time.valueOf( Parameters.orarioAperturaMattina.plusMinutes(Parameters.pausaFromVisite) ));

        v2.setIdVisita(22L);
        v2.setPrestazione(p2);
        v2.setOra(Time.valueOf( Parameters.orarioAperturaMattina.plusMinutes(Parameters.pausaFromVisite) ));

        listaVisiteToReturn.add(v1);
        listaVisiteToReturn.add(v2);

        return listaVisiteToReturn;
    }


    protected List<Medico> getAllMediciTests() {
        DatiMediciTest datiMediciTest = new DatiMediciTest();
        return datiMediciTest.getDatiTest();
    }

    protected List<Prestazione> getAllPrestazioniTests() {
        DatiPrestazioniTest datiPrestazioniTest = new DatiPrestazioniTest();
        return datiPrestazioniTest.getDatiTest();
    }


}