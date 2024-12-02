package com.beltra.sma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping("/benvenuto")
    public String benvenuto(Model model) {
        return "redirect:prestazioni/all";  // assicura che esista una vista benvenuto.html in resources/templates
    }
}