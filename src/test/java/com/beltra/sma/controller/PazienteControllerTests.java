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


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PazienteController.class)
public class PazienteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    VisitaService visitaService;


    @Test
    @WithMockUser(username = "marcobeltra", roles = {"PAZIENTE"}) /// solo l'utente di ruolo "PAZIENTE" può chiamare questo endpoint
    void getVisitePrenotateAndNotEffettuate_Works() throws Exception {

        String username = "marcobeltra";

        when( visitaService.getAllVisitePrenotateAndNotEffettuateByUsernamePaziente( username ) )
                .thenReturn( new ArrayList<>() );

        this.mockMvc
                .perform(
                        get("/paziente/visite")
                        .param("effettuata", "false")
                        .cookie(new Cookie("user-id", "marcobeltra")) // Aggiunta del cookie
                        .contentType(MediaType.TEXT_HTML)
                )

                //.andDo( print() )
                .andExpect( status().isOk() )
                .andExpect( model().attributeExists("visitePrenotate"))
                .andExpect( model().attribute("titolo", "Visite Prenotate e Non Effettuate"))
                // Verifica che nell'HTML restituito ci sia un frammento atteso
                /* Questa verifica è utile quando voglio:
                    - Accertarmi che il contenuto restituito dal controller contenga parti specifiche (come un titolo, un messaggio o un dato chiave).
                    - Validare che il rendering del template Thymeleaf includa determinati elementi previsti
                .*/
                // Verifica che il contenuto sia di tipo HTML
                .andExpect( content().contentTypeCompatibleWith(MediaType.TEXT_HTML) )
                .andExpect( content().string(org.hamcrest.Matchers.containsString("Prenotate e Non Effettuate")));

        verify( visitaService ).getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username);
    }


    @Test
    @WithMockUser(username = "marcobeltra", roles = {"PAZIENTE"}) // TODO: solo l'utente di ruolo "PAZIENTE" può chiamare questo endpoint
    void getVisitePrenotateAndEffettuate_Works() throws Exception {

        String username = "marcobeltra";

        when( visitaService.getAllVisitePrenotateAndEffettuateByUsernamePaziente( username ) )
                .thenReturn( new ArrayList<>() );

        this.mockMvc
                .perform(
                    get("/paziente/visite")
                    .param("effettuata", "true")
                    .cookie(new Cookie("user-id", "marcobeltra"))
                    .contentType(MediaType.TEXT_HTML)
                )

                //.andDo(print())
                .andExpect( status().isOk() )
                .andExpect( model().attributeExists("visitePrenotate"))
                .andExpect( model().attribute("titolo", "Visite Prenotate ed Effettuate"))
                // Verifica che il contenuto sia di tipo HTML
                .andExpect( content().contentTypeCompatibleWith(MediaType.TEXT_HTML) )
                .andExpect( content().string(org.hamcrest.Matchers.containsString("Prenotate ed Effettuate")));



        verify( visitaService ).getAllVisitePrenotateAndEffettuateByUsernamePaziente( username );
    }


    @Test
    @WithAnonymousUser
    void getVisitePrenotateAndNotEffettuate_AnonimoIsNotAuthorized() throws Exception {
        mockMvc.perform(
                    get("/paziente/visite")
                    .param("effettuata", "false")
                    // Mi aspetto HTML come risposta
                    .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithAnonymousUser
    void getVisitePrenotateAndEffettuate_AnonimoIsNotAuthorized() throws Exception {
        mockMvc
         .perform(get("/paziente/visite")
                 .param("effettuata", "true"))
                .andExpect(status().isUnauthorized());
    }

}
