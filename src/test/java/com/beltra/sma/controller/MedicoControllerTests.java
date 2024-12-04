package com.beltra.sma.controller;

import com.beltra.sma.service.VisitaService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
                .perform(get("/medico/visite/all"))
                //.andDo(print())
                .andExpect( status().isOk() );

        // con la verify() verifico se c'e' stata effettivamente un'iterazione con il metodo dell'oggetto che sto mockando
        // cioè, verifico che il metodo del service sia stato chiamato
        verify( visitaService ).getAllVisite();
    }


}
