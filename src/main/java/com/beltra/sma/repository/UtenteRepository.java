package com.beltra.sma.repository;

import com.beltra.sma.domain.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UtenteRepository extends JpaRepository<Utente, String> {

    /** Query method che restituisce l'utente dato il suo username */
    Utente findByUsername(String username);




}
