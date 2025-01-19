package com.beltra.sma.service;

import com.beltra.sma.components.PianificazioneComponent;

import com.beltra.sma.dto.VisitaPrenotataDTO;
import com.beltra.sma.model.*;
import com.beltra.sma.repository.MedicoRepository;
import com.beltra.sma.repository.VisitaRepository;
import com.beltra.sma.utils.SlotDisponibile;
import org.springframework.stereotype.Service;


import java.sql.Time;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/** In questa classe si fa parecchio uso della classe "operazionale" Service: {@link PianificazioneComponent} */
@Service
public class VisitaServiceImpl implements VisitaService {

    private final VisitaRepository visitaRepository;
    private final PianificazioneComponent pianificazioneComponent;
    private final UtenteService utenteService;
    private final MedicoService medicoService;

    public VisitaServiceImpl(VisitaRepository visitaRepository,
                             PianificazioneComponent pianificazioneComponent,
                             UtenteService utenteService,
                             MedicoService medicoService) {
        this.visitaRepository = visitaRepository;
        this.pianificazioneComponent = pianificazioneComponent;
        this.utenteService = utenteService;
        this.medicoService = medicoService;
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


    public List<Medico> getMediciFromVisite(List<Visita> visite, MedicoRepository medicoRepository) {
        return visite.stream()
                .map(Visita::getAnagrafica) // Ottieni l'Anagrafica dalla Visita
                .map(Anagrafica::getIdAnagrafica) // Ottieni l'ID dell'Anagrafica
                .distinct() // Evita duplicati di ID
                .map(medicoRepository::findById) // Usa il repository per trovare il Medico
                .filter(Optional::isPresent) // Filtra i Medici non trovati
                .map(Optional::get) // Estrai il Medico dall'Optional
                .collect(Collectors.toList()); // Raccogli i Medici in una lista
    }


    @Override
    public Visita salvaVisita(Visita visita) {
        return visitaRepository.save(visita);
    }

    @Override
    public Visita createVisita(String usernameUtente, Prestazione prestazione) {

        // Recupero l'utente attualmente connesso,per poterlo assegnare alla prenotazione.
        Utente utente = utenteService.getUtenteByUsername( usernameUtente );

        // Recupero la data di oggi (attuale, di questo momento)
        Date dataAttuale = new Date();

        // Inizio a creare la visita
        Visita nuovaVisita = new Visita();
        nuovaVisita.setPrestazione( prestazione );
        /*


        // Setto i campi della nuova visita, sulla base delle visite che intercorrono da oggi all'ultima visita creata
        // Recupero la lista di tutte le visite a partire dalla data di oggi fino all'ultima visita creata (quella che ha data più recente)
        List<Visita> listaVisitePartendoDaOggi = getAllVisiteStartingFromNow();

        // Per comodità, uso un array
        Visita[] arrayVisitePartendoDaOggi = listaVisitePartendoDaOggi.toArray(new Visita[0]);
        for(int i=0; i < arrayVisitePartendoDaOggi.length; i++) {
            // Scarto il primo elemento
            if(i>0) {

                //Time orarioCandidato = arrayVisitePartendoDaOggi[i-1].getOra().toLocalTime().plus
                //nuovaVisita.setOra(  );
                // TODO: ...
            }
        }



        // TODO: A che ora la assegno?? Chiamata a metodo trovaPrimoSlotDisponibile() del pianificazioneComponent.
        //Optional<SlotDisponibile> slotDisponibile = pianificazioneComponent.trovaPrimoSlotDisponibile( prestazione.getDurataMedia(), data );



        // Se è presente, inserisco la visita in quella data
        return Optional.empty();

         */



        // Trova il primo slot disponibile
        Optional<SlotDisponibile> slotDisponibile =
                pianificazioneComponent.trovaPrimoSlotDisponibile( nuovaVisita.getPrestazione().getDurataMedia(), getAllVisiteByData( dataAttuale ) );

        if (slotDisponibile.isPresent()) {
            // Pianifica la visita nello slot disponibile
            SlotDisponibile slot = slotDisponibile.get();
            nuovaVisita.setDataVisita(slot.getData());
            nuovaVisita.setOra(slot.getOrario());
            nuovaVisita.setAnagrafica( slot.getMedico().getAnagrafica() ); // setto il medico
        } else {
            // Recupera l'ultima visita esistente
            List<Visita> listaVisitePartendoDaOggi = this.getAllVisiteStartingFromNow();

            listaVisitePartendoDaOggi.sort(Comparator.comparing(Visita::getDataVisita).thenComparing(Visita::getOra));

            if (!listaVisitePartendoDaOggi.isEmpty()) {
                Visita ultimaVisita = listaVisitePartendoDaOggi.get( listaVisitePartendoDaOggi.size() - 1);
                LocalTime fineUltimaVisita = pianificazioneComponent.aggiungiDurata( ultimaVisita.getOra().toLocalTime(), ultimaVisita.getPrestazione().getDurataMedia() );
                Date dataUltimaVisita = ultimaVisita.getDataVisita();

                // Pianifica la nuova visita dopo l'ultima
                nuovaVisita.setDataVisita(dataUltimaVisita);
                nuovaVisita.setOra(  Time.valueOf( fineUltimaVisita )  );
                nuovaVisita.setAnagrafica( ultimaVisita.getAnagrafica() );
            } else {
                // Nessuna visita pianificata, pianifica la nuova visita nel primo giorno utile
                Calendar calendar = Calendar.getInstance();
                calendar.setTime( dataAttuale);

                while (!pianificazioneComponent.isGiornoAmmissibile(calendar.getTime())) {
                    calendar.add(Calendar.DATE, 1);
                }

                nuovaVisita.setDataVisita(calendar.getTime());
                nuovaVisita.setOra(Time.valueOf("07:00:00")); // Orario di inizio lavoro

                Anagrafica anagraficaMedico =
                        medicoService.getAllMedici()
                                .stream()
                                .map(Medico::getAnagrafica)
                                .toList()
                                .get(0);


                nuovaVisita.setAnagrafica( anagraficaMedico ); // Assegna il primo medico disponibile
            }
        }

        // Salva la nuova visita
        nuovaVisita = salvaVisita( nuovaVisita );
        return nuovaVisita;
    }


    public VisitaRepository getVisitaRepository() {
        return visitaRepository;
    }
}
