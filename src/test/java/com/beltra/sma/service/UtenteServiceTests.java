package com.beltra.sma.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UtenteServiceTests {

    @Autowired
    UtenteService utenteService;

    @Test
    public void getWelcome_MustReturn_o() {
        String username = "marcobeltra";
        String risultato = utenteService.getWelcome(username);

        assertEquals(risultato, "o");
    }


    @Test
    public void getWelcome_MustReturn_a() {
        String username = "linamarkes";
        String risultato = utenteService.getWelcome(username);

        assertEquals(risultato, "a");
    }


}
