package com.beltra.sma.service;

import com.beltra.sma.repository.PrestazioneRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// TODO: import mockito
import static org.mockito.Mockito.*;

@SpringBootTest
public class PrestazioneServiceTests {

    @Autowired
    private PrestazioneService prestazioneService;

    @Test
    public void getAllPrestazioniDisponibili() {
        prestazioneService.getAllPrestazioni().forEach(System.out::println);
    }



    @Test
    public void getAllVisiteOfPrestazioneOfPaziente() {

        // Non ho voglia di farmi un metodo nel service che fa uso del PrestazioneRepository
        // quindi faccio il mocking
        PrestazioneService prestazioneRepositoryMock = mock( PrestazioneService.class );


        //PrestazioneRepository prestazioneRepositorySpy = spy( prestazioneRepositoryMock );




    }
}
