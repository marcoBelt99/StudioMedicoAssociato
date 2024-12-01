package com.beltra.sma.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Log
@Controller
public class ErrorsController implements ErrorController {

    @GetMapping("/error")
    @SneakyThrows
    public String handleError(HttpServletRequest httpRequest,
                              HttpServletResponse httpResponse) {

        String errorMsg = "";
        int httpErrorCode = httpResponse.getStatus(); // TODO: recupero lo stato della risposta

        switch (httpErrorCode) {
            case 400:
                errorMsg = "Codice errore 400. Bad Request";
                break;
            case 401:
                errorMsg = "Codice errore 401. Non Autorizzato";
                break;
            case 404:
                errorMsg = "Codice errore 404. Risorsa Non Trovata";
                return "error404"; //TODO: in caso di errore 404 ritorno una specifica vista dedicata a questo errore
            case 500:
                errorMsg = "Codice errore 500. Errore Interno del Server";
                break;
        }

        log.warning( errorMsg );

        // TODO: ritorno una pagina di errore generica
        return "errorPage";
    }



}
