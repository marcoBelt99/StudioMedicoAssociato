package com.beltra.sma.controller;

import com.beltra.sma.components.PianificazioneComponent;
import com.beltra.sma.model.*;
import com.beltra.sma.service.*;
import com.beltra.sma.utils.SlotDisponibile;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;


@Controller
@RequestMapping("/prenotazione")

//@SessionAttributes({"nome", "cognome"}) // dati che recupero dalla sessione (dai form in POST quando submitto)
//@SessionAttributes({"primaDataDisponibile", "primoOrarioDisponibile", "nuovaVisita", "nuovaPrenotazione"}) // Tirando via questo pare funzioni il problema che salva solo quando mi ri-loggo
public class PrenotazioneVisitaController {


    private final UtenteService utenteService;
    private final MedicoService medicoService;
    private final PianificazioneComponent pianificazioneComponent;
    private final VisitaService visitaService;
    private final PrenotazioneService prenotazioneService;
    private final HttpSession httpSession; /** Faccio il code Injection anche della sessione!! */

    // Code Injection necessarie
    PrenotazioneVisitaController(HttpSession httpSession,
                                 PianificazioneComponent pianificazioneComponent,
                                 MedicoService medicoService,
                                 UtenteService utenteService,
                                 VisitaService visitaService,
                                 PrenotazioneService prenotazioneService

    ) {
        this.httpSession = httpSession;
        this.pianificazioneComponent = pianificazioneComponent;
        this.medicoService = medicoService;
        this.utenteService = utenteService;
        this.visitaService = visitaService;
        this.prenotazioneService = prenotazioneService;
    }





    /**
     *  #########################
     *  #########################
     *  GET step
     *  #########################
     *  #########################
     *  */


    @GetMapping("/step{step}")
    public String showStep(@PathVariable int step,
                           Model model) {


        /** Recupero dalla sessione l'attributo "prestazione" che contiene l'oggetto prestazione */
        Prestazione prestazione = (Prestazione) httpSession.getAttribute("prestazione");

        /** Recupero l'utente attualmente autenticato */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernamePazientePrenotante = auth.getName();
        Utente utentePazientePrenotante = utenteService.getUtenteByUsername( usernamePazientePrenotante );
        Anagrafica anagraficaPazientePrenotante = utentePazientePrenotante.getAnagrafica();

        String nomePazientePrenotante = anagraficaPazientePrenotante.getNome();
        String cognomePazientePrenotante = anagraficaPazientePrenotante.getCognome();

        httpSession.setAttribute("utentePaziente", utentePazientePrenotante);


        /** Aggiungo il passo al modello per renderlo disponibile nella view, così rendo dinamico lo stepper (nella view) e posso
         *  fargli fare cose diverse in base al valore dell'attributo step. */
        model.addAttribute("step", step);

        /** Necessario aggiungere al model l'oggetto prestazione recuperato dalla sessione,
         * altrimenti il pulsante "Indietro" dello stepper non funziona !! */
        if(prestazione != null)
            model.addAttribute("prestazione", prestazione);
        // e se prestazione è null???


        /**
        * TODO: ottengo SlotDisponibile:
        * Gli passo la durata media della prestazione, scelta obbligatoriamente allo step precedente. inoltre ti faccio partire la ricerca di questo slot
        * dalla data attuale (ci pensa poi il metodo a verificare che il giorno non sia un sabato o una domenica). */
        Optional<SlotDisponibile> slotDisponibile = getSlotDisponibile(model, prestazione, usernamePazientePrenotante, nomePazientePrenotante, cognomePazientePrenotante);



        /// (1) Creazione Prenotazione + Visita associata.
        //Utente utentePaziente = (Utente) httpSession.getAttribute("utentePaziente");
        //Prestazione prestazione = (Prestazione) httpSession.getAttribute("prestazione");
        //SlotDisponibile slotDisponibile = (SlotDisponibile) httpSession.getAttribute("slotDisponibile");

        Visita nuovaVisita = visitaService.creaVisita(prestazione, slotDisponibile.get());
        Prenotazione nuovaPrenotazione = prenotazioneService.creaPrenotazione(nuovaVisita, utentePazientePrenotante);

        // TODO: Pensare di chiedermi che, se lo step == 3 allora setta la sessione per salvare i dati...

        /// 2) Salvataggio in sessione di nuovaVisita e di Prenotazione
        httpSession.setAttribute("nuovaVisita", nuovaVisita);
        httpSession.setAttribute("nuovaPrenotazione", nuovaPrenotazione);
//        session.setAttribute("nuovaVisita", nuovaVisita);
//        session.setAttribute("nuovaPrenotazione", nuovaPrenotazione);



        /** Se lo step è compreso tra 1 e 3 (inclusi) allora fai la GET a pazientePrenotaVisita */
        return (step == 1 || step == 2 || step == 3 ) ? "pazientePrenotaVisita" : "errorPage";

    }




