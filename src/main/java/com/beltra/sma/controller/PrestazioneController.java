package com.beltra.sma.controller;

import com.beltra.sma.model.Prestazione;
import com.beltra.sma.service.PrestazioneService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/prestazioni")
public class PrestazioneController {


    private final PrestazioneService prestazioneService;

    PrestazioneController(PrestazioneService prestazioneService) {
        this.prestazioneService = prestazioneService;
    }




    @GetMapping("/{id}")
    public String mostraStep1(@PathVariable Long id,
                              Model model,
                              HttpSession session) {
        Prestazione prestazione = prestazioneService.getPrestazioneById( id );
        model.addAttribute("prestazione", prestazione);
        model.addAttribute("step", 1);


        // Salva alcuni dati nella sessione
        session.setAttribute("prestazione", prestazione); // mi salvo l'intero oggetto

        session.setAttribute("titoloPrestazione", prestazione.getTitolo()); // mi salvo solo il titolo

        return "pazientePrenotaVisita";
    }
}