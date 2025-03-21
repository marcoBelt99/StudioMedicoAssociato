package com.beltra.sma.controller;

import com.beltra.sma.service.PrestazioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

@Controller
public class PublicController {

    private final PrestazioneService prestazioneService;

    PublicController(PrestazioneService prestazioneService) {
        this.prestazioneService = prestazioneService;
    }

    @GetMapping("/benvenuto")
    public String benvenuto(Model model) {

        model.addAttribute("titolo", "Benvenuto!");
        model.addAttribute("sottotitolo", "Ecco l'elenco dei servizi offerti dallo Studio:");

        model.addAttribute("prestazioni",
                prestazioneService.getAllPrestazioniDisponibili()
        );

        return "benvenuto";  // assicura che esista una vista benvenuto.html in resources/templates
    }


    @GetMapping("/chiSiamo")
    public String chiSiamo(Model model) {
        model.addAttribute("titolo", "SMA-RT: lo Studio Medico Associato intelligente");
        return "chiSiamo";
    }



    // test
    @GetMapping("/api/message")
    @ResponseBody  // 🔹 Indica che il metodo deve restituire direttamente il JSON
    public  ResponseEntity<Map<String, String>> getMessage() {
        System.out.println("PASSATO DAL BACKEND");
        Map<String, String> response = Collections.singletonMap("message", "Messaggio dal backend!");
        return ResponseEntity.ok(response);
    }

}