package com.beltra.sma.service;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.repository.MedicoRepository;
import com.beltra.sma.repository.PrestazioneRepository;
import com.beltra.sma.repository.VisitaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.util.*;



@SpringBootTest
public class MedicoServiceTests {

    @Autowired
    MedicoRepository medicoRepository;



    @Autowired
    VisitaService visitaService;

    @Autowired
    private PrestazioneRepository prestazioneRepository;

    // TODO

}
