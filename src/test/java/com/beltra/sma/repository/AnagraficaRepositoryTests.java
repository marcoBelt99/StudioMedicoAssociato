package com.beltra.sma.repository;

import com.beltra.sma.model.Anagrafica;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: sto cercando di usare Testcontainer!!
// TODO: decommenta sia questa che le altre, e prova ad inserire Testcontainer!!
//
//@Disabled
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource(properties = {
//        "spring.datasource.url=jdbc:tc:postgresql:14.18-alpine://sma_db"
//})
//public class AnagraficaRepositoryTests {
//
//    @Autowired
//    private AnagraficaRepository anagraficaRepository;
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    public void insertNewAnagraficaCanBeFound() {
//        Anagrafica anagrafica = new Anagrafica();
//        anagrafica.setCognome("Rosa");
//        anagrafica.setNome("Maria");
//        anagrafica.setDataNascita(new GregorianCalendar(1999, Calendar.FEBRUARY, 10).getTime());
//        anagrafica.setGenere("F");
//
//
//        Long anagraficaId = entityManager.persist(anagrafica).getIdAnagrafica();
//        // Flusho per sincronizzare i cambiamenti al contesto di persistenza nel DB
//        entityManager.persistAndFlush(anagrafica);
//        // Pulisco il contesto, quindi l'entita' non sara' fetchata dal cache LL1
//        entityManager.clear();
//
//
//        Optional<Anagrafica> savedAnagrafica = anagraficaRepository.findByIdAnagrafica( anagraficaId );
//
//        assertThat(savedAnagrafica).isPresent();
//        assertThat(savedAnagrafica.get().getCognome()).isNotBlank();
//        //Assertions.assertNotNull( savedAnagrafica );
//
//    }
//}
