package com.beltra.sma.service;

import com.pholser.junit.quickcheck.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VisitaServiceTests {

    @Autowired
    private VisitaService visitaService;




    @DisplayName("Verifica che ")
    @Test
    void getVisitePrenotateSettimana_ShouldReturAllVisiteBetweenMondayAndFriday() {
        ;
    }

}
