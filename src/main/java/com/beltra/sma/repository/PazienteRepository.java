package com.beltra.sma.repository;

import com.beltra.sma.model.Paziente;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PazienteRepository extends JpaRepository<Paziente, Long> { //

    /** TODO: Query Method. <br>
     *        Deve seguire una specifica denominazione: <br>
     *          find
     *          By
     *          CodiceFiscale   <campo usato per fare la ricerca>.
     *
     *  <br>
     *  Trova il paziente
     *
     * */
    Paziente findByCodiceFiscale(String cpf);


    /** TODO: Query Method. <br>
     *       Cerco un Paziente usando parte del suo campo "cognome".
     *       <br>
     *       Usando il Like (come in SQL) indico che sto facendo una ricerca parziale.
     *       <br>
     *       @param pageable: Con la ricerca, in generale, potrei anche ottenere tantissimi pazienti. <br>
     *       Usando questo oggetto <b>Pageable</b> posso paginare i risultati di ricerca.
     * */
    List<Paziente> findByAnagraficaCognomeLike(String cognome, Pageable pageable);


    /** TODO: Query Method. <br>
     *   Cerco tutti i pazienti che hanno un determinato "nome" in ordine di cognome :
     *  */
    List<Paziente> findAllByAnagraficaNomeOrderByAnagraficaCognome(String nome);


    /** TODO: Query con <b>JPQL</b>.<br>
     *   CONVENZIONALMENTE: posso anteporre <b>"sel"</b> ai metodi JPQL, così da distinguere JPQL e Query Native dalle queries che si basano sul Query Method.<br>
     *   <br>
     *  Esempio: recuperare il paziente che ha un certo nome e un certo cognome.
     *  @param cognome il cognome del paziente.
     *  @param nome il nome del paziente.
     *  <br>
     * In particolare, la query seleziona la classe di entità Paziente che e' stata rinominata con l'alias p.<br>
     * (questo join riguarda le classi di entità e non le tabelle!)
     * <br>
     * JPQL e' molto potente perchè mi permette di fare query prescindendo dall'SQL standard, usando direttamente le classi di entità.
     *
     *
     * */
    @Query(value="SELECT p FROM Paziente p, Anagrafica a WHERE a.idAnagrafica = p.idAnagrafica  AND a.cognome = :c AND a.nome = (:n)")
    Paziente selPazienteByCognomeAndNome(@Param("c") String cognome,
                                         @Param("n") String nome);


    /** TODO: Query con SQL standard.<br>
     *  Esempio: Calcolo quanti pazienti ci sono con un certo cognome
     * */
    @Query(value = "SELECT COUNT (*) FROM pazienti, anagrafiche WHERE anagrafiche.id_anagrafica = pazienti.id_anagrafica AND anagrafiche.cognome= :c", nativeQuery = true)
    int countPazientiWithThisCognome(@Param("c") String cognome);


    /** Aggiorna il medico assegnato al paziente tramite il suo codice fiscale.
     *  <br>
     *  Ritorna il numero di righe che ha aggiornato.
     *  @param codiceFiscale codice fiscale del paziente (cioè la chiave di ricerca).
     *  @param medico è codice del medico (cioè il dato da aggiornare).
     *  @return 1 se la riga è stata aggiornata, 0 altrimenti.
     *
      */
//    @Modifying // indica che il metodo esegue un'operazione di modifica ==> informo Spring Data JPA che il metodo non è di sola lettura
//    @Query(value = "UPDATE pazienti SET medico = :codiceMedico WHERE codice_fiscale = :codiceFiscale", nativeQuery = true)
//    int updateMedicoByCodiceFiscale(@Param("codiceFiscale") String codiceFiscale,
//                                    @Param("codiceMedico") String medico);







}
