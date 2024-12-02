package com.beltra.sma.service;

public interface UtenteService {

    /** Restituisce la lettera 'o' se l'utente connesso e' di genere 'M',<br>
     *  altrimenti 'a' (se l'utente connesso e' di genere 'F').
     *  */
    String getWelcome(String username);
}
