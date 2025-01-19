package com.beltra.sma.components;


import com.beltra.sma.model.Medico;
import com.beltra.sma.model.Prestazione;
import com.beltra.sma.model.Visita;

import com.beltra.sma.service.MedicoService;
import com.beltra.sma.service.VisitaService;
import com.beltra.sma.utils.SlotDisponibile;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Component
public class PianificazioneComponentImpl implements PianificazioneComponent {


    private final VisitaService visitaService;
    private final MedicoService medicoService;


    /** Nota bene: @Lazy per evitare dipendenze circolari tra i Bean: PianificazioneComponentImpl e VisitaServiceImpl */
    public PianificazioneComponentImpl(@Lazy VisitaService visitaService,
                                       MedicoService medicoService) {
        this.visitaService = visitaService;
        this.medicoService = medicoService;
    }



    /** TODO: Va richiamato sia nel Controller della Prenotazione, per poter stamapare a video e in HTML nello stepper lo SlotDisponibile,
     *        Sia in fase di creazione visita.
     * */
    public Optional<SlotDisponibile> trovaPrimoSlotDisponibile(Double durataMedia, List<Visita> visiteGiornaliere) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date()); // Data attuale
        LocalTime oraAttuale = LocalTime.now(); // Ora attuale

        while (true) {
            // Data corrente
            Date dataCorrente = calendar.getTime();

            // Controlla se il giorno è ammissibile
            if (!isGiornoAmmissibile(dataCorrente)) {
                calendar.add(Calendar.DATE, 1); // Passa al giorno successivo
                oraAttuale = orarioAperturaMattina; // Reset orario per il giorno successivo
                continue;
            }

            // TODO: decommentare se necessario: per la fase di test lo lascio commentato
            // visiteGiornaliere = visitaService.getAllVisiteByData(dataCorrente);

            // Ordina le visite per orario
            visiteGiornaliere.sort(Comparator.comparing(Visita::getOra));

            // Scorri gli slot tra le visite
            LocalTime inizioSlot = oraAttuale.isBefore(orarioAperturaMattina) ? orarioAperturaMattina : oraAttuale;
            for (Visita visita : visiteGiornaliere) {
                LocalTime orarioVisita = visita.getOra().toLocalTime();
                LocalTime fineVisita = aggiungiDurata(orarioVisita, visita.getPrestazione().getDurataMedia());

                // Controlla se c'è spazio tra lo slot corrente e l'inizio della visita
                if (isSlotDisponibile(Time.valueOf(inizioSlot), visita.getOra(), durataMedia)) {
                    Medico medicoDisponibile = trovaMedicoDisponibile(dataCorrente, inizioSlot, durataMedia);
                    if (medicoDisponibile != null)
                        return Optional.of(new SlotDisponibile(dataCorrente, Time.valueOf(inizioSlot), medicoDisponibile));
                }
                inizioSlot = fineVisita; // Aggiorna l'inizio dello slot successivo
            }

            // Controlla slot dopo l'ultima visita
            if (!visiteGiornaliere.isEmpty()) {
                Visita ultimaVisita = visiteGiornaliere.get(visiteGiornaliere.size() - 1);
                LocalTime fineUltimaVisita = aggiungiDurata(ultimaVisita.getOra().toLocalTime(), ultimaVisita.getPrestazione().getDurataMedia());
                if (fineUltimaVisita.isBefore(orarioChiusuraPomeriggio) &&
                        isSlotDisponibile(Time.valueOf(fineUltimaVisita), Time.valueOf(orarioChiusuraPomeriggio), durataMedia)) {
                    Medico medicoDisponibile = trovaMedicoDisponibile(dataCorrente, fineUltimaVisita, durataMedia);
                    if (medicoDisponibile != null)
                        return Optional.of(new SlotDisponibile(dataCorrente, Time.valueOf(fineUltimaVisita), medicoDisponibile));
                }
            } else {
                // Nessuna visita per il giorno corrente, verifica slot libero
                if ( isSlotDisponibile(Time.valueOf(inizioSlot), Time.valueOf(orarioChiusuraPomeriggio), durataMedia )) {
                    Medico medicoDisponibile = trovaMedicoDisponibile(dataCorrente, inizioSlot, durataMedia);
                    if (medicoDisponibile != null)
                        return Optional.of(new SlotDisponibile(dataCorrente, Time.valueOf(inizioSlot.plusMinutes(pausaFromvisite)), medicoDisponibile));
                }
            }

            // Passa al giorno successivo
            calendar.add(Calendar.DATE, 1);
            oraAttuale = orarioAperturaMattina; // Reset orario per il nuovo giorno
        }
    }





    public boolean isSlotDisponibile(Time inizio, Time fine, Double durataMedia) {
        Duration slotDisponibile = Duration.between(inizio.toLocalTime(), fine.toLocalTime());
        return slotDisponibile.toMinutes() >= (durataMedia + pausaFromvisite); // Pausa inclusa
    }


    public LocalTime aggiungiDurata(LocalTime ora, Double durataMedia) {
        return ora.plusMinutes(durataMedia.intValue() + pausaFromvisite );
    }



    public Medico trovaMedicoDisponibile(Date data, LocalTime orario, Double durataMedia) {
        List<Medico> medici = medicoService.getAllMedici();
        for (Medico medico : medici) {
            List<Visita> visiteMedico = visitaService.getAllVisiteByMedicoAndData( medico, data);

            boolean isDisponibile = visiteMedico
                    .stream()
                    .noneMatch(visita -> {
                        LocalTime inizioVisita = visita.getOra().toLocalTime();
                        LocalTime fineVisita = aggiungiDurata(inizioVisita, visita.getPrestazione().getDurataMedia());
                        LocalTime fineProposta = aggiungiDurata(orario, durataMedia);

                return !(fineProposta.isBefore(inizioVisita) || orario.isAfter(fineVisita));
            });

            if (isDisponibile)
                return medico;
        }
        return null;
    }



    @Override
    public Boolean isGiornoAmmissibile( Date data ) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime( data );

        return (gregorianCalendar.get(Calendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY) && (gregorianCalendar.get(Calendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY) ;
    }




    @Override
    public Boolean condizioneSoddisfacibilita(LocalTime orarioDaControllare) {
        return
            ( !orarioDaControllare.isBefore(orarioAperturaMattina) &&
              !orarioDaControllare.isAfter(orarioChiusuraMattina)
            ) ||
            ( !orarioDaControllare.isBefore(orarioAperturaPomeriggio) &&
              !orarioDaControllare.isAfter(orarioChiusuraPomeriggio)
            );
    }


    @Override
    public Boolean isOrarioAmmissibile(Time orario, Prestazione prestazione) {

        LocalTime orarioLocalTime = orario.toLocalTime(); // Conversione da Time a LocaleTime

        // Verifico che l'orario sia dentro la fascia di orario lavorativo
        if( !condizioneSoddisfacibilita( orarioLocalTime ) )
            return false;

        LocalTime orarioMaggioratoDaDurata =
                java.sql.Time.valueOf (
                    orarioLocalTime
                        .plus( Duration.ofMillis( (long) (prestazione.getDurataMedia() * 60*1000))
                        .plusMinutes( pausaFromvisite ) ) //TODO: DA RIVEDERE (FORSE DEVO TOGLIERLA) ci aggiungo anche la pausa tra le visite
                )
                .toLocalTime();

        // Ecco perchè devo ricontrollare la condizione prima di ritornare
        return condizioneSoddisfacibilita( orarioMaggioratoDaDurata ) ;
    }


    @Override
    /** In questo metodo inserisco i dati di test per i vari casi di test specifici.
     *  In fase di produzione invece e' consigliato usare il metodo getAllVisiteSt */
    public List<Visita> getAllVisiteByData() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Data attuale
        Date dataCorrente = calendar.getTime();
        return visitaService.getAllVisiteByData(dataCorrente);
    }


}