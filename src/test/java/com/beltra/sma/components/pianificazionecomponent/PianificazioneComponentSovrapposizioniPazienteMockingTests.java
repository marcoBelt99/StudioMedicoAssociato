package com.beltra.sma.components.pianificazionecomponent;

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl;
import com.beltra.sma.components.PianificazioneComponentImpl;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PianificazioneComponentSovrapposizioniPazienteMockingTests extends PianificazioneComponentTests {

    @Mock
    private VisitaService visitaService;

//    @Mock
    private CalcolatoreAmmissibilitaComponentImpl calcolatore;

//    @InjectMocks
//    private PianificazioneComponentImpl pianificazioneComponent;

    private PianificazioneComponentImpl pianificazioneComponent;

    private final String username = "utenteTest";

    @BeforeEach
    void setUp() {
    calcolatore = new CalcolatoreAmmissibilitaComponentImpl();
    pianificazioneComponent = new PianificazioneComponentImpl(visitaService, calcolatore);
    }

    /*
    @Test
    void test_getRightOraDiPartenza_se_utente_ha_visite_oggi() {

        // Sono sempre lo stesso utente, quindi la prossima visita inizierà quando finisce l'unica che ho già prenotato per oggi (nel futuro).
        LocalTime oraInizioProssimaVisitaExpected = LocalTime.of(7, 25);


        // Faccio finta di essere partito da dataVenerdi17Gennaio2025Test, e che sono finito nel successivo giorno ammissibile
        // ossia a dataLunedi20Gennaio2025Test
        List<Visita> visitePrenotateOggi = new DatiVisiteTest().getListaVisiteFullFromCSV("src/test/resources/visiteMattinaFull_Caso_Sovrapposizione.csv");

        List<VisitaPrenotataDTO> visitePrenotateDTO = visitePrenotateOggi.stream()
                 .map( v -> new VisitaPrenotataDTO(null, v.getDataVisita(), v.getOra(), v.getNumAmbulatorio(),
                         null, null, v.getPrestazione().getTitolo(), v.getPrestazione().getDurataMedia()))
                 .toList();

        // Questo deve quindi ritornarmi true
        when(visitaService.utenteOggiHaGiaPrenotatoAlmenoUnaVisita(username, dataLunedi20Gennaio2025Test)).thenReturn(true);
        when(visitaService.getAllVisitePrenotateAndNotEffettuateByUsernamePazienteByData(username, dataLunedi20Gennaio2025Test)).thenReturn(visitePrenotateDTO);

        // ACT
        LocalTime risultato = pianificazioneComponent.getRightOraDiPartenza(username, dataLunedi20Gennaio2025Test);

        System.out.println(visitePrenotateDTO.get(0));

        assertEquals(oraInizioProssimaVisitaExpected ,risultato);
    }

     */

    @Test
    void test_getRightOraDiPartenza_se_stesso_giorno_e_nessuna_visita() {

        // Sono sempre lo stesso utente, per oggi (nel futuro) ho prenotato 0 visite, sto guardando.
        LocalTime oraInizioProssimaVisitaExpected = Parameters.orarioAperturaMattina;


        // Faccio finta di essere partito da dataVenerdi17Gennaio2025Test, e che sono finito nel successivo giorno ammissibile
        // ossia a dataLunedi20Gennaio2025Test


        // Questo deve quindi ritornarmi false
        when(visitaService.utenteOggiHaGiaPrenotatoAlmenoUnaVisita(username, dataLunedi20Gennaio2025Test)).thenReturn(false);

        LocalTime risultato = pianificazioneComponent.getRightOraDiPartenza(username, dataLunedi20Gennaio2025Test);

        assertEquals(oraInizioProssimaVisitaExpected ,risultato);
    }

    /*

    @Test
    void test_getRightOraDiPartenza_se_giorno_differente_e_nessuna_visita() {
        Date giornoFuturo = new Date(System.currentTimeMillis() + 86400000); // +1 giorno
        LocalTime orarioApertura = LocalTime.of(8, 0);

        //pianificazioneComponent.orarioAperturaMattina = orarioApertura;

        when(visitaService.utenteOggiHaGiaPrenotatoAlmenoUnaVisita(username, giornoFuturo)).thenReturn(false);
        //doReturn(false).when(calcolatore.isStessoGiorno).test(any(Date.class), eq(giornoFuturo));

        when(calcolatore.isStessoGiorno.test(any(Date.class), eq(giornoFuturo))).thenReturn(false);

        LocalTime risultato = pianificazioneComponent.getRightOraDiPartenza(username, giornoFuturo);

        assertEquals(orarioApertura, risultato);
    }

     */
}
