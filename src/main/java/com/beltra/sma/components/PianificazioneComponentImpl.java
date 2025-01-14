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


    // TODO:
    /** Calcolo Slot Orario */
    // C'e' uno slot orario? == Ci sta la visita qui dentro?
    // Se ci sta, allora la inserisci lì
    // Se non ci sta, la crei e la metti in fondo.
    // Naturalmente la data visita deve essere >= data attuale





    /** Trova orario visita */


    /** TODO: Va richiamato sia nel Controller della Prenotazione, per poter stamapare a video e in HTML nello stepper lo SlotDisponibile,
     *    Sia in fase di creazione visita.
     * */
    public Optional<SlotDisponibile> trovaPrimoSlotDisponibile(Double durataMedia, Date dataInizioRicerca) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataInizioRicerca);

        while (true) {
            // Salta il weekend
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, 1);
                continue;
            }

            // Recupera tutte le visite per il giorno corrente
            Date dataCorrente = calendar.getTime();
            List<Visita> visiteGiornaliere = visitaService.getAllVisiteByData( dataCorrente );

            // Ordina le visite per orario
            visiteGiornaliere.sort(Comparator.comparing(Visita::getOra));

            // Cerca uno slot disponibile tra le visite
            Time inizioOrarioLavoro = Time.valueOf("07:00:00");
            Time fineOrarioLavoro = Time.valueOf("21:00:00");

            // Verifica spazi prima della prima visita
            if (!visiteGiornaliere.isEmpty()) {
                Visita primaVisita = visiteGiornaliere.get(0);
                if (isSlotDisponibile(inizioOrarioLavoro, primaVisita.getOra(), durataMedia)) {
                    Medico medicoDisponibile = trovaMedicoDisponibile(dataCorrente, inizioOrarioLavoro.toLocalTime(), durataMedia);
                    if (medicoDisponibile != null) {
                        return Optional.of(new SlotDisponibile(dataCorrente, inizioOrarioLavoro, medicoDisponibile));
                    }
                }
            }

            // Verifica spazi tra le visite
            for (int i = 0; i < visiteGiornaliere.size() - 1; i++) {
                LocalTime fineVisita = aggiungiDurata(visiteGiornaliere.get(i).getOra().toLocalTime(), visiteGiornaliere.get(i).getPrestazione().getDurataMedia());
                LocalTime inizioVisitaSuccessiva = visiteGiornaliere.get(i + 1).getOra().toLocalTime();

                if ( isSlotDisponibile( Time.valueOf(fineVisita), Time.valueOf( inizioVisitaSuccessiva ), durataMedia)) {
                    Medico medicoDisponibile = trovaMedicoDisponibile( dataCorrente, fineVisita , durataMedia);
                    if (medicoDisponibile != null) {
                        return Optional.of(new SlotDisponibile(dataCorrente, Time.valueOf( fineVisita ), medicoDisponibile));
                    }
                }
            }

            // Verifica spazio dopo l'ultima visita
            if (!visiteGiornaliere.isEmpty()) {
                Visita ultimaVisita = visiteGiornaliere.get(visiteGiornaliere.size() - 1);
                Time fineUltimaVisita = Time.valueOf( aggiungiDurata(ultimaVisita.getOra().toLocalTime(), ultimaVisita.getPrestazione().getDurataMedia()) );
                if (isSlotDisponibile(fineUltimaVisita, fineOrarioLavoro, durataMedia)) {
                    Medico medicoDisponibile = trovaMedicoDisponibile(dataCorrente, fineUltimaVisita.toLocalTime(), durataMedia);
                    if (medicoDisponibile != null) {
                        return Optional.of(new SlotDisponibile(dataCorrente, fineUltimaVisita, medicoDisponibile));
                    }
                }
            }

            // Passa al giorno successivo
            calendar.add(Calendar.DATE, 1);
        }
    }

    private boolean isSlotDisponibile(Time inizio, Time fine, Double durataMedia) {
        long minutiDisponibili = (fine.getTime() - inizio.getTime()) / (1000 * 60);
        return minutiDisponibili >= durataMedia;
    }

    public LocalTime aggiungiDurata(LocalTime ora, Double durataMedia) {
        return ora.plusMinutes(durataMedia.intValue());
    }



    public Medico trovaMedicoDisponibile(Date data, LocalTime orario, Double durataMedia) {
        for (Medico medico : medicoService.getAllMedici()) {
            List<Visita> visiteMedico = visitaService.getAllVisiteByMedicoAndData(medico, data);
            boolean disponibile = true;

            for (Visita visita : visiteMedico) {
                LocalTime inizioVisita = visita.getOra().toLocalTime();
                LocalTime fineVisita = aggiungiDurata(inizioVisita, visita.getPrestazione().getDurataMedia());
                LocalTime fineRichiesta = aggiungiDurata(orario, durataMedia);

                // Verifica sovrapposizione
                if (!(fineRichiesta.isBefore(inizioVisita) || orario.isAfter(fineVisita))) {
                    disponibile = false;
                    break;
                }
            }

            if (disponibile)
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
            ( !orarioDaControllare.isBefore(PianificazioneComponent.orarioAperturaMattina) &&
              !orarioDaControllare.isAfter(PianificazioneComponent.orarioChiusuraMattina)
            ) ||
            ( !orarioDaControllare.isBefore(PianificazioneComponent.orarioAperturaPomeriggio) &&
              !orarioDaControllare.isAfter(PianificazioneComponent.orarioChiusuraPomeriggio)
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
                        .plusMinutes( pausaFromvisite ) ) // ci aggiungo anche la pausa tra le visite
                )
                .toLocalTime();

        // Ecco perchè devo ricontrollare la condizione prima di ritornare
        return condizioneSoddisfacibilita( orarioMaggioratoDaDurata ) ;

    }

}