package com.beltra.sma.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /** Effettua un redirect sull'endpoint "/forbidden"
     * <br>
     *  Tale endpoint porta ad una specifica pagina che dirà che l'utente non è abilitato ad accedere
     *  a quella particolare parte del sito.
     * */

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendRedirect("/forbidden");
    }
}
