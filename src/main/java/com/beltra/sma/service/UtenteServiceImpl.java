package com.beltra.sma.service;

import com.beltra.sma.model.Utente;
import com.beltra.sma.repository.UtenteRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.beltra.sma.exceptions.UtenteNotFoundException;

@Service
public class UtenteServiceImpl implements UtenteService {

    UtenteRepository utenteRepository;

    public UtenteServiceImpl(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public String getWelcome(String username) {
        // Vede se dire benvenuto o benvenuta in base al genere dell'utente loggato
        return
            utenteRepository
            .findByUsername(username)
            .getAnagrafica().getGenere().equals("M") ?  "o" : "a";

    }


    @Override
    public Utente getUtenteByUsername(String username) {
        return utenteRepository.findByUsername(username);
    }


    @Override
    public Boolean existsUtenteByUsername(String username) {
        return utenteRepository.existsByUsername(username);
    }


    @Override
    public Boolean isAnonimo(String username) throws UtenteNotFoundException {

        Utente utente = getUtenteByUsername(username);

        if (utente == null || !existsUtenteByUsername(utente.getUsername())) {
            return true; // L'utente non esiste o non è mai stato inserito
        }

        // Se arrivo qui, un utente deve esserci, quindi verifico che non abbia alcuno dei
        // ruoli possibili

        // Il ruolo dell'utente non deve essere uguale ai ruoli che gia' esistono
        return utente
                .getRuoli()
                .stream()
                .noneMatch(
                    r -> r.getTipo().equals("PAZIENTE") ||
                    r.getTipo().equals("MEDICO") ||
                    r.getTipo().equals("INFERMIERE")
                );

    }
}
