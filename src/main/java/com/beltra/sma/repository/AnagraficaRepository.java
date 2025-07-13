package com.beltra.sma.repository;

import com.beltra.sma.model.Anagrafica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AnagraficaRepository extends JpaRepository<Anagrafica, Long> {


    Optional<Anagrafica> findByIdAnagrafica(Long idAnagrafica);
}
