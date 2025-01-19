package com.beltra.sma.components;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Setter
@Getter
/** Memorizza idVisita e orario di fine della visita che dura meno tra quelle gia' esistenti che si stanno considerando attualemente.
 *  <br> Classe di utilita' necessaria selezionare i dati di una nuova visita Vi, sulla base dei dati gia' presenti delle visite gia'
 *  schedulate.
 *  */
public class FineVisita {
    Long idVisita;
    Time oraFine;

    public FineVisita() {
    }

    public FineVisita(Long idVisita, Time oraFine) {
        this.idVisita = idVisita;
        this.oraFine = oraFine;
    }

    @Override
    public String toString() {
        return "FineVisita{" +
                "idVisita=" + idVisita +
                ", oraFine=" + oraFine +
                '}';
    }
}