package com.beltra.sma.components;

/** Enumerazione per rappresentare il risultato del calcolo ammissibilita' orario.
 *  <ul>
 *      <li>AMMISSIBILE = orario entro gli orari prestabiliti di lavoro </li>
 *      <li>NO_BECAUSE_BEFORE_APERTURA_MATTINA = orario prima delle 07:00 </li>
 *      <li>NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO =  orario compreso tra  le 12:00 e le 14:00 </li>
 *      <li>NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO = orario dopo le 21:00  </li>
 *  </ul>
 * */
public enum Risultato {
    AMMISSIBILE,
    NO_BECAUSE_BEFORE_APERTURA_MATTINA,
    NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO,
    NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO
}
