package com.beltra.sma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

//    /** Variabile che posso passare alla vista tramite Model e metodo addAttribute() */
//    private String saluti = "Saluti, sono la tua prima applicazione web creata in Spring Boot 3 e Thymeleaf";;
//
//
//
//    /** Ho scritto questo metodo per poter esercitarmi con gli unit test
//     * <br>
//     *  Se, come in questo esempio, al RequestMapping non specifico il tipo di metodo,
//     *  per impostazione predefinita mi mappa tutte le operazioni HTTP.
//     * */
//    @RequestMapping("/saluto")
//    public @ResponseBody String getSaluto() {
//        return "Allenamento per Unit Test !!";
//    }
//
//
//
//
//    /** Metodo che gestisce la root page della mia app
//     * @param model: permette di passare alcuni attributi
//     * */
//    @GetMapping("/")
//    public String getWelcome(Model model) {
//
//        model.addAttribute("intestazione", "Benvenuto/a nella root page dello Studio Medico Associato");
//        model.addAttribute("titolo", "HomePage");
//        model.addAttribute("saluti", saluti);
//
//        // TODO
//        return "index";
//    }
//
//
//
//
//    /** TODO: metodo necessario per l'autenticazione.
//     *  <br>
//     *  Senza questo metodo, una volta inserito userame e password nel form di login,
//     *  non sarei in grado di vedere la pagina di index
//     * */
//    @GetMapping(value = "index/{name}")
//    public String getWelcome1(@PathVariable String name,
//                              Model model) {
//        model.addAttribute("intestazione", String.format("Benvenuto %s nella index page della webapp Studio Medico Associato", name) );
//        model.addAttribute("saluti", saluti);
//
//        return "index";
//    }
//
//
//    /** TODO: metodo per usare il cookie custom */
//    @GetMapping(value = "index")
//    public String getWelcome2(Model model,
//                              @CookieValue(name = "user-id") String userId) {
//        model.addAttribute("intestazione", String.format("Benvenuto %s nella index page della webapp Studio Medico Associato", userId) );
//        model.addAttribute("saluti", saluti);
//
//        // TODO: tecnicamente, ora non dovrebbe più essere necessario usare il cookie, dal momento che l'ho centralizzato
//        //model.addAttribute("userName", userId);
//
//        return "index";
//    }



    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("saluto", "Ciao, tutto funziona!");
        return "index";
    }


}
