package com.beltra.sma.repository;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public interface VisitaRepository extends JpaRepository<Visita, Long> {

    /** TODO: Query Method.
     * <br>
     *  Elenco delle visite prenotate ed effettuate
     * */
    @Query(
        """
        SELECT new com.beltra.sma.dto.VisitaPrenotataDTO(
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
    List<VisitaPrenotataDTO> findAllVisiteOrderByDataVisitaDesc();


    /** Elenco di tutte le visite appartenenti ad un determinato paziente. */
    @Query(
    """
    SELECT visita
    FROM Prenotazione pren
    JOIN pren.visita visita
    WHERE pren.anagrafica.idAnagrafica = :idAnagraficaPaziente
    """
    )
    List<Visita> findAllVisitePazienteByAnagraficaPaziente(
        @Param("idAnagraficaPaziente") Long idAnagraficaPaziente
    );


    /** Per l'utente paziente: elenco delle visite prenotate e non effettuate */
    @Query(
    """
        SELECT new com.beltra.sma.dto.VisitaPrenotataDTO(
        pren.dataPrenotazione,
        vis.dataVisita, vis.ora, vis.numAmbulatorio,
        utente.anagrafica.nome, utente.anagrafica.cognome,
        vis.prestazione.titolo
    )
        FROM Visita vis, Prenotazione pren, Utente utente
        WHERE
        vis.idVisita = pren.visita.idVisita
        AND pren.effettuata = :effettuata
        AND utente.anagrafica.idAnagrafica = pren.anagrafica.idAnagrafica
        AND utente.username = :username
    """
    )
    List<VisitaPrenotataDTO> findAllVisitePrenotateByUsernamePaziente(
        @Param("username") String username,
        @Param("effettuata") Boolean effettuata
    );





    /** Elenco di tutte le visite prenotate e non effettuate */
    @Query(
            """
                SELECT new com.beltra.sma.dto.VisitaPrenotataDTO(
                pren.dataPrenotazione,
                vis.dataVisita, vis.ora, vis.numAmbulatorio,
                utente.anagrafica.nome, utente.anagrafica.cognome,
                vis.prestazione.titolo
            )
                FROM Visita vis, Prenotazione pren, Utente utente
                WHERE
                vis.idVisita = pren.visita.idVisita
                AND pren.effettuata = :effettuata
                ORDER BY vis.dataVisita ASC
            """
    )
    List<VisitaPrenotataDTO> findAllVisitePrenotateOrderByDataVisitaAsc(
            @Param("effettuata") Boolean effettuata
    );


    /** Trova tutte le visite del DB e le ordina con in cima la più "prossima" */
    List<Visita> findAllByOrderByDataVisitaDesc();

    
    List<Visita> findAllByOrderByDataVisitaAscOraAsc();


    /** Trova tutte le visite del DB relative ad una determinata data, in ordine di ora (crescente) */
    @Query("SELECT v FROM Visita v WHERE v.dataVisita = :data ORDER BY v.ora ASC")
    List<Visita> findAllByDataVisita(@Param("data") Date data);


    /** Trova tutte le visite del DB a partire dalla data di oggi fino alla data dell'ultima visita creata */
    @Query(
            value=
        """
        SELECT * 
            FROM visite
                WHERE data_visita BETWEEN NOW() AND 
                    (SELECT MAX( data_visita ) 
                        FROM visite )
                ORDER BY data_visita ASC;
            
        """
        , nativeQuery = true)
    List<Visita> findAllVisiteFromNow();


    /** Trova tutte le visite di un determinato medico in una determinata data */
    @Query("SELECT v FROM Visita v WHERE v.anagrafica.idAnagrafica = :idMedico AND v.dataVisita = :data")
    List<Visita> findByMedicoAndData(@Param("idMedico") Long idMedico, @Param("data") Date data);


    List<Visita> findByAnagrafica(Anagrafica anagrafica);

    /** Prende tutte le visite di una determianta data assegnate ad un determinato medico */
    List<Visita> findByAnagraficaAndDataVisita(Anagrafica medico, Date dataVisita);




    //boolean existsByMedicoAndDataVisitaAndOraBetween(Medico medico, Date dataVisita, Time oraInizio, Time oraFine);

}
