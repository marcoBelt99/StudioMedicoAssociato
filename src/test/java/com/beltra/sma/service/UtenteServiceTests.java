package com.beltra.sma.service;

import com.beltra.sma.model.Ruolo;
import com.beltra.sma.model.Utente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UtenteServiceTests {

    @Autowired
    UtenteService utenteService;

    @Test
    public void getWelcome_MustReturn_o() {
        String username = "marcobeltra";
        String risultato = utenteService.getWelcome(username);

        assertEquals("o", risultato );
    }


    @Test
    public void getWelcome_MustReturn_a() {
        String username = "linamarkes";
        String risultato = utenteService.getWelcome(username);

        assertEquals("a", risultato);
    }

    @Test
    public void isAnonimo_ReturnsTrue() {

        String username = "USERNAME_IMPOSSIBILE_CHE_ESISTA";

        assertEquals( true, utenteService.isAnonimo( username ) );
    }


    @Test
    public void isAnonimo_ReturnsFalse_WithPaziente() {
        String username = "marcobeltra";

        assertEquals( false, utenteService.isAnonimo( username ) );
    }

    @Test
    public void isAnonimo_ReturnsFalse_WithMedico() {
        String username = "mario_rossi";

        assertEquals( false, utenteService.isAnonimo( username ) );
    }


    @Test
    public void isAnonimo_ReturnsFalse_WithRandomRole() {
        String username = "marcobeltra";

        Utente utente = utenteService.getUtenteByUsername(username);

        Set<Ruolo> ruoliUtente = utente.getRuoli();

        ruoliUtente.add( new Ruolo() );


        assertEquals( false, utenteService.isAnonimo( username ) );

    }

}
