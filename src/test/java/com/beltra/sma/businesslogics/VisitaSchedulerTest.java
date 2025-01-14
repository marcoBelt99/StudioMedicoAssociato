package com.beltra.sma.businesslogics;

import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.repository.MedicoRepository;
import com.beltra.sma.repository.VisitaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class VisitaSchedulerTest {

    @Autowired
    private VisitaRepository visitaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        // Inserisco una visita con la relativa prenotazione
        jdbcTemplate.execute(
        """
            INSERT INTO visite (data_visita, ora, num_ambulatorio, id_anagrafica, id_prestazione)
            VALUES
            ('2025-02-20', '08:00:00', 5, 3, 3);
            
            INSERT INTO prenotazioni (data_prenotazione, effettuata, id_visita, id_anagrafica)
            VALUES
            ('2025-01-08', FALSE, 21, 5);
            """
        );


        jdbcTemplate.execute(
        """
            INSERT INTO visite (data_visita, ora, num_ambulatorio, id_anagrafica, id_prestazione)
            VALUES
            ('2025-02-20', '10:00:00', 5, 1, 5);
            
            INSERT INTO prenotazioni (data_prenotazione, effettuata, id_visita, id_anagrafica)
            VALUES
            ('2025-01-08', FALSE, 22, 7);
            """
        );



    }

    @Test
    void testFindNextAvailableSlotAndMedico() {
        // Recupera la lista delle visite ordinate per data e ora
        List<Visita> visite = visitaRepository.findAllByOrderByDataVisitaAscOraAsc();

        // Trova il primo slot disponibile
        Time nextAvailableTime = Time.valueOf("08:00:00");
        Date nextAvailableDate = visite.get(visite.size() - 1).getDataVisita();

        for (Visita visita : visite) {
            if (visita.getOra().after(nextAvailableTime)) {
                nextAvailableTime = visita.getOra();
            }
        }

        // Trova un medico libero
        List<Medico> medici = medicoRepository.findAll();
        Optional<Medico> medicoLibero = medici.stream()
                .filter(medico -> visite.stream()
                        .noneMatch(visita -> visita.getAnagrafica().equals(medico.getAnagrafica())))
                .findFirst();

        // Asserzioni
        assertNotNull(nextAvailableDate, "La prossima data disponibile non dovrebbe essere null.");
        assertNotNull(nextAvailableTime, "Il prossimo orario disponibile non dovrebbe essere null.");
        assertTrue(medicoLibero.isPresent(), "Dovrebbe esserci almeno un medico libero.");

        System.out.println("Prossima data disponibile: " + nextAvailableDate);
        System.out.println("Prossimo orario disponibile: " + nextAvailableTime);
        System.out.println("Medico libero: " + medicoLibero.get().getMatricola());
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELET FROM visite WHERE id_visita = 21");
        jdbcTemplate.execute("DELET FROM prenotazioni WHERE id_prenotazione = 21");
        jdbcTemplate.execute("DELET FROM visite WHERE id_visita = 22");
        jdbcTemplate.execute("DELET FROM prenotazioni WHERE id_prenotazione = 22");
    }
}
