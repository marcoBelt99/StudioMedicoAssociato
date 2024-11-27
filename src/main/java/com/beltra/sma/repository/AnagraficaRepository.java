package com.beltra.sma.repository;

import com.beltra.sma.domain.Anagrafica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnagraficaRepository extends JpaRepository<Anagrafica, Long> {

    //Anagrafica findById(String codiceFiscale);
}
