package com.beltra.sma.controller;

import com.beltra.sma.model.Prenotazione;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.service.PrestazioneService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/prenotazione")

@SessionAttributes({"nome", "cognome"}) // dati che recupero dalla sessione (dai form in POST quando submitto)
public class PrenotazioneController {

    private final PrestazioneService prestazioneService;
    private final HttpSession httpSession; /** Faccio il code Injection anche della sessione!! */

    PrenotazioneController(PrestazioneService prestazioneService, HttpSession httpSession) {
        this.prestazioneService = prestazioneService;
        this.httpSession = httpSession;
    }


    @GetMapping("/step{step}")
    public String showStep(@PathVariable int step,
                           Model model) {

        // Aggiungo il passo al modello per renderlo disponibile nella view
        model.addAttribute("step", step);

        /** Recupero dalla sessione l'attributo "prestazione" che contiene l'oggetto prestazione */
        Prestazione prestazione = (Prestazione) httpSession.getAttribute("prestazione");

        /** Necessario aggiungere al model l'oggetto prestazione recuperato dalla sessione,
         * altrimenti il pulsante "Indietro" dello stepper non funziona !! */
        if(prestazione != null)
            model.addAttribute("prestazione", prestazione);


        return (step == 1 || step == 2 || step == 3 ) ? "pazientePrenotaVisita" : "errorPage";

    }


    @PostMapping("/step2")
    public String step2(@RequestParam String nome,
                        @RequestParam String cognome,
                        HttpSession session,
                        Model model) {

        session.setAttribute("nome", nome);
        session.setAttribute("cognome", cognome);

        model.addAttribute("step", 3);

        // TODO: check presenza dottore e disponibilita' orario


        // TODO: aggiunta a sessione la visita e la prenotazione
        // La prenotazione viene salvata con la data odierna

        return "pazientePrenotaVisita";
    }

    @PostMapping("/conferma")
    public String confermaPrenotazione(HttpSession session,
                                       RedirectAttributes redirectAttributes) {

        String nome = (String) session.getAttribute("nome");
        String cognome = (String) session.getAttribute("cognome");
        String titoloPrestazione = (String) session.getAttribute("titoloPrestazione");

        // TODO:  Logica di salvataggio VISITA + PRENOTAZIONE
        System.out.println("Prenotazione confermata per " + nome + " " + cognome + " - " + titoloPrestazione);

        redirectAttributes.addFlashAttribute("success", "Prenotazione completata!");

        return "redirect:/paziente/visite?effettuata=false";
    }










}
