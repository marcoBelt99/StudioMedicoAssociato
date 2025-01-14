package com.beltra.sma.service;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Visita;
import com.beltra.sma.repository.MedicoRepository;
import com.beltra.sma.repository.PrestazioneRepository;
import com.beltra.sma.repository.VisitaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MedicoServiceTests {

    @Autowired
    MedicoRepository medicoRepository;



    @Autowired
    VisitaService visitaService;

    @Autowired
    private PrestazioneRepository prestazioneRepository;


    /** Devo fare in modo che, all'interno di un certo intervallo orario (compreso tra oraInizio ed oraFine) e di una certa data,
     *  ci sia almeno un medico non occupato in qualche visita.
     *  Se c'è più di un medico libero, scelgo il primo.
     * */
    @Test
    public void searchMedicoDisponibile() {

        // C'È UN MEDICO ?
        // C'È POSTO ?



        // Elenco di tutti i medici del sistema
        List<Medico> listaMedici = medicoRepository.findAll();

        // Elenco cronologico delle visite
        List<Visita> listaVisiteOrdinateDallaPiuRecente = visitaService.getAllVisiteOrderByDataVisitaAsc();

        HashMap<Date, Medico> mappaMedici = new HashMap<>();

        listaVisiteOrdinateDallaPiuRecente.forEach(visita -> {
//            mappaMedici.put( visita.getDataVisita(),  )
           // se la data e' la stessa della precedente
        });

        for(int i=1; i<=listaMedici.size(); i++) {

        }


        // Per ogni visita della lista
        // Partendo dalla prima (quella più prossima),

        // Elenco (set) di tutte le date del sistema
        Set<Date> setDate = new HashSet<>();

        listaVisiteOrdinateDallaPiuRecente.forEach( visita -> setDate.add( visita.getDataVisita() ) );
        List<Date> listaDate = setDate.stream().sorted().toList();




        listaVisiteOrdinateDallaPiuRecente.forEach(visita -> {
            System.out.println(visita);
            System.out.println();
        });


        System.out.println("\nSet di date:\n");
        //    setDate.forEach(System.out::println);
        listaDate.forEach(System.out::println);
        // Vedo se non ho problemi di orario

        // Vedo se non ho

        //
        //    Time oraInizio = new Time(15, 30, 0);
        //    Time oraFine = new Time(16, 30, 0);
        //
        //    List<VisitaPrenotataDTO> listaVisitePrenotate =
        //            visitaService.getAllVisitePrenotateAndNotEffettuate()
        //            .stream()
        //            .filter( vp -> vp.getOra().after( oraInizio ) && vp.getOra().before( oraFine ) )
        //            .filter( vp ->
        //                ( oraFine.getTime() - oraInizio.getTime() ) > prestazioneRepository.findByTitolo( vp.getTitoloPrestazione() ).orElseThrow().getDurataMedia()  )
        //            .toList();
        //
        //
        //        //System.out.println( listaVisitePrenotate.size() );
        //
        //        System.out.println( visitaService.getAllVisitePrenotateAndNotEffettuate() );
        //
        //        //assertEquals( listaVisitePrenotate.size(), visitaService.getAllVisitePrenotateAndNotEffettuate().size() );
        //

    }


    @Test
    public void testSearchMedicoDisponibile() {
        // TODO:

    }
}
