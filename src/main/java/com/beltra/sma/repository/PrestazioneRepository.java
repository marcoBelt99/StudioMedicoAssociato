package com.beltra.sma.repository;

import com.beltra.sma.model.Prestazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestazioneRepository extends JpaRepository<Prestazione, Long> {

    //List<Prestazione> findAll();
}
