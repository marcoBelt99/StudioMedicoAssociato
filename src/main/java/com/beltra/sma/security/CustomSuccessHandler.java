package com.beltra.sma.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/** Classe che va usata per implementare */
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    /** Ritorna alla pagina di "/index" usando lo username dell'utente */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        /** Implementazione del logout: creo un mio cookie custom di nome 'user-id' */
        ResponseCookie springCookie = ResponseCookie.from("user-id", authentication.getName())
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(600)
                .sameSite("Strict") // La sessione si distrugge quando l'utente chiude il browser
                .build();
        // Quindi, aggiungo un header ed Inserisco il cookie
        response.addHeader(HttpHeaders.SET_COOKIE, springCookie.toString());

        // Redirect alla pagina di index
        response.sendRedirect("/index");
    }
}
