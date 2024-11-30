package com.beltra.sma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@SessionAttributes("name")
public class LoginController {

    private String titolo = "Accesso & Autenticazione";
    private String sottotitolo = "Procedi ad inserire lo username e la password";
    private String errMsg = "Spiacente, lo username o la password non sono corretti";


    @GetMapping(value = "/login")
    public String getLogin(Model model) {

        model.addAttribute("titolo", titolo);
        model.addAttribute("sottotitolo", sottotitolo);
        model.addAttribute("errmsg", errMsg);

        // ritorno il nome della vista di login
        return "login";
    }


}
