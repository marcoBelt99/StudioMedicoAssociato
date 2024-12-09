package com.beltra.sma.controller;

import com.beltra.sma.repository.UtenteRepository;
import com.beltra.sma.service.PrestazioneService;
import com.beltra.sma.service.UtenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

    private final UtenteService utenteService;
    private final PrestazioneService prestazioneService;

    public IndexController(UtenteService utenteService, PrestazioneService prestazioneService) {
        this.utenteService = utenteService;
        this.prestazioneService = prestazioneService;
    }




    /** Metodo che gestisce la root page della mia app
     * @param model: permette di passare alcuni attributi
     * */
    @GetMapping("/")
    public String getWelcome(Model model) {

        model.addAttribute("intestazione", "Benvenuto/a nella root page dello Studio Medico Associato");
        model.addAttribute("titolo", "HomePage");
        //model.addAttribute("saluti", saluti);


        // return "index"; // prima era così
        return "redirect:index";
    }




    /** TODO: metodo per usare il cookie custom */
    @GetMapping(value = "index")
    public String getWelcome2(Model model,
                              @CookieValue(name = "user-id") String userId) {

        model.addAttribute("titolo",
                String.format("Benvenut%s %s nella homepage di SMA-RT",
                    utenteService.getWelcome(userId), // verifico se e' maschio o femmina
                    userId)
                );

        model.addAttribute("sottotitolo", // (per paziente)
                "Prenota subito una visita tra le seguenti:");


 // TODO: Se utente e' paziente mostrami le card con le prestazioni disponibili
        model.addAttribute("prestazioni",
                prestazioneService.getAllPrestazioniDisponibili() );


        // TODO: Se utente e' medico mostrami il quadro orario
        // TODO:



        return "index";
    }





}
