package com.beltra.sma.service;

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
import com.beltra.sma.components.PianificazioneComponent;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.*;
import com.beltra.sma.repository.VisitaRepository;
import com.beltra.sma.utils.SlotDisponibile;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;



/** In questa classe si fa parecchio uso della classe "operazionale" Service: {@link PianificazioneComponent} */
@Service
public class VisitaServiceImpl implements VisitaService {

    private final VisitaRepository visitaRepository;
    private final UtenteService utenteService;
    private final PianificazioneComponent pianificazioneComponent;
    private final PrenotazioneService prenotazioneService;

    public VisitaServiceImpl(VisitaRepository visitaRepository,
                             CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilitaComponent,
                             PianificazioneComponent pianificazioneComponent,
                             UtenteService utenteService,
                             PrenotazioneService prenotazioneService
                             ) {
        this.visitaRepository = visitaRepository;
        this.pianificazioneComponent = pianificazioneComponent;
        this.utenteService = utenteService;
        this.prenotazioneService = prenotazioneService;
    }


    @Override
    public List<Visita> getAll() {
        return visitaRepository.findAll();
    }


    @Override
    public List<VisitaPrenotataDTO> getAllVisite() {
        return visitaRepository.findAllVisiteOrderByDataVisitaDesc();
    }

    @Override
    public List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(String username) {
        return visitaRepository.findAllVisitePrenotateByUsernamePaziente(username, false);
    }

    @Override
    public List<VisitaPrenotataDTO> getAllVisitePrenotateAndEffettuateByUsernamePaziente(String username) {
        return visitaRepository.findAllVisitePrenotateByUsernamePaziente(username, true);
    }

    @Override
    public List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuate() {
        return visitaRepository.findAllVisitePrenotateOrderByDataVisitaAsc(false);
    }

    @Override
    public List<VisitaPrenotataDTO> getVisitePrenotateSettimana() {
        LocalDate oggi = LocalDate.now(); // Inizio da oggi
        LocalDate fineSettimana = oggi.plusDays(7); // Oggi + 7 giorni

        return getAllVisite()
                .stream()
                .filter(vp -> {
                    LocalDate dataVisita = vp.getDataVisita().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate(); // Conversione a LocalDate
                    return !dataVisita.isBefore(oggi) && !dataVisita.isAfter(fineSettimana);
                }) // Filtra le visite tra oggi e 7 giorni da oggi
                .collect(Collectors.toList());
    }

    @Override
    public List<Visita> getAllVisiteOrderByDataVisitaAsc() {
        return visitaRepository.findAllByOrderByDataVisitaDesc();
    }


    @Override
    public List<Visita> getVisiteByAnagraficaMedico(Anagrafica anagrafica) {
        return visitaRepository.findByAnagrafica(anagrafica);
    }


    @Override
    public List<Visita> getAllVisiteByData(Date data) {
        return visitaRepository.findAllByDataVisita(data);
    }

    @Override
    public List<Visita> getAllVisiteStartingFromNow() {
        return visitaRepository.findAllVisiteFromNow();
    }

    @Override
    public List<Visita> getAllVisiteByMedicoAndData(Medico medico, Date data) {
        return visitaRepository.findByAnagraficaAndDataVisita( medico.getAnagrafica(), data );
    }

    @Override
    public Visita creaVisita(Prestazione prestazione, SlotDisponibile slotDisponibile) {
        Visita nuovaVisita = new Visita();
        nuovaVisita.setPrestazione(prestazione);
        nuovaVisita.setDataVisita( slotDisponibile.getData() );
        nuovaVisita.setOra( slotDisponibile.getOrario() );
        Medico medicoDisponibile = slotDisponibile.getMedico();
        nuovaVisita.setAnagrafica(medicoDisponibile.getAnagrafica());
        nuovaVisita.setNumAmbulatorio(medicoDisponibile.getIdAnagrafica().intValue()+1);// chissene frega del numero di ambulatorio... si aprirebbe tutta un'altra questione
        return nuovaVisita;
    }



    @Override
    public Visita salvaVisita(Visita visita) {
        return visitaRepository.save(visita);
    }



    @Override
    public void salvaVisitaAndPrenotazione(Visita visita, Prenotazione prenotazione ) {
        salvaVisita(visita);
        prenotazioneService.salvaPrenotazione(prenotazione);
    }


//    public List<VisitaPrenotataDTO> getAllVisiteGiornalierePrenotateAndNotEffettuateByUsernamePaziente(String username, Date oggi) {
//        return
//                getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username) // Prendo solo le visite dell'utente paziente di interesse.
//                .stream()
//                .filter( v -> v.getDataVisita().compareTo(oggi) == 0) // Ovviamente mi interessa controllare le visite odierne.
//                .toList();
//    }

    public List<VisitaPrenotataDTO> getAllVisiteGiornalierePrenotateAndNotEffettuateByUsernamePaziente(String username, Date oggi) {
        // Converte la data di oggi in LocalDate (senza ora)
        LocalDate oggiLocalDate = oggi.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username).stream()
                .filter(v -> {
                    // Converte anche la data della visita in LocalDate
                    LocalDate dataVisita = v.getDataVisita().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return dataVisita.equals(oggiLocalDate);
                })
                .toList();
    }

    /**  */
    public boolean utenteOggiHaGiaPrenotatoAlmenoUnaVisita(String username, Date oggi) {
        return !getAllVisiteGiornalierePrenotateAndNotEffettuateByUsernamePaziente(username, oggi)
                .isEmpty();
    }





}
