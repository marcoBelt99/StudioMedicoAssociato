package com.beltra.sma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/forbidden")
public class ForbiddenController {

    /** Ritorna la vista "forbidden" laddove l'endpoint sia: "/forbidden" */
    @GetMapping()
    public String getPage() {

        return "forbidden";
    }
}
