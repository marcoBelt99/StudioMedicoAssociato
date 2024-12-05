# Studio Medico Associato

Progetto per l'esame di Ingegneria del Software Avanzata.

## Testo - Analisi dei Requisiti
Uno studio medico associato, in cui operano medici di base e medici specialisti, dà incarico a una società di informatica di 
progettare e realizzare un database contenente i dati anagrafici e professionali dei medici, l’orario delle visite,
i tempi medi previsti per ogni visita, il costo delle singole prestazioni, i dati anagrafici dei pazienti e le prestazioni richieste.
Alle visite possono collaborare anche uno o più infermieri.

Si vuole elaborare il progetto definendone i dati necessari e successivamente sviluppare un'applicazione web che soddisfi le seguenti richieste:

Per l’utente **medico** devono essere soddisfatti i seguenti requisiti:
- Elenco settimanale contenente gli *appuntamenti di ciascun medico suddivisi per giorno e per ora*.
- Elenco giornaliero delle visite prenotate ed *effettuate*.
- Elenco giornaliero delle visite prenotate e *non effettuate*.
- Elenco cronologico delle *visite usufruite da ciascun paziente*.
- Selezionare gli eventuali infermieri che possono collaborare alle visite (prenotate e non effettuate). [Idea del check box in cui ho la lista di infermieri e seleziono solo quelli che mi interessano]
- Sapere se, ed eventualmente quali infermieri hanno collaborato ad una determinata visita.
- Compilare, creare ed inserire nella piattaforma l’esito relativo ad una determinata visita.

I requisiti per l’utente **paziente** sono:
- Visualizzare l’elenco delle *prestazioni disponibili*.
- Prenotare una o più visite, in base ai servizi offerti disponibili (prestazioni esistenti).
- Visualizzare lo storico (in ordine cronologico) delle visite da lui *prenotate ma non ancora effettuate*.
- Visualizzare lo storico (in ordine cronologico) delle sue visite usufruite (*effettuate*).
- Registrarsi alla piattaforma
- Gestire il proprio account
- Per ogni sua visita, visualizzarne l’*esito* solo dopo aver pagato un importo pari alla somma della prestazione di cui ha usufruito.
- Poter scaricare l’esito.

I requisiti per l’utente **infermiere** sono:
- Visualizzare l'elenco delle visite a cui ha collaborato.
- Visualizzare l’elenco dei pazienti presenti nel sistema.
- Visualizzare l’elenco degli esiti presenti nel sistema.

Realizzare la pagina web con la quale lo studio medico pubblicizza la propria attività fornendo l’indicazione dei servizi (per utenti anonimi e pazienti) e il quadro orario (solo per i medici). Dati mancanti opportunamente scelti.

Un utente che non ha effettuato il login (**utente anonimo**) deve poter vedere solo l’elenco dei servizi offerti e la/le/ landing pages.

Lo studio medico associato lavora *dal lunedì al venerdì*, pertanto è possibile prenotare visite solo in questa fascia di giorni.\
Per un utente x, non ci possono essere sovrapposizioni temporali.
Esempio: se l’utente x ha già prenotato una visita fissata per martedì dalle 15:00 alle 16:30 , allora l’utente x non può fare un’altra visita martedì nella fascia oraria 15:00 - 16:30.

Si noti che la cancellazione di un utente è puramente logica: non è prevista l'eliminazione fisica dalla base di dati.


### Diagramma ER
![alt Studio Medico Associato Schema Entita'-Relazioni](/database/SMA-DiagrammaER.png)


### Specifica (Statechart)
...\
...

## Utilizzo
Per poter eseguire questo progetto è necessario
...\
...\
...



## Tecnologie
- Java 17
- Spring Boot 3
- Spring Data JPA
- Thymeleaf
- HTML 5, CSS 3, Ecmascript, Ajax
- Bootstrap Italia
- Spring Security
- JUnit (per test unitari)
- Mockito (per mocking)
- Spring Test (per integration test)
- Selenium (per test end-to-end)
- Containerizzazione con Docker e Docker Compose
- CI/CD con GitHub Actions
