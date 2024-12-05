package com.beltra.sma.repository;

import com.beltra.sma.dto.VisitaPrenotataDTO;

import com.beltra.sma.model.Visita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class VisitaRepositoryTests {

    @Autowired
    private VisitaRepository visitaRepository;
    @Autowired
    private UtenteRepository utenteRepository;


    private final String username = "marcobeltra";
    private int totaleVisitePaziente ;


    @BeforeEach
    public void setup() {
        Long idAnagrafica = utenteRepository
                .findByUsername(username)
                .getAnagrafica()
                .getIdAnagrafica();

        totaleVisitePaziente = visitaRepository
                .findAllVisitePazienteByAnagraficaPaziente(idAnagrafica)
                .size();
    }


    @Test
    public void findAllVisiteOrderByDataVisitaDesc_ReturnsListOfVisitaDTO() {

        List<VisitaPrenotataDTO> listaVisiteDTO =
                visitaRepository.findAllVisiteOrderByDataVisitaDesc();

        if(!listaVisiteDTO.isEmpty())
            listaVisiteDTO.forEach(System.out::println);

        assertNotNull( listaVisiteDTO );
    }


    @Test
    public void findAllVisitePrenotateAndNotEffettuateByUsernamePaziente_ReturnsListOfVisitaPrenotataDTO() {

        List<VisitaPrenotataDTO> listaVisitePrenotateAndNotEffettuateDTO =
                visitaRepository.findAllVisitePrenotateByUsernamePaziente(username, false);

        if(!listaVisitePrenotateAndNotEffettuateDTO.isEmpty())
            listaVisitePrenotateAndNotEffettuateDTO.forEach(System.out::println);

        int totaleVisiteNotEffettuatePaziente = listaVisitePrenotateAndNotEffettuateDTO.size();


        // Totale visite - totale visite effettuate = totale visite non effettuate
        assertEquals(totaleVisitePaziente - (visitaRepository.findAllVisitePrenotateByUsernamePaziente(username, true).size() ),
                totaleVisiteNotEffettuatePaziente);

    }



    @Test
    public void findAllVisitePrenotateAndEffettuateByUsernamePaziente_ReturnsListOfVisitaPrenotataDTO() {

        List<VisitaPrenotataDTO> listaVisitePrenotateAndEffettuateDTO =
                visitaRepository.findAllVisitePrenotateByUsernamePaziente(username, true);

        if(!listaVisitePrenotateAndEffettuateDTO.isEmpty())
            listaVisitePrenotateAndEffettuateDTO.forEach(System.out::println);

        int totaleVisiteEffettuatePaziente = listaVisitePrenotateAndEffettuateDTO.size();

        // Totale visite - totale visite effettuate = totale visite non effettuate
        assertEquals(totaleVisitePaziente - (visitaRepository.findAllVisitePrenotateByUsernamePaziente(username, false).size() ),
                          totaleVisiteEffettuatePaziente);
    }



    @Test
    public void findAllVisitePazienteByAnagraficaPaziente_MustReturnListOfVisita() {

        String username = "marcobeltra";
        Long idAnagraficaPaziente =
                 utenteRepository.findByUsername( username ).getAnagrafica().getIdAnagrafica();

         List<Visita> listaVisite =
                 visitaRepository.findAllVisitePazienteByAnagraficaPaziente(idAnagraficaPaziente);

        assertNotNull( listaVisite );

        System.out.println( String.format("\nLista delle visite per il paziente: %s\n", username) );
        listaVisite.forEach(System.out::println);

    }
}
