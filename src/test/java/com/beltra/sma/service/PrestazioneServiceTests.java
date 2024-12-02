package com.beltra.sma.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PrestazioneServiceTests {

    @Autowired
    private PrestazioneService prestazioneService;

    @Test
    public void getAllPrestazioni() {
        prestazioneService.getAllPrestazioni().forEach(System.out::println);

    }
}
