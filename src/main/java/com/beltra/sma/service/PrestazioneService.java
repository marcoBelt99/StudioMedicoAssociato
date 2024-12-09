package com.beltra.sma.service;

import com.beltra.sma.model.Prestazione;

import java.util.List;

public interface PrestazioneService {

    /**
     *  @return Recupera l'elenco di <b>tutte</b> le prestazioni dalla medesima tabella. */
    List<Prestazione> getAllPrestazioni();

    /**
     *  @return Recupera l'elenco delle sole prestazioni <b>disponibili</b> (cioe' hanno deleted a false) dalla medesima tabella. */
    List<Prestazione> getAllPrestazioniDisponibili();

    /** @return Recupera la specifica prestazione dato l'id.
     *  @param id chiave di ricerca della specifica prestazione. */
    Prestazione getPrestazioneById(Long id);

}
