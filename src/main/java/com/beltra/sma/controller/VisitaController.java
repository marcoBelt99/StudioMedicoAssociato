package com.beltra.sma.controller;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.service.VisitaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/visite")
public class VisitaController {


    private final VisitaService visitaService;

    public VisitaController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    /** Accessibile solo da Medico. */
    @GetMapping("/all")
    public String getElencoCronologicoVisite(Model model) {

        List<VisitaPrenotataDTO> listaVisite = visitaService.getAllVisite();

        model.addAttribute("titolo", "Elenco Cronologico delle Visite Usufruite da Ciascun Paziente.");
        model.addAttribute("visite", listaVisite);
        return "visite";
    }



}
