package com.beltra.sma.controller;

import com.beltra.sma.service.VisitaService;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/medici")
public class MedicoController {

    final VisitaService visitaService;

    MedicoController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    // TODO: ...



}
