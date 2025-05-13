package com.beltra.sma.controller;

import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.service.MedicoService;
import com.beltra.sma.service.PrestazioneService;
import com.beltra.sma.service.UtenteService;
import com.beltra.sma.utils.SlotDisponibile;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;


@Controller
@RequestMapping("/prenotazione")

//@SessionAttributes({"nome", "cognome"}) // dati che recupero dalla sessione (dai form in POST quando submitto)
@SessionAttributes({"primaDataDisponibile", "primoOrarioDisponibile"})
public class PrenotazioneController {

    private final PrestazioneService prestazioneService;
    private final UtenteService utenteService;
    private final MedicoService medicoService;
    private final PianificazioneComponent pianificazioneComponent;
    private final HttpSession httpSession; /** Faccio il code Injection anche della sessione!! */

    PrenotazioneController(PrestazioneService prestazioneService,
                           HttpSession httpSession,
                           PianificazioneComponent pianificazioneComponent,
                           MedicoService medicoService,
                           UtenteService utenteService) {
        this.prestazioneService = prestazioneService;
        this.httpSession = httpSession;
        this.pianificazioneComponent = pianificazioneComponent;
        this.medicoService = medicoService;
        this.utenteService = utenteService;
    }


    @GetMapping("/step{step}")
    public String showStep(@PathVariable int step,
                           Model model) {

        // Aggiungo il passo al modello per renderlo disponibile nella view
        model.addAttribute("step", step);


        /** Recupero dalla sessione l'attributo "prestazione" che contiene l'oggetto prestazione */
        Prestazione prestazione = (Prestazione) httpSession.getAttribute("prestazione");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Anagrafica anagraficaPazientePrenotante = utenteService.getUtenteByUsername( auth.getName() ).getAnagrafica();
        String nomePaziente = anagraficaPazientePrenotante.getNome();
        String cognomePaziente = anagraficaPazientePrenotante.getCognome();


        /** Necessario aggiungere al model l'oggetto prestazione recuperato dalla sessione,
         * altrimenti il pulsante "Indietro" dello stepper non funziona !! */
        if(prestazione != null)
//            model.addAttribute("prestazione", prestazione);
            model.addAttribute("prestazione", prestazione);



        // TODO: aggiungo il valore dello SlotDisponibile:
        // Gli passo la durata media della prestazione scelta obbligatoriamente allo step precedente, inoltre ti faccio partire la ricerca di questo slot
        // dalla data attuale (ci pensa poi il metodo a verificare che il giorno non sia un sabato o una domenica).

        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaSlotDisponibile( prestazione.getDurataMedia(),
                        new Date(), // data di oggi
                        LocalTime.now(), // orario attuale
                        medicoService.getAllMedici(),
                        pianificazioneComponent.getAllVisiteByData(new Date() ) ); // gli passo la data di oggi

        slotDisponibile.ifPresent(slot -> {
            model.addAttribute("primaDataDisponibile", slot.getData() )
                 .addAttribute("primoOrarioDisponibile", slot.getOrario() )
                 .addAttribute("medicoCandidato", slot.getMedico().getNominativo()   )
                 .addAttribute("nomePaziente", nomePaziente )
                 .addAttribute("cognomePaziente", cognomePaziente); // Notare l'uso del method chaining
            System.out.println( slotDisponibile.get() );
        });


        return (step == 1 || step == 2 || step == 3 ) ? "pazientePrenotaVisita" : "errorPage";

    }


    /** !!!!!
     *
     *  #########################
     *  #########################
     *
     *  TODO !!!!!
     *
     *  #########################
     *  #########################
     *
     *  */
    @PostMapping("/step2")
    public String step2(
            @RequestParam String primaDataDisponibile,
            @RequestParam String primoOrarioDisponibile,
            @RequestParam String medicoCandidato,
                        HttpSession session,
                        Model model) {

//        session.setAttribute("nome", nome);
//        session.setAttribute("cognome", cognome);

        session.setAttribute("primaDataDisponibile", primaDataDisponibile);
        session.setAttribute("primoOrarioDisponibile", primoOrarioDisponibile);
        model.addAttribute("medicoCandidato", medicoCandidato);

        model.addAttribute("step", 3);


        //model.addAttribute("primaDataDisponibile",  );

        // TODO: check presenza dottore e disponibilita' orario
        // ............

        // TODO: aggiunta a sessione la visita e la prenotazione
        //  La prenotazione viene salvata con la data odierna
        // .............

        return "pazientePrenotaVisita";
    }


    /** Ultimo stadio della prenotazione */
    @PostMapping("/conferma")
//    @PostMapping("/step/3")
    public String confermaPrenotazione(HttpSession session,
                                       RedirectAttributes redirectAttributes) {

        Date primaDataDisponibile = (Date) session.getAttribute("primaDataDisponibile");
        Time primoOrarioDisponibile = (Time) session.getAttribute("primoOrarioDisponibile");
        //String titoloPrestazione = (String) session.getAttribute("titoloPrestazione");

        Prestazione prestazione = (Prestazione) httpSession.getAttribute("prestazione");

        // TODO:  Logica di salvataggio VISITA + PRENOTAZIONE
//        System.out.println("Prenotazione confermata per " + nome + " " + cognome + " - " + titoloPrestazione);
        // System.out.println("Prenotazione confermata per " + nome + " " + cognome + " - " + prestazione);

        redirectAttributes.addFlashAttribute("success", "Prenotazione completata!");

        return "redirect:/paziente/visite?effettuata=false";
    }





}
