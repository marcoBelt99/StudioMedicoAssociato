package com.beltra.sma.service;

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
import com.beltra.sma.components.PianificazioneComponent;

import com.beltra.sma.dto.AppuntamentiSettimanaliMedicoDTO;
import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.*;
import com.beltra.sma.repository.VisitaRepository;
import com.beltra.sma.utils.SlotDisponibile;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;



/** In questa classe si fa parecchio uso della classe "operazionale" Service: {@link PianificazioneComponent} */
@Service
public class VisitaServiceImpl implements VisitaService {

    private final VisitaRepository visitaRepository;
    private final PrenotazioneService prenotazioneService;

    public VisitaServiceImpl(VisitaRepository visitaRepository,
                             PrenotazioneService prenotazioneService
                             ) {
        this.visitaRepository = visitaRepository;
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
    public List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePazienteByData(String username, Date dataDiRicerca) {
        return visitaRepository.findAllVisitePrenotateByUsernamePazienteByDataVisita(username, false, dataDiRicerca);
    }



    @Override
    public List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuate() {
        return visitaRepository.findAllVisitePrenotateOrderByDataVisitaAsc(false);
    }

//    @Override
//    public List<VisitaPrenotataDTO> getVisitePrenotateSettimana() {
//        LocalDate oggi = LocalDate.now(); // Inizio da oggi
//        LocalDate fineSettimana = oggi.plusDays(7); // Oggi + 7 giorni
//
//        return getAllVisite()
//                .stream()
//                .filter(vp -> {
//                    LocalDate dataVisita = vp.getDataVisita().toInstant()
//                            .atZone(ZoneId.systemDefault())
//                            .toLocalDate(); // Conversione a LocalDate
//                    return !dataVisita.isBefore(oggi) && !dataVisita.isAfter(fineSettimana);
//                }) // Filtra le visite tra oggi e 7 giorni da oggi
//                .collect(Collectors.toList());
//    }

    @Override
    public List<Visita> getAllVisiteOrderByDataVisitaAsc() {
        return visitaRepository.findAllByOrderByDataVisitaDesc();
    }


    @Override
    public List<Visita> getVisiteByAnagraficaMedico(Anagrafica anagrafica) {
        return visitaRepository.findAllByAnagrafica(anagrafica);
    }


    @Override
    public List<Visita> getAllVisiteByData(Date data) {
        return visitaRepository.findAllByDataVisita(data);
    }

    @Override
    public List<Visita> getAllVisiteStartingFromNow() {
        return visitaRepository.findAllVisiteFromNow();
    }

//    @Override
//    public List<Visita> getAllVisiteByMedicoAndData(Medico medico, Date data) {
//        return visitaRepository.findByAnagraficaAndDataVisita( medico.getAnagrafica(), data );
//    }



    @Override
    public List<Map<String, String>> getAppuntamentiSettimanaliMedicoListaMappe(String username, Date dataInizio, Date dataFine) {

        List<AppuntamentiSettimanaliMedicoDTO> listaAppuntamentiGrezza = getAppuntamentiSettimanaliMedicoLista(username, dataInizio, dataFine);

        return listaAppuntamentiGrezza.stream()
                .map(this::convertToScheduleXEvent)
                .toList();
    }

    @Override
    public List<AppuntamentiSettimanaliMedicoDTO> getAppuntamentiSettimanaliMedicoLista(String username, Date dataInizio, Date dataFine) {
        return visitaRepository.findAppuntamentiSettimanaliMedico(username, dataInizio, dataFine);
    }

    private Map<String, String> convertToScheduleXEvent(AppuntamentiSettimanaliMedicoDTO app) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Map<String, String> mappaEventoScheduleX = new HashMap<>();
        mappaEventoScheduleX.put("id", app.getIdVisita().toString());
        mappaEventoScheduleX.put("title", app.getTitoloPrestazione() + " - " + app.getNomePaziente() + " " + app.getCognomePaziente());
        mappaEventoScheduleX.put("start", dateFormat.format(app.getDataVisita()) + " " + timeFormat.format(app.getOraInizioVisita()));
        mappaEventoScheduleX.put("end", dateFormat.format(app.getDataVisita()) + " " + timeFormat.format(app.getOraFineVisita()));

        return mappaEventoScheduleX;
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
    public void salvaVisitaAndPrenotazione(Visita visita, Prenotazione prenotazione ) {
        salvaVisita(visita);
        prenotazioneService.salvaPrenotazione(prenotazione);
    }

    @Override
    public Visita salvaVisita(Visita visita) {
        return visitaRepository.save(visita);
    }

    public boolean utenteOggiHaGiaPrenotatoAlmenoUnaVisita(String username, Date oggi) {
        // Direttamente a DB
        return !getAllVisitePrenotateAndNotEffettuateByUsernamePazienteByData(username, oggi).isEmpty();
    }









}
