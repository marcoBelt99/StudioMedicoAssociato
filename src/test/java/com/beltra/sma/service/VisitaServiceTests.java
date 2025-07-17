package com.beltra.sma.service;

import com.beltra.sma.dto.AppuntamentoSettimanaleMedicoDTO;

import com.beltra.sma.repository.MedicoRepository;
import com.beltra.sma.repository.PrenotazioneRepository;
import com.beltra.sma.repository.VisitaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.Mockito.mock;

@Disabled
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VisitaServiceTests {

    private PrenotazioneService prenotazioneService;
    private VisitaRepository visitaRepository;
    private VisitaService visitaService;

    @BeforeAll
    void setupService() {
        prenotazioneService = mock(PrenotazioneService.class);
        visitaRepository = mock(VisitaRepository.class);
        visitaService = new VisitaServiceImpl(visitaRepository, prenotazioneService);
    }

    @Disabled
    @DisplayName("Verifica che ")
    @Test
    void getVisitePrenotateSettimana_ShouldReturAllVisiteBetweenMondayAndFriday() {
        ;
    }


    @Test
    void getAppuntamentiSettimanaliWorks() {

        Date inizioSettimana = new GregorianCalendar(2025, Calendar.JUNE, 23).getTime();
        Date fineSettimana = new GregorianCalendar(2025, Calendar.JUNE, 29).getTime();
        List<AppuntamentoSettimanaleMedicoDTO> appuntamentiSettimanali = visitaService
            .getAppuntamentiSettimanaliMedicoLista("mario_rossi", inizioSettimana, fineSettimana);

        // Assert ???

    }

}
