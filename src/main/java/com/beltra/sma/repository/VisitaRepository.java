package com.beltra.sma.repository;

import com.beltra.sma.dto.VisitaDTO;
import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitaRepository extends JpaRepository<Visita, Long> {

    /** TODO: Query Method.
     * <br>
     *  Elenco delle visite prenotate ed effettuate
     * */
    @Query(
        """
        SELECT new com.beltra.sma.dto.VisitaDTO(
            pren.dataPrenotazione,
            vis.dataVisita, vis.ora, vis.numAmbulatorio,
            anag.nome, anag.cognome,
            prest.titolo
        )
            FROM Visita vis, Prenotazione pren, Prestazione prest, Anagrafica anag
                WHERE vis.idVisita = pren.visita.idVisita
                AND pren.anagrafica.idAnagrafica = anag.idAnagrafica
                AND vis.prestazione.idPrestazione = prest.idPrestazione
        ORDER BY vis.dataVisita DESC
            """)
    List<VisitaDTO> findAllVisiteOrderByDataVisitaDesc();


    /** Per l'utente paziente: elenco delle visite prenotate e non effettuate */
    @Query(
            """
                SELECT new com.beltra.sma.dto.VisitaDTO(
                pren.dataPrenotazione,
                vis.dataVisita, vis.ora, vis.numAmbulatorio,
                utente.anagrafica.nome, utente.anagrafica.cognome,
                vis.prestazione.titolo
            )
                FROM Visita vis, Prenotazione pren, Utente utente
                WHERE
                vis.idVisita = pren.visita.idVisita
                AND pren.effettuata = FALSE
                AND utente.anagrafica.idAnagrafica = pren.anagrafica.idAnagrafica
                AND utente.username =:username
            """
    )
    List<VisitaDTO> findAllVisitePrenotateAndNotEffettuateByUsernamePaziente(@Param("username")String username);
}
