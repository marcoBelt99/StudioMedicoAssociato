package com.beltra.sma.controller;

import com.beltra.sma.service.PrestazioneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    private final PrestazioneService prestazioneService;

    PublicController(PrestazioneService prestazioneService) {
        this.prestazioneService = prestazioneService;
    }

    @GetMapping("/benvenuto")
    public String benvenuto(Model model) {

        model.addAttribute("titolo", "Benvenuto!");
        model.addAttribute("sottotitolo", "Ecco l'elenco dei servizi offerti dallo Studio:");

        model.addAttribute("prestazioni",
                prestazioneService.getAllPrestazioniDisponibili()
        );

        return "benvenuto";  // assicura che esista una vista benvenuto.html in resources/templates
    }


    @GetMapping("/chiSiamo")
    public String chiSiamo(Model model) {
        model.addAttribute("titolo", "SMA-RT: lo Studio Medico Associato intelligente");
        return "chiSiamo";
    }
}