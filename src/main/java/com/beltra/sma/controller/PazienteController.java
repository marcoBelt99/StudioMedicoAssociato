package com.beltra.sma.controller;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.service.VisitaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/paziente")
public class PazienteController {


    private final VisitaService visitaService;

    public PazienteController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    @GetMapping("/visite")
    public String getAllVisitePrenotateByUsernamePaziente(
            @CookieValue(name="user-id") String username,
            @RequestParam(value = "effettuata") Boolean effettuata,
            Model model) {


        // Se ho /visite?effettuata=true allora visualizzo solo quelle effettuate
        // Se ho /visite?effettuata=false allora visualizzo solo quelle non ancora effettuate

        List<VisitaPrenotataDTO> listaVisite = null;

        // Notare: credo si possa recuperare lo username anche facendo uso di:
        // SecurityContextHolder.getContext().getAuthentication().getName();

        if(effettuata) {
            listaVisite = visitaService.getAllVisitePrenotateAndEffettuateByUsernamePaziente( username );
            model.addAttribute("titolo", "Visite Prenotate ed Effettuate");
        }
        else {
            listaVisite = visitaService.getAllVisitePrenotateAndNotEffettuateByUsernamePaziente( username );
            model.addAttribute("titolo", "Visite Prenotate e Non Effettuate");
        }

        model.addAttribute("visitePrenotate", listaVisite);
        return "pazienteVisite";
    }


}




