package com.beltra.sma.security;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    /** @Bean Necessario per salvare a DB la password inserita in modo criptato
     *  lo richiamo in UtenteServiceImpl
     * */
    @Bean
    @SneakyThrows
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /** TODO:  */
    // Code Injection
    private UserDetailsService userDetailsService;

    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /** TODO: permette di fare configurazioni globali del mio sistema di autenticazione */
    @Autowired
    @SneakyThrows
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService( userDetailsService ) // TODO: quale userDetailsService sto usando
            .passwordEncoder( new BCryptPasswordEncoder() );  // TODO: sistema di criptazione che utilizzo
    }




    /** TODO: Uno dei metodi più importanti, perchè <b><u>mi consente di configurare i diversi aspetti che riguardano la sicurezza
     *        della mia applicazione</u></b>.
     *        <br>
     *        1. La prima configurazione che setto con questo bean è quella che riguarda gli endpoint che l'utente può accedere senza
     *        autenticarsi, e quindi accedere in modalità completamente anonima.
     *        <br>
     *        2. Poi specificherò a quali endpoint potrà accedere il relativo utente user,
     *        <br>
     *        3. Il relativo utente admin
     *        <br>
     *        ecc..
     *        */
    @Bean
    @SneakyThrows
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeRequests( authz ->
        {
        /** Endpoints che si possono accedere in maniera anonima */
            authz.requestMatchers(
                    new AntPathRequestMatcher("/login/**"), // endpoint "/login/**" è accessibile in maniera del tutto anonima
                    new AntPathRequestMatcher("/js/**"),    // anche gli endpoint che mi permettono di accedere alle risorse
                    new AntPathRequestMatcher("/css/**"),   // i ** indicano "qualsiasi ulteriore path sia presente Es) in /css/"
                    new AntPathRequestMatcher("/plugins/**"),
                    new AntPathRequestMatcher("/svg/**"),
                    new AntPathRequestMatcher("/fonts/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/benvenuto"), // test di pagina che può vedere un utente anonimo (e' mappata da un opportuno controller  chiamato PublicController)
                    new AntPathRequestMatcher("/prestazioni/all"), // endpoint della pagina di benvenuto
                    new AntPathRequestMatcher("/chiSiamo"),
                    new AntPathRequestMatcher("/api/message") // test per verificare se Vue JS funziona, toglilo pure questo in futuro
            ).permitAll(); // per poter accedere in modalità anonima

        //TODO: endpoint che sono accessibili <b>solo da utente medico (che sarebbe l'amministratore)</b>
            authz.requestMatchers(

                    //new AntPathRequestMatcher("/visite/all"),
                    new AntPathRequestMatcher("/medico/visite/**")
                    //new AntPathRequestMatcher("/visitePaziente")
            ).hasRole("MEDICO"); // tutti questi endpoint sono accessibili solo dall'utente che ha il ruolo di MEDICO

            // TODO: fare per il paziente
            authz.requestMatchers(
                    new AntPathRequestMatcher("/paziente/visite/**"),
                    new AntPathRequestMatcher("/paziente/prenota/**")
            ).hasRole("PAZIENTE");

            // TODO: fare per infermiere
            // ...

            // TODO: qualsiasi altra richiesta che riguardi qualunque altro endpoint devo essere
            //       almeno autenticato (cioè aver inserito username e password)
            authz.anyRequest().authenticated(); // tutto il resto dell'app (tranne gli endpoint specifici esclusivi per ADMIN) è accessibile da un utente che abbia fatto log-on
        })


    // TODO: specifico quale form di login devo usare per permettere l'inserimento di username
    //       e password --> non devo usare il form di login di default ovviamente,
    //       ma il mio form di login !!!
        .formLogin(
                form -> form
                .loginPage("/login")                // TODO: endpoint che sarà usato per poter poter apripre la mia pagina di login
                .loginProcessingUrl("/autentica")   // TODO: "/autentica" rappresenta nella vista il blocco di codice dentro la vista Thymeleaf del login:
                                //  ...
                                // <form action=# th:action="@{/autentica}" method=post>
                                // ...
                                                    // TODO: "/autentica" gestisce il th:action che dice "quando fai il submit del form viene lanciato '/autentica'", che e' contenuto sempre dentro l'endpoint "/login" (infatti nella action dell'HTML ho #)

                .usernameParameter("name")          // TODO: rappresenta la mappatura tra il l'attributo name HTML del campo in cui inserisco lo username e il metodo
                                                    //       usernameParameter("name") ==> "name" deve essere uguale sia in questo metodo, sia nell'attributo "name" dell'HTML della vista.
                                                    // Se invece avessi usato il nome di default (che è "username") non avrei dovuto richiamare questo metodo
                // .passwordParameter("password")   // Lo stesso principio vale per il metodo passwordParameter("password"), che di default vale "password"
                                                    // e siccome io nell'HTML come attributo name del campo della password ho usato "password", non è necessario richiamarlo


                .successHandler( authenticationSuccessHandler() ) // TODO: gestore dell'evento successo: ho inserito username e password, quello che ho inserito
                                                                  //  è coerente con quanto ho specificato nelle configurazioni, in questo caso, del metodo che ho creato
                                                                 //   userDetailsManager(), quindi la mia applicazione in caso di successo mi deve far andare in una determinata pagina
                                                                 //   questo evento è gestito da un opportuno metodo che creo, in questo caso chiamato authenticationSuccessHandler(),
                                                                 //   che mi creo nella classe CustomSuccessHandler.
                                                                 //   ==> Devo riportare in questa classe ( SecurityConfiguration ) il Bean che istanzia un nuovo oggetto di tipo CustomSuccessHandler.

                .failureHandler( authenticationFailureHandler() ) // TODO: gestore dell'evento di fallimento: funzione opposta rispetto al gestore del successo.
                                                                  //  Se inserisco username e password errati devo gestire in modo opportuno questo evento attraverso
                                                                  //  il metodo authenticationFailureHandler() ---> creo la classe CustomAuthenticationFailureHandler
                                                                  //  ==> Devo riportare in questa classe ( SecurityConfiguration ) il Bean che istanzia un nuovo oggetto di tipo CustomAuthenticationFailureHandler


                )                                              // TODO: gestisco i casi nei quali tento di entrare in una pagina in cui non posso entrare, perchè ad es., non sono amministratore
            .exceptionHandling( ex -> ex                       //  infatti ho predisposto sopra una serie di endpoint che non sono accessibili all'utente con role "USER"
                .accessDeniedHandler( accessDeniedHandler() )  //  --> aggiungo la parte di gestione degli errori, che mi permette di attivare l'accessDeniedHandler
                                                               //  --> richiede di creare la classe CustomAccessDeniedHandler
                                                               //  ==> Devo riportare in questa classe ( SecurityConfiguration ) il Bean che istanzia un nuovo oggetto di tipo CustomAuthenticationFailureHandler


                /** TODO: Cambio il comporatmento di default: di default se non sei autenticato devi aprire l'app nell'endpoint che ti specifico qui sotto */
                .authenticationEntryPoint( (request, response, authException) -> {
                    response.sendRedirect("/benvenuto");
                })
        ) // ;
    // TODO: ho finito con i parametri di configurazione di base


    // TODO: di seguito alcuni tentativi per risolvere il problema della sessione sporca a causa del csrf

    // Sessione
        .sessionManagement(
                httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))


        .csrf(csrf -> csrf
                .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
        )

    // Indicazione esplicita della pagina di logout: RIPULISCO SIA LA SESSIONE CHE I COOKIE DI SESSIONE
        .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID","user-id")

        );

    // TODO: restituisco http.build()
        return http.build();
    }








    /** Crea il Bean che istanza la {@link CustomSuccessHandler} che mi fa saltare nella pagina index passando lo username
     *  <br>
     *  In particolare mi consente di usare in questa classe il metodo authenticationSuccessHandler()
     * */
    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSuccessHandler();
    }


    /** Crea il Bean che istanza la {@link CustomAuthenticationFailureHandler} che mi fa saltare nella pagina index passando lo username
     *  <br>
     *  In particolare mi consente di usare in questa classe il metodo onAuthenticationFailure()
     * */
    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }


    /** Crea il Bean che istanza la {@link CustomAccessDeniedHandler} che mi fa saltare nella pagina index passando lo username
     *  <br>
     *  In particolare mi consente di usare in questa classe il metodo handle()
     * */
    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}