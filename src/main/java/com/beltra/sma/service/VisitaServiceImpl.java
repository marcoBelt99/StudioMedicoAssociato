package com.beltra.sma.service;


import com.beltra.sma.dto.AppuntamentoSettimanaleMedicoDTO;
import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.exceptions.AppuntamentoNotFoundException;
import com.beltra.sma.model.*;
import com.beltra.sma.repository.VisitaRepository;
import com.beltra.sma.utils.SlotDisponibile;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;





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



    @Override
    public List<Map<String, Object>> getAppuntamentiSettimanaliMedicoListaMappe(String username, Date dataInizio, Date dataFine) {

        List<AppuntamentoSettimanaleMedicoDTO> listaAppuntamentiGrezza = getAppuntamentiSettimanaliMedicoLista(username, dataInizio, dataFine);

        return listaAppuntamentiGrezza.stream()
                .map(this::convertToScheduleXEvent)
                .toList();
    }

    @Override
    public List<AppuntamentoSettimanaleMedicoDTO> getAppuntamentiSettimanaliMedicoLista(String username, Date dataInizio, Date dataFine) {
        return visitaRepository.findAppuntamentiSettimanaliMedico(username, dataInizio, dataFine);
    }

    private Map<String, Object> convertToScheduleXEvent(AppuntamentoSettimanaleMedicoDTO app) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Map<String, Object> mappaEventoScheduleX = new HashMap<>();
        mappaEventoScheduleX.put("id", app.getIdVisita().toString());
        mappaEventoScheduleX.put("title", app.getTitoloPrestazione());
        mappaEventoScheduleX.put("people",  Arrays.asList(app.getNomePaziente() + " " + app.getCognomePaziente()));
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



public AppuntamentoSettimanaleMedicoDTO getAppuntamentoById(Long idVisita, String usernameMedico, Date dataInizio, Date dataFine) throws AppuntamentoNotFoundException {
        /*
        return getAppuntamentiSettimanaliMedicoLista(usernameMedico, dataInizio, dataFine).stream()
            .filter(a -> a.getIdVisita().equals(idAppuntamento))
            .findFirst()
            .orElseThrow(() -> new AppuntamentoNotFoundException("Appuntamento non presente."));
         */

    return visitaRepository.findDettaglioAppuntamentoSettimanaleMedico(idVisita, usernameMedico, dataInizio, dataFine);
}


//    public AppuntamentoSettimanaleMedicoDTO getAppuntamentoById(Long idAppuntamento) {
//        visitaRepository.findById(idAppuntamento);
//    }



}
