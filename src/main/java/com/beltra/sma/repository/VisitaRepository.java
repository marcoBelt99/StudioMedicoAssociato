package com.beltra.sma.repository;

import com.beltra.sma.dto.AppuntamentiSettimanaliMedicoDTO;
import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.time.LocalDate;
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


    /** Per l'utente paziente: elenco delle visite prenotate, dati il suo username e la data su cui si vuole ricercare. */
    @Query(
            """
                SELECT new com.beltra.sma.dto.VisitaPrenotataDTO(
                pren.dataPrenotazione,
                vis.dataVisita, vis.ora, vis.numAmbulatorio,
                utente.anagrafica.nome, utente.anagrafica.cognome,
                vis.prestazione.titolo,
                vis.prestazione.durataMedia
            )
                FROM Visita vis, Prenotazione pren, Utente utente
                WHERE
                vis.idVisita = pren.visita.idVisita
                AND pren.effettuata = :effettuata
                AND utente.anagrafica.idAnagrafica = pren.anagrafica.idAnagrafica
                AND utente.username = :username
                AND vis.dataVisita = :dataDiRicerca
            """
    ) // TODO: decidere se tenere questo metodo oppure quello nel service fatto con stream() API.
    List<VisitaPrenotataDTO> findAllVisitePrenotateByUsernamePazienteByDataVisita(
            @Param("username") String username,
            @Param("effettuata") Boolean effettuata,
            @Param("dataDiRicerca") Date dataDiRicerca
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

    List<Visita> findAllByAnagrafica(Anagrafica anagrafica);

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


    @Query(
    """
        SELECT new com.beltra.sma.dto.AppuntamentiSettimanaliMedicoDTO(
            vis.idVisita,
            vis.dataVisita,
            vis.ora,
            vis.prestazione.durataMedia,
            vis.prestazione.titolo,
            vis.numAmbulatorio,
            anag_paziente.nome,
            anag_paziente.cognome,
            paz.codiceFiscale
    )
        FROM Visita vis
        JOIN vis.anagrafica medico
        JOIN Utente utente ON utente.anagrafica = medico
        JOIN Prenotazione pren ON pren.visita = vis
        JOIN pren.anagrafica anag_paziente
        JOIN Paziente paz ON paz.anagrafica = anag_paziente
        WHERE utente.username = :usernameMedico
          AND vis.dataVisita BETWEEN :dataInizio AND :dataFine
          AND pren.effettuata = false
    """
    )
    List<AppuntamentiSettimanaliMedicoDTO> findAppuntamentiSettimanaliMedico(
            @Param("usernameMedico") String usernameMedico,
            @Param("dataInizio") Date dataInizio,
            @Param("dataFine") Date dataFine
    );

}
