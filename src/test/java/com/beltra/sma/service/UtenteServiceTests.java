package com.beltra.sma.service;

import com.beltra.sma.exceptions.UtenteNotFoundException;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Ruolo;
import com.beltra.sma.model.Utente;
import com.beltra.sma.repository.UtenteRepository;
import org.codehaus.plexus.util.cli.Arg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UtenteServiceTests {


    private UtenteService utenteService;
    private UtenteRepository utenteRepository;

    @BeforeEach
    void setupService() {
        utenteRepository = mock(UtenteRepository.class);
        utenteService = new UtenteServiceImpl(utenteRepository);
    }


    /// #####################################
    /// TEST: getWelcome()
    /// #####################################

    private static Stream<Arguments> provideStringsForGetWelcome() {
        return Stream.of(
                Arguments.of("marcobeltra", "M", "o"), // Maschio ==> mi aspetto "o"
                Arguments.of("linamarkes", "F", "a") // Femmina ==> mi aspetto "a"
        );
    }

    @ParameterizedTest
    @MethodSource("provideStringsForGetWelcome")
    void getWelcome_Works(String usernameInput, String genereInput, String letteraExpected ) {

        Utente utente = new Utente();
        utente.setUsername(usernameInput);
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setGenere(genereInput);
        utente.setAnagrafica(anagrafica);

        when(utenteRepository.findByUsername(usernameInput)).thenReturn(utente); // mocko un layer inferiore rispetto al service.

        String risultato = utenteService.getWelcome(usernameInput);

        assertThat(risultato).isEqualTo(letteraExpected);

    }

    /// #####################################
    /// TEST: getUtenteByUsername()
    /// #####################################



    @Test
    void getUtenteByUsername_ReturnsUtente() {
        Utente utente = new Utente();
        when(utenteRepository.findByUsername(anyString())).thenReturn(utente);

        assertThat(utenteService.getUtenteByUsername(anyString())).isEqualTo(utente);
    }

    @Test
    void getUtenteByUsername_ThrowsUtenteNotFoundException() {

        when(utenteRepository.findByUsername(anyString())).thenReturn(null);

        assertThrows(UtenteNotFoundException.class, () -> utenteService.getUtenteByUsername(anyString()));
    }





/*
    @Test
    void isAnonimo_ReturnsTrue() {

        String username = "USERNAME_IMPOSSIBILE_CHE_ESISTA";

        assertEquals( true, utenteService.isAnonimo( username ) );
    }

 */


    // TODO: guardare come fare dei test di sicurezza
/*
    @Test
    void isAnonimo_ReturnsFalse_WithPaziente() {
        String username = "marcobeltra";

        assertEquals( false, utenteService.isAnonimo( username ) );
    }

    @Test
    void isAnonimo_ReturnsFalse_WithMedico() {
        String username = "mario_rossi";

        assertEquals( false, utenteService.isAnonimo( username ) );
    }


    @Test
    void isAnonimo_ReturnsFalse_WithRandomRole() {
        String username = "marcobeltra";

        Utente utente = utenteService.getUtenteByUsername(username);

        Set<Ruolo> ruoliUtente = utente.getRuoli();

        ruoliUtente.add( new Ruolo() );


        assertEquals( false, utenteService.isAnonimo( username ) );

    }

 */

}
