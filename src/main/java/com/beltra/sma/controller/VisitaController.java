package com.beltra.sma.controller;

import com.beltra.sma.dto.VisitaDTO;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.VisitaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/visite")
public class VisitaController {


    private final VisitaService visitaService;

    public VisitaController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    @GetMapping("/all")
    public String getElencoCronologicoVisite(Model model) {

        List<VisitaDTO> listaVisite = visitaService.getAllVisite();

        model.addAttribute("titolo", "Elenco Cronologico delle Visite Usufruite da Ciascun Paziente.");
        model.addAttribute("visite", listaVisite);
        return "visite";
    }

//    @GetMapping("/effettuate")
//    public String visitePrenotateEdEffettuate(Model model) {
//        List<Visita> listaVisitePrenotateEdEffettuate = visitaService.getVisitePrenotateEdEffettuate();
//        return "visitePrenotateEdEffettuate";
//    }

    @GetMapping("/paziente/{username}")
    public String getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(
            @PathVariable("username") String username,
            Model model) {

        List<VisitaDTO> listaVisite = visitaService.getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username);

        model.addAttribute("visiteNonEffetuate", listaVisite);

        return "visitePaziente";

    }

}
