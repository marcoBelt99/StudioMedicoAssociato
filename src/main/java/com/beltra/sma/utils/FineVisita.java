package com.beltra.sma.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Setter
@Getter
@NoArgsConstructor
/** Memorizza idVisita e orario di fine della visita che dura meno tra quelle gia' esistenti che si stanno considerando attualemente.
 *  <br> Classe di utilita' necessaria selezionare i dati di una nuova visita Vi, sulla base dei dati gia' presenti delle visite gia'
 *  schedulate.
 *  */
public class FineVisita implements Comparable<FineVisita> {
    Long idVisita;
    Time oraFine;

    public FineVisita(Long idVisita, Time oraFine) {
        this.idVisita = idVisita;
        this.oraFine = oraFine;
    }

    @Override
    public int compareTo(FineVisita o) {
        return oraFine.compareTo(o.oraFine);
    }

    @Override
    public String toString() {
        return "FineVisita{" +
                "idVisita=" + idVisita +
                ", oraFine=" + oraFine +
                '}';
    }
}