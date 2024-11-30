package com.beltra.sma.repository;

import com.beltra.sma.dto.VisitaDTO;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class VisitaRepositoryTests {

    @Autowired
    private VisitaRepository visitaRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    @Test
    public void findAllVisiteOrderByDataVisitaDesc_ReturnsListOfVisitaDTO() {

        List<VisitaDTO> listaVisiteDTO =  visitaRepository.findAllVisiteOrderByDataVisitaDesc();

        if(!listaVisiteDTO.isEmpty()){
            listaVisiteDTO.forEach(System.out::println);
        }

        Assertions.assertNotNull( listaVisiteDTO );

    }


    @Test
    public void findAllVisitePrenotateAndNotEffettuateByUsernamePaziente_ReturnsListOfVisitaDTO_NotEmpty() {

        String username = "marcobeltra";

        List<VisitaDTO> listaVisitePrenotateAndNotEffettuateDTO = visitaRepository.findAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username);

        if(!listaVisitePrenotateAndNotEffettuateDTO.isEmpty()){
            listaVisitePrenotateAndNotEffettuateDTO.forEach(System.out::println);
        }

        listaVisitePrenotateAndNotEffettuateDTO.forEach(Assertions::assertNotNull);

        Assertions.assertNotEquals(0, listaVisitePrenotateAndNotEffettuateDTO.size());

    }
}
