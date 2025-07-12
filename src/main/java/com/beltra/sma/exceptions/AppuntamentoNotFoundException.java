package com.beltra.sma.exceptions;

public class AppuntamentoNotFoundException extends RuntimeException {
    public AppuntamentoNotFoundException(String message) {
        super(message); // Passo il messaggio al costruttore della superclasse
    }

    public AppuntamentoNotFoundException(String message, Throwable cause) {
        super(message, cause); // Passa il messaggio e la causa alla superclasse
    }
}
