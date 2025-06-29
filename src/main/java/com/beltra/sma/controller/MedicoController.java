package com.beltra.sma.controller;

import com.beltra.sma.dto.AppuntamentiSettimanaliMedicoDTO;
import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.service.MedicoServiceImpl;
import com.beltra.sma.service.VisitaService;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    private final VisitaService visitaService;
    //private final MedicoServiceImpl medicoServiceImpl;

    MedicoController(VisitaService visitaService /*,MedicoServiceImpl medicoServiceImpl */) {
        this.visitaService = visitaService;
//        this.medicoServiceImpl = medicoServiceImpl;
    }

    /** Accessibile solo da Medico. */
    @GetMapping("/visite/all")
    public String getElencoCronologicoVisite(Model model) {

        List<VisitaPrenotataDTO> listaVisite = visitaService.getAllVisite();

        model.addAttribute("titolo", "Elenco Cronologico delle Visite Usufruite da Ciascun Paziente.");
        model.addAttribute("visite", listaVisite);

        return "medicoVisite";
    }


    /** Accessibile solo da Medico. */
    /*
    @GetMapping("/appuntamenti")
    public ResponseEntity<List<AppuntamentiSettimanaliMedicoDTO>> getEventi(
            @RequestParam String inizioSettimana,
            @RequestParam String fineSettimana
    ) {
        Date dataInizio = new Date( inizioSettimana);
        Date dataFine = new Date ( fineSettimana);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //String username = "mario_rossi";

        List<AppuntamentiSettimanaliMedicoDTO> appuntamentiSettimanaliMedico =
                visitaService.getAppuntamentiSettimanaliMedico(username, dataInizio, dataFine);
        return ResponseEntity.ok(appuntamentiSettimanaliMedico);

    }

     */

    @GetMapping("/appuntamenti")
    public ResponseEntity<List<Map<String, String>>> getAppuntamentiSettimanali(
            @RequestParam String inizioSettimana,
            @RequestParam String fineSettimana
    ) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dataInizio = dateFormat.parse(inizioSettimana);
            Date dataFine = dateFormat.parse(fineSettimana);

            // Mi serve lo username del medico attualmente connesso
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // String username = "mario_rossi"; // Per test

            List<Map<String, String>> appuntamentiSettimanaliMedico =
                    visitaService.getAppuntamentiSettimanaliMedicoListaMappe(username, dataInizio, dataFine);

            return ResponseEntity.ok(appuntamentiSettimanaliMedico);

        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



//    // TODO: Endpoint per ottenere i dettagli del paziente via AJAX
//    @GetMapping("/appuntamenti")
//    @ResponseBody // Indica a Spring di restituire JSON direttamente
//    public ResponseEntity<PazienteDTO> getPazienteDetails(@RequestParam("codiceFiscale") String codiceFiscale) {
//
//        return ResponseEntity.ok( visi.getPazienteDTOByCodiceFiscale( codiceFiscale ) );
//
//    }





}
