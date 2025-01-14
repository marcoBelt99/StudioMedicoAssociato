package com.beltra.sma.repository;

import com.beltra.sma.model.Prestazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrestazioneRepository extends JpaRepository<Prestazione, Long> {

    //List<Prestazione> findAll();

    Optional<Prestazione> findByTitolo(String titolo);
}
