package com.beltra.sma.controller;

import com.beltra.sma.model.Prestazione;
import com.beltra.sma.service.PrestazioneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/prestazioni")
public class PrestazioneController {

    private final PrestazioneService prestazioneService;

    PrestazioneController(PrestazioneService prestazioneService) {
        this.prestazioneService = prestazioneService;
    }

    @GetMapping("/all")
    public String getPrestazioni(Model model) {
        List<Prestazione> listaPrestazioni = prestazioneService.getAllPrestazioni();
        model.addAttribute("titolo", "Benvenuto!");
        model.addAttribute("sottotitolo", "Ecco l'elenco dei servizi offerti dallo Studio:");
        model.addAttribute("prestazioni", listaPrestazioni );

        return "benvenuto";
    }
}
