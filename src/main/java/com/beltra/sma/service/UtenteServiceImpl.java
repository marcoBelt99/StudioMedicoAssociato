package com.beltra.sma.service;

import com.beltra.sma.repository.UtenteRepository;
import org.springframework.stereotype.Service;

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
}
