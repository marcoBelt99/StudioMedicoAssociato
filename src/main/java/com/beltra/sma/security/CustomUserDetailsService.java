package com.beltra.sma.security;

import com.beltra.sma.model.Utente;
import com.beltra.sma.repository.UtenteRepository;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/** Implementa {@link UserDetailsService}. */
@Log
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Code Injection
    UtenteRepository utenteRepository;

    public CustomUserDetailsService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }


/** Ritorna uno UserDetails e come parametro prende lo username.  */
    @Override
    @SneakyThrows // per far gestire le eccezioni al lombok
    public UserDetails loadUserByUsername(String username) {

        String errMsg = "";

        /** Voglio che sia stato passato uno username e che sia > 5 caratteri */
        if(username == null || username.length() < 5) {
            errMsg = "Nome utente assente o non valido";
            log.warning(errMsg);
            throw new UsernameNotFoundException(errMsg);
        }

        /** Ora devo ottenere le informazioni dell'utente tramite lo username */
        Utente utente  = this.getUtente(username);

        /** Se non ho trovato l'utente */
        if(utente == null) {
            errMsg = String.format("Utente %s non valido!!", username);
            log.warning(errMsg);
            throw new UsernameNotFoundException(errMsg);
        }

        /** Se ho trovato l'utente ==> costruisco uno UserBuilder passandogli: username, password, attivo. * */
        User.UserBuilder userBuilder = null;
        userBuilder = User.withUsername( utente.getUsername().trim() );
        userBuilder.password( utente.getPassword().trim() );
        userBuilder.disabled( !utente.isAttivo() ); // l'utente e' disabilitato quando il campo attivo vale false. (Di default vale true).

        /** Gestione collezione dei ruoli: creazione delle authorities */
        String[] profili = utente.getRuoli()
                .stream()
                .map( r -> "ROLE_" + r.getTipo().trim() )
                .toArray( String[]::new);

        userBuilder.authorities( profili );

        /** Restituisco il builder */
        return userBuilder.build();

    }


    /** Recupero l'utente tramite il suo username */
    private Utente getUtente(String username) {
        Utente utente = null;

        try {
            utente = this.utenteRepository.findByUsername(username);
        } catch (Exception e) {
            String errMsg = "Errore nel tentativo di ricerca dell'utente.";
            log.warning(errMsg);
        }
        return utente;
    }


}
