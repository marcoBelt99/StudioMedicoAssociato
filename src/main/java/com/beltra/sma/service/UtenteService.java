package com.beltra.sma.service;

import com.beltra.sma.model.Utente;

public interface UtenteService {

    /** Restituisce la lettera 'o' se l'utente connesso e' di genere 'M',<br>
     *  altrimenti 'a' (se l'utente connesso e' di genere 'F').
     *  */
    String getWelcome(String username);

    /** Recupera l'utente in base al suo username.
     * @param username chiave di ricerca
     * */
    Utente getUtenteByUsername(String username);

    /** Verifica che l'utente con quel dato username sia presente nel database.
     * @param username chiave di ricerca
     * */
    Boolean existsUtenteByUsername(String username);

    /** Verifica che l'utente sia anonimo, controllando che non abbia settati i nessuno dei ruoli
     *  possibili.
     *  @param username l'utente da verificare
     *  */
    Boolean isAnonimo(String username);

}
