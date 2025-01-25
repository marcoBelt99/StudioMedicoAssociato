package com.beltra.sma.components;

/** ENUMERAZIONE per rappresentare il risultato del calcolo ammissibilita' orario
 *  <p>AMMISSIBILE = orario entro gli orari prestabiliti di lavoro </p>
 *  <p>NO_BEFORE_APERTURA_MATTINA = orario prima delle 07:00 </p>
 *  <p>NO_AFTER_CHIUSURA_MATTINA =  orario dopo le 12:00 </p>
 *  <p>NO_BEFORE_APERTURA_POMERIGGIO = orario prima delle 14:00 </p>
 *  <p>NO_AFTER_CHIUSURA_POMERIGGIO = orario dopo le 21:00 </p>
 * */
public enum Risultato {
    AMMISSIBILE,
    NO_BECAUSE_BEFORE_APERTURA_MATTINA,
    NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO,
    NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO
}
