package com.beltra.sma.controller;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.service.VisitaService;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    private final VisitaService visitaService;

    MedicoController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    /** Accessibile solo da Medico. */
    @GetMapping("/visite/all")
    public String getElencoCronologicoVisite(Model model) {

        List<VisitaPrenotataDTO> listaVisite = visitaService.getAllVisite();

        model.addAttribute("titolo", "Elenco Cronologico delle Visite Usufruite da Ciascun Paziente.");
        model.addAttribute("visite", listaVisite);

        return "medicoVisite";
    }

    // TODO: ...



}
