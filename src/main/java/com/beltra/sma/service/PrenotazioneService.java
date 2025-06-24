package com.beltra.sma.service;

import com.beltra.sma.model.Prenotazione;
import com.beltra.sma.model.Utente;
import com.beltra.sma.model.Visita;

public interface PrenotazioneService {

    /** 1) Crea la prenotazione, pronta per essere salvata in sessione. */
    Prenotazione creaPrenotazione(Visita visita, Utente utentePaziente);

    /** 2) Salva la prenotazione, pronta per essere persistita a database. */
    //Prenotazione salvaPrenotazione(Visita visita, Utente utentePaziente);

    /** Versione semplice. */
    Prenotazione salvaPrenotazione(Prenotazione prenotazione);
}
