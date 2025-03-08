package com.beltra.sma.components;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.BiPredicate;

@Component

public class CalcolatoreAmmissibilitaComponentImpl implements CalcolatoreAmmissibilitaComponent {

    /** ATTRIBUTI */

    /** Orario da controllare */
    private LocalTime orarioDaControllare;
    /** quantita' di cui maggiorare orario */
    private Double durataPrestazione;


    private LocalTime orarioAperturaMattina = PianificazioneComponent.orarioAperturaMattina;
    private LocalTime orarioChiusuraMattina = PianificazioneComponent.orarioChiusuraMattina;
    private LocalTime orarioAperturaPomeriggio = PianificazioneComponent.orarioAperturaPomeriggio;
    private LocalTime orarioChiusuraPomeriggio = PianificazioneComponent.orarioChiusuraPomeriggio;


    /** COSTRUTTORI */
    public CalcolatoreAmmissibilitaComponentImpl() {
    }

    public CalcolatoreAmmissibilitaComponentImpl(Double durataPrestazione) {
        this.orarioDaControllare = LocalTime.now();
        this.durataPrestazione = durataPrestazione;
    }

    public CalcolatoreAmmissibilitaComponentImpl(LocalTime orarioDaControllare, Double durataPrestazione) {
        this.orarioDaControllare = orarioDaControllare;
        this.durataPrestazione = durataPrestazione;
    }


    /** METODI */

    public Boolean isGiornoAmmissibile( Date data ) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime( data );

        return (gregorianCalendar.get(Calendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY) && (gregorianCalendar.get(Calendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY) ;
    }



    /** Verifica che data1 abbia lo stesso giorno di data2 */
    public BiPredicate<Date, Date> isStessoGiorno = (data1, data2) -> {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime( data1 );

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime( data2 );

        return  calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    };



    public Boolean condizioneSoddisfacibilita(LocalTime orarioDaControllare) { return
                ( !orarioDaControllare.isBefore(orarioAperturaMattina) &&
                        !orarioDaControllare.isAfter(orarioChiusuraMattina)
                ) ||
                ( !orarioDaControllare.isBefore(orarioAperturaPomeriggio) &&
                        !orarioDaControllare.isAfter(orarioChiusuraPomeriggio)
                );
    }



    public Boolean isOrarioAmmissibile(LocalTime orario, Double durataPrestazione) {
        return condizioneSoddisfacibilita( orario ) && condizioneSoddisfacibilita( aggiungiDurataAndPausa( orario, durataPrestazione ) );
    }



    public Risultato getRisultatoCalcoloAmmissibilitaOrario(LocalTime orarioDaControllare, Double durataPrestazione) {

        // Basta che uno solo dei due orari ( o quello da controllare o quello maggiorato) sforino i limiti consentiti
        // e risultato sarà negativo

        LocalTime orarioMaggioratoDaDurata = aggiungiDurataAndPausa( orarioDaControllare, durataPrestazione );

        if ( ( (orarioDaControllare.isBefore(orarioAperturaMattina) ) ||
               (orarioMaggioratoDaDurata.isBefore(orarioAperturaMattina)) ) &&
             !isOrarioAmmissibile(orarioDaControllare, durataPrestazione)  )
            return Risultato.NO_BECAUSE_BEFORE_APERTURA_MATTINA;
        else if( ( (orarioDaControllare.isAfter(orarioChiusuraMattina) && orarioDaControllare.isBefore(orarioAperturaPomeriggio) ) ||
                   (orarioMaggioratoDaDurata.isAfter(orarioChiusuraMattina) && orarioMaggioratoDaDurata.isBefore(orarioAperturaPomeriggio)) ) &&
                  !isOrarioAmmissibile(orarioDaControllare, durataPrestazione)  )
            return Risultato.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO;
        else if( ( (orarioDaControllare.isAfter(orarioChiusuraPomeriggio)) ||
                 (orarioMaggioratoDaDurata.isAfter(orarioChiusuraPomeriggio)) ) &&
                  !isOrarioAmmissibile(orarioDaControllare, durataPrestazione)  )
            return Risultato.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO;
        else
            return Risultato.AMMISSIBILE;
    }



    /** Aggiunge il parametro durataMedia all'orario passato come parametro e la pausa di 5 minuti (tra una visita e la successiva).*/
    public LocalTime aggiungiDurataAndPausa(LocalTime ora, Double durataMedia) {
        return ora.plusMinutes(durataMedia.intValue() + PianificazioneComponent.pausaFromvisite);
    }


    public boolean isOrarioAfterMezzanotte(LocalTime ora, Double durataMedia)
    {

        return
            !isOrarioAmmissibile(ora, durataMedia) &&
            (ora.isBefore(LocalTime.MAX) &&
             aggiungiDurataAndPausa(ora, durataMedia).isAfter(LocalTime.MAX));
    }


    public boolean isOrarioInMattina(LocalTime ora, Double durata) {
        return isOrarioAmmissibile(ora, durata) &&
                ora.isBefore(orarioChiusuraMattina);
    }

    // Potrei anche non implementare il metodo isOrarioInPomeriggio(), ma ricavarlo dalla negazione
    // del metodo isOrarioInMattina(), questo perchè in isOrarioInMattina() contiene già il controllo di ammissibilità
    // e se orario e' ammissibile ma isOrarioInMattina() è falso, significa che siamo nel pomeriggio.
    public boolean isOrarioInPomeriggio(LocalTime ora, Double durata) {
        return isOrarioAmmissibile( ora, durata) && !isOrarioInMattina(ora, durata);
    }

    public boolean isOrarioBetweenMidnightAndAperturaMattina(LocalTime ora) {
        return ora.isAfter(LocalTime.MIDNIGHT) && ora.isBefore(orarioAperturaMattina);
    }
}
