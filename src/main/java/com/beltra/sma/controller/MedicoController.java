package com.beltra.sma.controller;

import com.beltra.sma.dto.AppuntamentoSettimanaleMedicoDTO;
import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.Visita;
import com.beltra.sma.service.VisitaService;


import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    private final VisitaService visitaService;

    private final HttpSession session;
    private final HttpSession httpSession;


    MedicoController(VisitaService visitaService, HttpSession session, HttpSession httpSession) {
        this.visitaService = visitaService;
        this.session = session;
        this.httpSession = httpSession;
    }

    /** Accessibile solo da Medico. */
    @GetMapping("/visite/all")
    public String getElencoCronologicoVisite(Model model) {

        List<VisitaPrenotataDTO> listaVisite = visitaService.getAllVisite();

        model.addAttribute("titolo", "Elenco Cronologico delle Visite Usufruite da Ciascun Paziente.");
        model.addAttribute("visite", listaVisite);

        return "medicoVisite";
    }


    /** Accessibile solo da Medico. <br>
     *  Endpoint per popolare il calendario con gli eventi (appuntamenti) del medico corrente.
     * */
    @GetMapping("/appuntamenti")
    public ResponseEntity<List<Map<String, Object>>> getAppuntamentiSettimanali(
            @RequestParam String inizioSettimana,
            @RequestParam String fineSettimana) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dataInizio = dateFormat.parse(inizioSettimana);
            Date dataFine = dateFormat.parse(fineSettimana);

            // Mi serve lo username del medico attualmente connesso
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // String username = "mario_rossi"; // Per test

            List<Map<String, Object>> appuntamentiSettimanaliMedico =
                    visitaService.getAppuntamentiSettimanaliMedicoListaMappe(username, dataInizio, dataFine);

            // Mi salvo in sessione la data
            httpSession.setAttribute("inizioSettimana", inizioSettimana);
            httpSession.setAttribute("fineSettimana", fineSettimana);

            return ResponseEntity.ok(appuntamentiSettimanaliMedico);

        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    /** Accessibile solo da Medico. <br>
     *  Permette di accedere alla pagina di dettaglio della specifica visita. */
    @GetMapping("/appuntamenti/dettaglio/{idAppuntamento}")
    public String getDettaglioAppuntamento(@PathVariable Long idAppuntamento,
                                     Model model) throws ParseException {

            // Deve essere necessariamente un Medico
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String inizioSettimana = (String) httpSession.getAttribute("inizioSettimana");
            String fineSettimana =  (String) httpSession.getAttribute("fineSettimana");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dataInizio = dateFormat.parse(inizioSettimana);
            Date dataFine = dateFormat.parse(fineSettimana);


            AppuntamentoSettimanaleMedicoDTO appuntamentoSettimanale = visitaService.getAppuntamentoById(idAppuntamento, username, dataInizio, dataFine);

            model.addAttribute("titolo", "Appuntamento numero: " + idAppuntamento);
            model.addAttribute("appuntamento", appuntamentoSettimanale);

            return "appuntamentoDettaglio";

    }



}
