package com.beltra.sma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarioController {

    @GetMapping("/calendario")
    public String calendario() {
        return "calendarioTestDEPRECATO";
    }
}
