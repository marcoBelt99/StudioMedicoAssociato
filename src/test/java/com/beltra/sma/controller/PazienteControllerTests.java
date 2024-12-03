package com.beltra.sma.controller;

import com.beltra.sma.service.VisitaService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PazienteController.class)
public class PazienteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    VisitaService visitaService;


    @Test
    @WithMockUser(username = "marcobeltra", roles = {"PAZIENTE"}) // TODO: solo l'utente di ruolo "PAZIENTE" può chiamare questo endpoint
    void getVisitePrenotateAndNotEffettuate() throws Exception {

        String username = "marcobeltra";

        when( visitaService.getAllVisitePrenotateAndNotEffettuateByUsernamePaziente( username ) )
                .thenReturn( new ArrayList<>() );

        this.mockMvc
                .perform(
                        get("/paziente/visite")
                        .param("effettuata", "false")
                        .cookie(new Cookie("user-id", "marcobeltra")) // Aggiunta del cookie
                )
                .andDo( print() )
                .andExpect( status().isOk() )
                .andExpect( model().attributeExists("visitePrenotate"))
                .andExpect( model().attribute("titolo", "Visite Prenotate e Non Effettuate"));

        verify( visitaService ).getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username);
    }


    @Test
    @WithMockUser(username = "marcobeltra", roles = {"PAZIENTE"}) // TODO: solo l'utente di ruolo "PAZIENTE" può chiamare questo endpoint
    void getVisitePrenotateAndEffettuate() throws Exception {

        String username = "marcobeltra";

        when( visitaService.getAllVisitePrenotateAndEffettuateByUsernamePaziente( username ) )
                .thenReturn( new ArrayList<>() );

        this.mockMvc
                .perform(
                        get("/paziente/visite")
                                .param("effettuata", "true")
                                .cookie(new Cookie("user-id", "marcobeltra")) // Aggiunta del cookie
                )
                .andDo(print())
                .andExpect( status().isOk() )
                .andExpect( model().attributeExists("visitePrenotate"))
                .andExpect( model().attribute("titolo", "Visite Prenotate ed Effettuate"));

        verify( visitaService ).getAllVisitePrenotateAndEffettuateByUsernamePaziente( username );
    }
}
