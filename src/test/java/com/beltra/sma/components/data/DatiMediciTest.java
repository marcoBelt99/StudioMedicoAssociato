package com.beltra.sma.components.data;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;

import java.util.Arrays;
import java.util.List;

public class DatiMediciTest implements DatiTest<Medico> {

    private DatiAnagraficheTest datiAnagraficheTest = new DatiAnagraficheTest();

    @Override
    public List<Medico> getDatiTest() {


        List<Anagrafica> listaAnagrafiche = datiAnagraficheTest.getDatiTest();

        Medico medico1 = new Medico();
        medico1.setMatricola("MED0001");
        medico1.setIdAnagrafica(1L);
        medico1.setSpecializzazione("");

        Medico medico2 = new Medico();
        medico2.setMatricola("MED0002");
        medico2.setIdAnagrafica(2L);
        medico2.setSpecializzazione("Pediatria");

        Medico medico3 = new Medico();
        medico3.setMatricola("MED0003");
        medico3.setIdAnagrafica(3L);
        medico3.setSpecializzazione("Chirurgia");

        List<Medico> listaMedici = Arrays.asList(medico1, medico2, medico3);

        // Ad ogni singolo medico assegno la rispettiva anagrafica

        for (int i = 0; i < listaMedici.size(); i++)
            listaMedici.get(i).setAnagrafica(listaAnagrafiche.get(i));

        return listaMedici;
    }
}