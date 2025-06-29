package com.beltra.sma.components;

import com.beltra.sma.functional.TriPredicate;
import com.beltra.sma.utils.Parameters;
import org.springframework.stereotype.Component;


import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiPredicate;

@Component
public class CalcolatoreAmmissibilitaComponentImpl implements CalcolatoreAmmissibilitaComponent {

    /** ATTRIBUTI */

    /** Orario da controllare */
    private LocalTime orarioDaControllare;
    /** quantita' di cui maggiorare orario */
    private Double durataPrestazione;


    private LocalTime orarioAperturaMattina = Parameters.orarioAperturaMattina;
    private LocalTime orarioChiusuraMattina = Parameters.orarioChiusuraMattina;
    private LocalTime orarioAperturaPomeriggio = Parameters.orarioAperturaPomeriggio;
    private LocalTime orarioChiusuraPomeriggio = Parameters.orarioChiusuraPomeriggio;
    private Long pausaFromVisite = Parameters.pausaFromVisite;

    /** COSTRUTTORI */
    public CalcolatoreAmmissibilitaComponentImpl() {
    }

// Questi due costruttori sono inutili molto probabilmente
//    public CalcolatoreAmmissibilitaComponentImpl(Double durataPrestazione) {
//        this.orarioDaControllare = LocalTime.now();
//        this.durataPrestazione = durataPrestazione;
//    }

//    public CalcolatoreAmmissibilitaComponentImpl(LocalTime orarioDaControllare, Double durataPrestazione) {
//        this.orarioDaControllare = orarioDaControllare;
//        this.durataPrestazione = durataPrestazione;
//    }


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


    public void setIsStessoGiorno(BiPredicate<Date, Date> isStessoGiorno) {
        this.isStessoGiorno = isStessoGiorno;
    }

    public Boolean isOrarioAmmissibile(LocalTime orario, Double durataPrestazione) {
        return condizioneSoddisfacibilita( orario ) && condizioneSoddisfacibilita( aggiungiDurataAndPausa( orario, durataPrestazione ) );
    }



    public RisultatoAmmissibilita getRisultatoCalcoloAmmissibilitaOrario(LocalTime orarioDaControllare, Double durataPrestazione) {

        // Basta che uno solo dei due orari ( o quello da controllare o quello maggiorato) sforino i limiti consentiti
        // e risultato sarà negativo

        LocalTime orarioMaggioratoDaDurata = aggiungiDurataAndPausa( orarioDaControllare, durataPrestazione );

        if ( ( (orarioDaControllare.isBefore(orarioAperturaMattina) ) ||
               (orarioMaggioratoDaDurata.isBefore(orarioAperturaMattina)) ) &&
             !isOrarioAmmissibile(orarioDaControllare, durataPrestazione)  )
            return RisultatoAmmissibilita.NO_BECAUSE_BEFORE_APERTURA_MATTINA;
        else if( ( (orarioDaControllare.isAfter(orarioChiusuraMattina) && orarioDaControllare.isBefore(orarioAperturaPomeriggio) ) ||
                   (orarioMaggioratoDaDurata.isAfter(orarioChiusuraMattina) && orarioMaggioratoDaDurata.isBefore(orarioAperturaPomeriggio)) ) &&
                  !isOrarioAmmissibile(orarioDaControllare, durataPrestazione)  )
            return RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO;
        else if( ( (orarioDaControllare.isAfter(orarioChiusuraPomeriggio)) ||
                 (orarioMaggioratoDaDurata.isAfter(orarioChiusuraPomeriggio)) ) &&
                  !isOrarioAmmissibile(orarioDaControllare, durataPrestazione)  )
            return RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO;
        else
            return RisultatoAmmissibilita.AMMISSIBILE;
    }



    /** Aggiunge il parametro durataMedia all'orario passato come parametro e la pausa di 5 minuti (tra una visita e la successiva).*/
    public LocalTime aggiungiDurataAndPausa(LocalTime ora, Double durataMedia) {
        return ora.plusMinutes(durataMedia.intValue() + Parameters.pausaFromVisite);
    }


    @Override
    public boolean isOrarioAmmissibileInMattina(LocalTime ora, Double durata) {
        return isOrarioAmmissibile(ora, durata) &&
                ora.isBefore(orarioChiusuraMattina);
    }


    // Potrei anche non implementare il metodo isOrarioInPomeriggio(), ma ricavarlo dalla negazione
    // del metodo isOrarioInMattina(), questo perchè in isOrarioInMattina() contiene già il controllo di ammissibilità
    // e se orario e' ammissibile ma isOrarioInMattina() è falso, significa che siamo nel pomeriggio.
    public boolean isOrarioAmmissibileInPomeriggio(LocalTime ora, Double durata) {
        return isOrarioAmmissibile( ora, durata) && !isOrarioAmmissibileInMattina(ora, durata);
    }


    @Override
    public boolean isOrarioInMattina(LocalTime ora) {
        return  ora.isAfter(LocalTime.MIDNIGHT) &&
                ora.isBefore(orarioChiusuraMattina);
    }

    @Override
    public boolean isOrarioInPomeriggio(LocalTime ora) {
        return  ora.isAfter(orarioChiusuraMattina) &&
                ora.isBefore(LocalTime.MAX);
    }

    @Override
    public boolean isOrarioAfterAperturaPomeriggio(LocalTime ora) {
        return  ora.isAfter(orarioAperturaPomeriggio.plusMinutes(pausaFromVisite)) &&
                ora.isBefore(LocalTime.MAX);
    }


    @Override
    public boolean isOrarioAfterChiusuraPomeriggio(LocalTime ora) {
        return ora.compareTo(orarioChiusuraPomeriggio) > 0; // in questo caso io NON sto comprendendo il fatto che esattamente le "21:00" sia dopo chiusura pomeriggio
    }



    public boolean isOrarioBetweenMidnightAndAperturaMattina(LocalTime ora) {
        return ora.isAfter(LocalTime.MIDNIGHT) && ora.isBefore(orarioAperturaMattina);
    }



    /** TriPredicate e' una interfaccia funzionale che ho creato io per poter usare più parametri nel mio predicato.
     * <br>
     * Controlla se durataMedia è contenuta in oraFine ed orarioChiusura. */
    private TriPredicate<Double, LocalTime, LocalTime> lambdaIsDurataMediaContenuta = (durataMedia, oraFine, orarioChiusura)  -> {

        // Calcolo la differenza in minuti tra oraFine (maggiorata di 5 minuti) e ora chiusura
        // nota che non e' specificato se chiusura mattino o pomeriggio, lo scelgo in fase di chiamata
        long differenzaInMinuti = Duration.between( oraFine.plusMinutes(PianificazioneComponent.pausaFromvisite), orarioChiusura ).toMinutes();
        return (differenzaInMinuti - durataMedia) >= 0;
    };

    @Override
    public boolean isDurataMediaContenuta(Double durataMedia, LocalTime oraFine, LocalTime orarioChiusura) {
        return lambdaIsDurataMediaContenuta.test(durataMedia, oraFine, orarioChiusura);
    }

    @Override
    public Date findNextGiornoAmmissibile(Date dataDiPartenza, Calendar calendar) {

        calendar.setTime(dataDiPartenza);
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Partiamo dal giorno successivo
        Date dataSuccessiva = calendar.getTime();

        while (!isGiornoAmmissibile(dataSuccessiva)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dataSuccessiva = calendar.getTime();
        }

        return dataSuccessiva;
    }
}
