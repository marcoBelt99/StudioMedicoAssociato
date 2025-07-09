package com.beltra.sma.service;



//@SpringBootTest

import com.beltra.sma.repository.MedicoRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MedicoServiceTests {


    private MedicoRepository medicoRepository;
    private MedicoService medicoService;

    @BeforeAll
    void setupService() {
        medicoRepository = mock(MedicoRepository.class);
        medicoService = new MedicoServiceImpl(medicoRepository);
    }

    @Test
    void getAllMedici_isNotNull() {
        assertThat(medicoService.getAllMedici()).isNotNull();
    }


}
