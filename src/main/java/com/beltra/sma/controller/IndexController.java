package com.beltra.sma.controller;

import com.beltra.sma.service.PrestazioneService;
import com.beltra.sma.service.UtenteService;
import com.beltra.sma.service.VisitaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/** Gestisce cosa vuoi vedere nella pagina di index, sulla base del tipo di utente.  */
@Controller
public class IndexController {

    private final UtenteService utenteService;
    private final PrestazioneService prestazioneService;
    private final VisitaService visitaService;


    public IndexController(UtenteService utenteService, PrestazioneService prestazioneService, VisitaService visitaService) {
        this.utenteService = utenteService;
        this.prestazioneService = prestazioneService;
        this.visitaService = visitaService;
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

        model.addAttribute("titolo",
                String.format("Benvenut%s %s nella homepage di SMA-RT",
                    utenteService.getWelcome(userId), // verifico se e' maschio o femmina
                    userId)
                );

        // Ottieni l'utente attualmente connesso (autenticato), che servirà poi per
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
        String ruolo = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElseThrow().trim(); // Prendo il primo ruolo trovato (potrebbe succedere che un utente ne abbia piu' di uno)



        // Attuo comportamenti diversi in base al ruolo utente attualmente connesso
        switch (ruolo) {
            // TODO: Se utente e' paziente mostrami le card con le prestazioni disponibili
            case "ROLE_PAZIENTE" -> {
                model.addAttribute("sottotitolo", "Prenota subito una visita tra le seguenti:");
                model.addAttribute("prestazioni",
                        prestazioneService.getAllPrestazioniDisponibili() );

            }
            // TODO: Se utente e' medico mostrami il quadro orario
            case "ROLE_MEDICO" -> {
                model.addAttribute("sottotitolo", "Elenco settimanale dei tuoi appuntamenti:");
                model.addAttribute("appuntamenti",
                        visitaService.getAllVisite() ); // TODO: cambiare questa lista se necessario con la lista delle sole visite comprese da lunedì a venerdì
            }
        };

        return "index";
    }





}
