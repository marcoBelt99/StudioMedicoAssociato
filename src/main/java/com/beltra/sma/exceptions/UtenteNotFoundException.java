package com.beltra.sma.exceptions;

public class UtenteNotFoundException extends RuntimeException {

    public UtenteNotFoundException(String message) {
        super(message); // Passo il messaggio al costruttore della superclasse
    }

    public UtenteNotFoundException(String message, Throwable cause) {
        super(message, cause); // Passa il messaggio e la causa alla superclasse
    }
}