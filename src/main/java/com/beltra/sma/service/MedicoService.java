package com.beltra.sma.service;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;

import java.util.List;

public interface MedicoService {

    /** Recupera tutti i medici (con o senza specializzazione) presenti nel sistema. */
    List<Medico> getAllMedici();


    Medico getMedicoByAnagrafica(Anagrafica anagrafica);

    Medico getMedicoByIdAnagrafica(Long idAnagrafica);

    Medico getFirstMedico();
}