    /**
     *  #########################
     *  #########################
     *  POST step: avviene sempre dopo la GET di un determinato step
     *  #########################
     *  #########################
     *  */
    @PostMapping("/step2")
    public String step2(
            @RequestParam String primaDataDisponibile,
            @RequestParam String primoOrarioDisponibile,
            @RequestParam String medicoCandidato,
                        HttpSession session,
                        Model model) {


//        session.setAttribute("primaDataDisponibile", primaDataDisponibile);
//        session.setAttribute("primoOrarioDisponibile", primoOrarioDisponibile);

        model.addAttribute("medicoCandidato", medicoCandidato);
        model.addAttribute("step", 3);


        return "pazientePrenotaVisita";
    }


    /**
     *   Ultimo stadio della prenotazione: intercetta la chiamata POST quando
     *   viene premuto il button: <input type="submit" name="conferma" value="conferma"/> del
     *   form avente action "conferma".
     *      */
    @PostMapping("/conferma")
    public String confermaPrenotazione(HttpSession session,
                                       RedirectAttributes redirectAttributes) {


        // TODO:  Logica di salvataggio VISITA + PRENOTAZIONE

        /// 3) Recupero dati dalla sessione
        Visita nuovaVisita = (Visita) httpSession.getAttribute("nuovaVisita");
        Prenotazione nuovaPrenotazione = (Prenotazione) httpSession.getAttribute("nuovaPrenotazione");
//
//        Visita nuovaVisita = (Visita) session.getAttribute("nuovaVisita");
//        Prenotazione nuovaPrenotazione = (Prenotazione) session.getAttribute("nuovaPrenotazione");

        /// 4) Salvataggio dati a database
        visitaService.salvaVisitaAndPrenotazione( nuovaVisita, nuovaPrenotazione  );

        // 🔴 Pulisci la sessione!
        session.removeAttribute("nuovaVisita");
        session.removeAttribute("nuovaPrenotazione");
        session.removeAttribute("slotDisponibile");

        // TODO: prova a rimuovere facendo httpSession.removeAttribute( ... )
        httpSession.removeAttribute("nuovaVisita");
        httpSession.removeAttribute("nuovaPrenotazione");
        httpSession.removeAttribute("slotDisponibile");


        redirectAttributes.addFlashAttribute("success", "Prenotazione completata!");

        return "redirect:/paziente/visite?effettuata=false";
    }





    private Optional<SlotDisponibile> getSlotDisponibile(Model model, Prestazione prestazione, String usernamePazientePrenotante, String nomePazientePrenotante, String cognomePazientePrenotante) {
        Date oggi = new Date();
        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaSlotDisponibile( prestazione.getDurataMedia(),
                        oggi, // data odierna
                        //LocalTime.now(), // orario attuale
                        getRightOraDiPartenza(usernamePazientePrenotante, prestazione, oggi),
                        medicoService.getAllMedici(), // lista di medici del sistema
                        pianificazioneComponent.getAllVisiteByData( oggi ) ); // lista di visite odierne

        slotDisponibile.ifPresent(slot -> {
            model.addAttribute("primaDataDisponibile", slot.getData() )
                    .addAttribute("primoOrarioDisponibile", slot.getOrario() )
                    .addAttribute("medicoCandidato", slot.getMedico().getNominativo()   )
                    .addAttribute("nomePaziente", nomePazientePrenotante)
                    .addAttribute("cognomePaziente", cognomePazientePrenotante); // Notare l'uso del method chaining
            // Log dello slot a console
            System.out.println( slotDisponibile.get() );

            // Aggiungo alla sessione lo slot, per questione di comodità
            httpSession.setAttribute("slotDisponibile", slotDisponibile.get());
        });
        return slotDisponibile;
    }

    /** Questo metodo risolve il "Problema delle sovrapposizioni temporali":
     *  Siano U=utente, V=visita, X=giorno, Y=orario, L=lista di visite prenotate da U in X, allora:
     *  Se U ha prenotato V per X alle Y, allora bisogna chiamare
     *      trovaSlotDisponibile() con oraPartenza=L.last.ora.calcolaOraFine()
     */
    public LocalTime getRightOraDiPartenza(String username, Prestazione prestazione, Date oggi) {
        return visitaService.utenteOggiHaGiaPrenotatoAlmenoUnaVisita(username, oggi) ?
                    visitaService.getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username)
                        .get(visitaService.getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username).size()-1)
                            .getOra().toLocalTime().plusMinutes( Math.round( prestazione.getDurataMedia() ) ) :
                LocalTime.now();
    }


}
