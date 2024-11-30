package com.beltra.sma.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logout")
public class LogoutController {


    /** Questo metodo va ad eliminare qualsiasi traccia della sessione in corso. */
    @GetMapping()
    public String getLogout(
            HttpServletRequest request,
            HttpServletResponse response) {

        /** Ripulisco la sessione (la invalido) */
        HttpSession session = request.getSession(false);
        SecurityContextHolder.clearContext();

        session = request.getSession(false);

        if(session != null) {
            session.invalidate();
        }

        /** Ciclo su qualsiasi cookie sia presente imponendogli un maxAge a 0  */
        for(Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }

        /** Elimino fisicamente il cookie dove ho memorizzato lo username dell'utente */
        ResponseCookie springCookie = ResponseCookie.from("user-id", "")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, springCookie.toString());

        /** Redirect sulla schermata di logout con un parametro  logout=true */
        return "redirect:/login?logout=true";



    }
}
