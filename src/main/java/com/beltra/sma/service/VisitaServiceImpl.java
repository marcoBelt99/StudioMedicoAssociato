package com.beltra.sma.service;

import com.beltra.sma.components.CalcolatoreAmmissibilitaComponent;
import com.beltra.sma.components.PianificazioneComponent;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.*;
import com.beltra.sma.repository.VisitaRepository;
import com.beltra.sma.utils.SlotDisponibile;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;



/** In questa classe si fa parecchio uso della classe "operazionale" Service: {@link PianificazioneComponent} */
@Service
public class VisitaServiceImpl implements VisitaService {

    private final VisitaRepository visitaRepository;
    private final UtenteService utenteService;
    private final PianificazioneComponent pianificazioneComponent;
    private final MedicoServiceImpl medicoServiceImpl;

    public VisitaServiceImpl(VisitaRepository visitaRepository,
                             CalcolatoreAmmissibilitaComponent calcolatoreAmmissibilitaComponent,
                             PianificazioneComponent pianificazioneComponent,
                             UtenteService utenteService,
                             MedicoService medicoService, MedicoServiceImpl medicoServiceImpl) {
        this.visitaRepository = visitaRepository;
        this.pianificazioneComponent = pianificazioneComponent;
        this.utenteService = utenteService;
        this.medicoServiceImpl = medicoServiceImpl;
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
    public Visita salvaVisita(Visita visita) {
        return visitaRepository.save(visita);
    }







    /** TODO:  */
    @Override
    public Visita createVisita(String usernameUtente, Prestazione prestazione) {

        // TODO: 1) trovi il primo slot dispobibile

        // TODO: 2) Fai uso del componente Pianificazione

        // TODO: 3) Inserimento transazionale di VISITA + PRENOTAZIONE
        //          Nota che la prenotazione va salvata con la data attuale
        //          Invece, la data visita viene calcolata in base alle logiche
        //          del sistema.


        // Recupero l'utente attualmente connesso,per poterlo assegnare alla prenotazione.
        Utente utente = utenteService.getUtenteByUsername( usernameUtente );

        // Recupero la data di oggi (attuale, di questo momento)
        Date dataAttuale = new Date();

        // TODO: Inizio a creare la visita
        Visita nuovaVisita = new Visita();
        nuovaVisita.setPrestazione( prestazione );



        // TODO: A che ora la assegno?? Chiamata a metodo trovaPrimoSlotDisponibile() del calcolatoreAmmissibilitaComponent.
        //Optional<SlotDisponibile> slotDisponibile = calcolatoreAmmissibilitaComponent.trovaPrimoSlotDisponibile( prestazione.getDurataMedia(), data );





        // Trova il primo slot disponibile
        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaSlotDisponibile( nuovaVisita.getPrestazione().getDurataMedia(), new Date(), LocalTime.now(), medicoServiceImpl.getAllMedici() ,getAllVisiteByData( dataAttuale ) );

        if (slotDisponibile.isPresent()) {
            // Pianifica la visita nello slot disponibile
            SlotDisponibile slot = slotDisponibile.get();
            nuovaVisita.setDataVisita(slot.getData());
            nuovaVisita.setOra(slot.getOrario());
            nuovaVisita.setAnagrafica( slot.getMedico().getAnagrafica() ); // setto il medico
        }


        // SE NON È PRESENTE LO SLOT, ALLORA  METTO LA VISITA  IN FONDO ALLA LISTA...

        // Salva la nuova visita
        nuovaVisita = salvaVisita( nuovaVisita );
        return nuovaVisita;
    }


}
