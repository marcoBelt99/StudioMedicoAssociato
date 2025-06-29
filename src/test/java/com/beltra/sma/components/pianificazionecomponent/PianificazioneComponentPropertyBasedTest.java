//package com.beltra.sma.components.pianificazionecomponent;
//
//import com.beltra.sma.components.CalcolatoreAmmissibilitaComponentImpl;
//import com.beltra.sma.components.PianificazioneComponentImpl;
//import com.beltra.sma.components.RisultatoAmmissibilita;
//import com.beltra.sma.generator.ListaMediciGenerator;
//import com.beltra.sma.generator.MedicoGenerator;
//import com.beltra.sma.generator.VisitaGenerator;
//import com.beltra.sma.generator.VisiteGiornaliereGenerator;
//import com.beltra.sma.model.Medico;
//import com.beltra.sma.model.Visita;
//import com.beltra.sma.service.VisitaService;
//import com.beltra.sma.utils.SlotDisponibile;
//import com.pholser.junit.quickcheck.From;
//import com.pholser.junit.quickcheck.Property;
//import com.pholser.junit.quickcheck.generator.InRange;
//import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
//import org.junit.Before; // Aggiungi questa import
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.sql.Time;
//import java.time.LocalTime;
//import java.util.*;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//@RunWith(JUnitQuickcheck.class)
//public class PianificazioneComponentPropertyBasedTest {
//
//    @Mock
//    private VisitaService visitaService;
//
//    @Mock // Aggiungi anche questo mock
//    private CalcolatoreAmmissibilitaComponentImpl calcolatore;
//
//    private PianificazioneComponentImpl pianificazione;
//
//    // Orari di apertura e chiusura (assumendo orari standard)
//    private static final LocalTime ORARIO_APERTURA_MATTINA = LocalTime.of(8, 0);
//    private static final LocalTime ORARIO_CHIUSURA_MATTINA = LocalTime.of(12, 0);
//    private static final LocalTime ORARIO_APERTURA_POMERIGGIO = LocalTime.of(14, 0);
//    private static final LocalTime ORARIO_CHIUSURA_POMERIGGIO = LocalTime.of(18, 0);
//
//    @Before // Usa @Before invece di chiamare setup() nei metodi
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        pianificazione = new PianificazioneComponentImpl(visitaService, calcolatore);
//        // Setup mock behavior per il calcolatore
//        setupCalcolatoreBasicBehavior();
//    }
//
//    @Property(trials = 100)
//    public void testTrovaSlotDisponibileProperty(
//            @InRange(min = "15.0", max = "120.0") Double durata,
//            @From(ListaMediciGenerator.class) List<Medico> listaMedici,
//            @From(VisiteGiornaliereGenerator.class) List<Visita> visiteGiornaliere) {
//
//        // Arrange: genera data attuale casuale (nei prossimi 30 giorni)
//        Date dataAttuale = generateRandomDate();
//        LocalTime oraAttuale = generateRandomTime();
//
//        // Mock del servizio visite per restituire le visite generate
//        when(visitaService.getAllVisiteByData(any(Date.class)))
//                .thenReturn(visiteGiornaliere);
//
//        // Setup comportamento del calcolatore per ammissibilità
//        setupCalcolatoreForProperty(dataAttuale, oraAttuale, durata);
//
//        // Act: chiama il metodo da testare
//        Optional<SlotDisponibile> risultato = pianificazione.trovaSlotDisponibile(
//                durata, dataAttuale, oraAttuale, listaMedici, visiteGiornaliere);
//
//        // Assert: proprietà fondamentali
//        assertTrue("Dovrebbe sempre trovare uno slot disponibile", risultato.isPresent());
//
//        SlotDisponibile slot = risultato.get();
//        assertSlotValidity(slot, durata, listaMedici);
//    }
//
//    @Property(trials = 50)
//    public void testSlotDisponibileInOrariAmmissibili(
//            @InRange(min = "30.0", max = "90.0") Double durata,
//            @From(ListaMediciGenerator.class) List<Medico> listaMedici) {
//
//        Date dataAttuale = new Date();
//        LocalTime oraAttuale = LocalTime.of(9, 0); // Orario sicuramente ammissibile
//        List<Visita> visiteVuote = new ArrayList<>();
//
//        when(visitaService.getAllVisiteByData(any(Date.class)))
//                .thenReturn(visiteVuote);
//
//        // Setup per giorno ammissibile
//        when(calcolatore.isGiornoAmmissibile(any(Date.class))).thenReturn(true);
//        when(calcolatore.getRisultatoCalcoloAmmissibilitaOrario(any(LocalTime.class), any(Double.class)))
//                .thenReturn(RisultatoAmmissibilita.AMMISSIBILE);
//        when(calcolatore.isOrarioAmmissibile(any(LocalTime.class), any(Double.class)))
//                .thenReturn(true);
//
//        Optional<SlotDisponibile> risultato = pianificazione.trovaSlotDisponibile(
//                durata, dataAttuale, oraAttuale, listaMedici, visiteVuote);
//
//        assertTrue("Dovrebbe trovare slot in orario ammissibile", risultato.isPresent());
//
//        SlotDisponibile slot = risultato.get();
//        LocalTime orarioSlot = slot.getOrario().toLocalTime();
//
//        // Verifica che l'orario sia negli intervalli ammissibili
//        boolean inMattina = (orarioSlot.isAfter(ORARIO_APERTURA_MATTINA) || orarioSlot.equals(ORARIO_APERTURA_MATTINA)) &&
//                orarioSlot.isBefore(ORARIO_CHIUSURA_MATTINA);
//        boolean inPomeriggio = (orarioSlot.isAfter(ORARIO_APERTURA_POMERIGGIO) || orarioSlot.equals(ORARIO_APERTURA_POMERIGGIO)) &&
//                orarioSlot.isBefore(ORARIO_CHIUSURA_POMERIGGIO);
//
//        assertTrue("L'orario dello slot deve essere in fascia ammissibile (mattina o pomeriggio)",
//                inMattina || inPomeriggio);
//    }
//
//    @Property(trials = 30)
//    public void testSlotConMediciDisponibili(
//            @InRange(min = "20.0", max = "60.0") Double durata,
//            @From(ListaMediciGenerator.class) List<Medico> listaMedici,
//            @From(VisiteGiornaliereGenerator.class) List<Visita> visiteGiornaliere) {
//
//        Date dataAttuale = new Date();
//        LocalTime oraAttuale = LocalTime.of(10, 0);
//
//        when(visitaService.getAllVisiteByData(any(Date.class)))
//                .thenReturn(visiteGiornaliere);
//
//        setupCalcolatoreForProperty(dataAttuale, oraAttuale, durata);
//
//        Optional<SlotDisponibile> risultato = pianificazione.trovaSlotDisponibile(
//                durata, dataAttuale, oraAttuale, listaMedici, visiteGiornaliere);
//
//        assertTrue("Dovrebbe sempre trovare uno slot", risultato.isPresent());
//
//        SlotDisponibile slot = risultato.get();
//        assertNotNull("Lo slot deve avere un medico assegnato", slot.getMedico());
//        assertTrue("Il medico assegnato deve essere nella lista disponibile",
//                listaMedici.contains(slot.getMedico()));
//    }
//
//    // Metodi di supporto per la generazione di dati casuali e setup
//
//    private Date generateRandomDate() {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_YEAR, new Random().nextInt(30)); // Prossimi 30 giorni
//        return cal.getTime();
//    }
//
//    private LocalTime generateRandomTime() {
//        Random random = new Random();
//        int hour = random.nextInt(24);
//        int minute = random.nextInt(60);
//        return LocalTime.of(hour, minute);
//    }
//
//    private void setupCalcolatoreBasicBehavior() {
//        // Comportamento base del calcolatore
//        when(calcolatore.isGiornoAmmissibile(any(Date.class))).thenReturn(true);
//        when(calcolatore.isOrarioAmmissibile(any(LocalTime.class), any(Double.class))).thenReturn(true);
//        when(calcolatore.isOrarioAmmissibileInMattina(any(LocalTime.class), any(Double.class))).thenReturn(true);
//        when(calcolatore.isOrarioAmmissibileInPomeriggio(any(LocalTime.class), any(Double.class))).thenReturn(true);
//        when(calcolatore.isDurataMediaContenuta(any(Double.class), any(LocalTime.class), any(LocalTime.class)))
//                .thenReturn(true);
//    }
//
//    private void setupCalcolatoreForProperty(Date dataAttuale, LocalTime oraAttuale, Double durata) {
//        when(calcolatore.isGiornoAmmissibile(dataAttuale)).thenReturn(true);
//
//        // Simula comportamento realistico basato sull'orario
//        RisultatoAmmissibilita risultato = determineAmmissibilita(oraAttuale);
//        when(calcolatore.getRisultatoCalcoloAmmissibilitaOrario(oraAttuale, durata))
//                .thenReturn(risultato);
//
//        when(calcolatore.isOrarioAmmissibile(any(LocalTime.class), any(Double.class)))
//                .thenAnswer(invocation -> {
//                    LocalTime orario = invocation.getArgument(0);
//                    return isInOrarioAmmissibile(orario);
//                });
//
//        when(calcolatore.aggiungiDurataAndPausa(any(LocalTime.class), any(Double.class)))
//                .thenAnswer(invocation -> {
//                    LocalTime orario = invocation.getArgument(0);
//                    Double dur = invocation.getArgument(1);
//                    return orario.plusMinutes(dur.longValue() + 5); // +5 minuti di pausa
//                });
//    }
//
//    private RisultatoAmmissibilita determineAmmissibilita(LocalTime orario) {
//        if (orario.isBefore(ORARIO_APERTURA_MATTINA)) {
//            return RisultatoAmmissibilita.NO_BECAUSE_BEFORE_APERTURA_MATTINA;
//        } else if (orario.isAfter(ORARIO_CHIUSURA_MATTINA) && orario.isBefore(ORARIO_APERTURA_POMERIGGIO)) {
//            return RisultatoAmmissibilita.NO_BECAUSE_BETWEEN_AFTER_CHIUSURA_MATTINA_AND_BEFORE_APERTURA_POMERIGGIO;
//        } else if (orario.isAfter(ORARIO_CHIUSURA_POMERIGGIO)) {
//            return RisultatoAmmissibilita.NO_BECAUSE_AFTER_CHIUSURA_POMERIGGIO;
//        } else {
//            return RisultatoAmmissibilita.AMMISSIBILE;
//        }
//    }
//
//    private boolean isInOrarioAmmissibile(LocalTime orario) {
//        return (orario.isAfter(ORARIO_APERTURA_MATTINA) && orario.isBefore(ORARIO_CHIUSURA_MATTINA)) ||
//                (orario.isAfter(ORARIO_APERTURA_POMERIGGIO) && orario.isBefore(ORARIO_CHIUSURA_POMERIGGIO));
//    }
//
//    private void assertSlotValidity(SlotDisponibile slot, Double durata, List<Medico> listaMedici) {
//        assertNotNull("Lo slot non deve essere null", slot);
//        assertNotNull("Lo slot deve avere una data", slot.getData());
//        assertNotNull("Lo slot deve avere un orario", slot.getOrario());
//        assertNotNull("Lo slot deve avere un medico assegnato", slot.getMedico());
//        assertTrue("Il medico deve essere nella lista disponibile",
//                listaMedici.contains(slot.getMedico()));
//
//        // Verifica che la data dello slot non sia nel passato
//        assertFalse("La data dello slot non deve essere nel passato",
//                slot.getData().before(new Date()));
//    }
//}