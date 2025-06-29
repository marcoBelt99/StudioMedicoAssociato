package com.beltra.sma.service;

import com.beltra.sma.dto.AppuntamentiSettimanaliMedicoDTO;
import com.pholser.junit.quickcheck.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@SpringBootTest
public class VisitaServiceTests {

    @Autowired
    private VisitaService visitaService;




    @DisplayName("Verifica che ")
    @Test
    void getVisitePrenotateSettimana_ShouldReturAllVisiteBetweenMondayAndFriday() {
        ;
    }


    @Test
    void getAppuntamentiSettimanaliWorks() {

        Date inizioSettimana = new GregorianCalendar(2025, Calendar.JUNE, 23).getTime();
        Date fineSettimana = new GregorianCalendar(2025, Calendar.JUNE, 29).getTime();
        List<AppuntamentiSettimanaliMedicoDTO> appuntamentiSettimanali = visitaService
            .getAppuntamentiSettimanaliMedicoLista("mario_rossi", inizioSettimana, fineSettimana);


    }

}
