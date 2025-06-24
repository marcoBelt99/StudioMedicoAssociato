package com.beltra.sma.service;

import com.beltra.sma.model.Prenotazione;
import com.beltra.sma.model.Utente;
import com.beltra.sma.model.Visita;
import com.beltra.sma.repository.PrenotazioneRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PrenotazioneServiceImpl implements PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;


    public PrenotazioneServiceImpl(PrenotazioneRepository repository) {
        this.prenotazioneRepository = repository;
    }


    @Override
    public Prenotazione creaPrenotazione(Visita visita, Utente utentePaziente) {
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setEffettuata(false); // pensare di mettere default a false
        prenotazione.setDataPrenotazione(new Date()); // data di oggi
        prenotazione.setVisita(visita); // visita programmata
        prenotazione.setAnagrafica(utentePaziente.getAnagrafica() ); // paziente
        return prenotazione;
    }


    @Override
    public Prenotazione salvaPrenotazione(Prenotazione prenotazione) {
        return prenotazioneRepository.save(prenotazione);
    }


}
