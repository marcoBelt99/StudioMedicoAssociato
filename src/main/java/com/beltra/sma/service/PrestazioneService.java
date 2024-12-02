package com.beltra.sma.service;

import com.beltra.sma.model.Prestazione;

import java.util.List;

public interface PrestazioneService {

    /** Recupera l'elenco di tutte le prestazioni dalla medesima tabella. */
    List<Prestazione> getAllPrestazioni();
}
