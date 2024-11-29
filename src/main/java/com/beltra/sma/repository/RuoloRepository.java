package com.beltra.sma.repository;

import com.beltra.sma.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuoloRepository extends JpaRepository<Ruolo, Long> {


    Optional<Ruolo> findByTipo(String tipoRuolo);
}
