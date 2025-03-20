package com.beltra.sma.datastructures;

import com.beltra.sma.generator.VisitaGenerator;
import com.beltra.sma.model.Visita;
import com.beltra.sma.utils.FineVisita;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.sql.Time;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@Disabled
//@RunWith(JUnitQuickcheck.class)
//public class PianificatorePropertyBasedTest {


//    @Disabled
//
//    // Property-based test per verificare che il primo medico nella mappa abbia
//    // sempre l'orario di fine più basso
//    @Property
//    public void alwaysFirstMedicoHasTheLowerOrarioFine(@From(VisitaGenerator.class) List<Visita> visite) {
//
//        // Classe che voglio testare
//        Pianificatore pianificatore = new PianificatoreImpl();
//
//        // Aggiungo tutte le visite generate
//        visite.forEach(pianificatore::aggiungiVisita);
//
//        // Recupero la mappa di medici (mediciMap)
//        Map<Long, FineVisita> mediciMap = pianificatore.getMediciMap();
//
//        // Verifica che la mappa non sia vuota
//        if (!mediciMap.isEmpty()) {
//            // Ottieni il primo medico (quello con l'orario di fine più basso)
//            FineVisita primoMedico = mediciMap.values().stream().findFirst().orElseThrow();
//            Time primoOrarioFine = primoMedico.getOraFine();
//
//            // Verifica che nessun altro medico abbia un orario di fine minore
//            for (FineVisita fineVisita : mediciMap.values()) {
//                assertTrue(primoOrarioFine.compareTo(fineVisita.getOraFine()) <= 0,
//                        "Il primo medico non ha l'orario di fine più basso: " + primoOrarioFine + " vs " + fineVisita.getOraFine());
//            }
//        }
//    }



//}