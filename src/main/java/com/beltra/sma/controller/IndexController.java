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

        model.addAttribute("intestazione",
                String.format("Benvenut%s %s nella index page della  Studio Medico Associato",
                    utenteService.getWelcome(userId), // verifico se e' maschio o femmina
                    userId)
                );

        // TODO: Se utente e' paziente mostrami le card con le prestazioni

        model.addAttribute("prestazioni",
                prestazioneService.getAllPrestazioni());

        // TODO: Se utente e' medico mostrami il quadro orario
        // TODO:

        // model.addAttribute("saluti", saluti);

        return "index";
    }





}
