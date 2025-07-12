package com.beltra.sma.controller;

import com.beltra.sma.service.VisitaService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


@WebMvcTest(MedicoController.class)
public class MedicoControllerTests {

    @Autowired
    private MockMvc mockMvc;


    /** Con la @MockBean creo e inietto un mock per il mio service
     * (se non lo faccio, il contesto dell'applicazione non può essere avviato)
     * e ne imposto il comportamento usando Mockito.
     * */
    @MockBean
    private VisitaService visitaService;


    @Test
    @WithMockUser(username = "mario_rossi", roles = {"MEDICO"}) // TODO: solo l'utente di ruolo "MEDICO" può chiamare questo endpoint
    void getElencoCronologicoVisite_MustReturnListOfAllVisiteDTO() throws Exception {

        when( visitaService.getAllVisite() )
                .thenReturn( new ArrayList<>() );

        this.mockMvc
                .perform(
                        // Simulo la chiamata GET all'endpoint
                        get("/medico/visite/all")
                        // Aggiunta del cookie
                        .cookie(new Cookie("user-id", "mario_rossi"))
                        // Mi aspetto HTML come risposta
                        .contentType(MediaType.TEXT_HTML))
                //.andDo(print())

                // Verifico che lo stato della risposta sia 200 ok
                .andExpect( status().isOk() )
                .andExpect( model().attributeExists("visite"))
                .andExpect( model().attributeExists("titolo"))
                .andExpect( model().attribute("titolo", "Elenco Cronologico delle Visite Usufruite da Ciascun Paziente."))
                // Verifico che il contenuto sia di tipo HTML
                .andExpect( content().contentTypeCompatibleWith(MediaType.TEXT_HTML) )
                // Verifica che nell'HTML restituito ci sia un frammento atteso
                /* Questa verifica è utile quando voglio:
                    - Accertarmi che il contenuto restituito dal controller contenga parti specifiche (come un titolo, un messaggio o un dato chiave).
                    - Validare che il rendering del template Thymeleaf includa determinati elementi previsti
                .*/
                .andExpect( content().string(org.hamcrest.Matchers.containsString("Visite Usufruite")))
        ;

        // con la verify() verifico se c'e' stata effettivamente un'interazione con il metodo dell'oggetto che sto mockando
        // cioè, verifico che il metodo del service sia stato chiamato
        verify( visitaService ).getAllVisite();
    }


    @Test
    @WithAnonymousUser
    void getElencoCronologicoVisite_AnonimoIsNotAuthorized() throws Exception {
        mockMvc
                .perform(
                        get("/medico/visite/all")

                )

                .andExpect(status().isUnauthorized());
    }

    /// N.B.: per fare il seguente test (testo che anche il paziente nons ia autorizzato, dovrei fare uso
    /// di @SpringBootTest)
    /**
    @Test
    @WithMockUser(username = "marcobeltra", roles = "PAZIENTE")
    void getElencoCronologicoVisite_PazienteIsUnauthorized() throws Exception {
        mockMvc
                .perform(get("/medico/visite/all"))
                .andExpect(status().isForbidden());
    }
    */


}
