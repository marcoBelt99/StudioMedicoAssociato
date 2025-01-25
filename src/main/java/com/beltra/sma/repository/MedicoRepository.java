package com.beltra.sma.repository;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {


    /** Trova tutti i medici che in una determinata fascia oraria non sono occupati in qualche visita. */
    @Query("SELECT m FROM Medico m WHERE m.anagrafica.idAnagrafica NOT IN " +
            "(SELECT v.anagrafica.idAnagrafica FROM Visita v WHERE v.dataVisita = :data AND v.ora BETWEEN :startTime AND :endTime)")
    List<Medico> findMediciDisponibili(@Param("data") Date data,
                                       @Param("startTime") Time startTime,
                                       @Param("endTime") Time endTime);


    Medico findByAnagrafica(Anagrafica anagrafica);
}
