# Studio Medico Associato

Progetto per l'esame di Ingegneria del Software Avanzata.

## Testo - Analisi dei Requisiti
Uno Studio Medico Associato, in cui operano <span style="color:green"><i>**medici**</i></span> di base e medici specialisti, dà incarico a una società
di informatica di progettare e realizzare un database contenente i dati anagrafici e professionali dei medici,
l’orario delle <span style="color:green"><i>**visite**</i></span>, i tempi medi previsti per ogni visita,
il costo delle singole <span style="color:green"><i>**prestazioni**</i></span>, i dati
anagrafici dei <span style="color:green"><i>**pazienti**</i></span> e le prestazioni richieste. 
Alle visite possono collaborare anche uno o più <span style="color:green"><i>**infermieri**</i></span>.


Si vuole elaborare il progetto definendone i dati necessari e successivamente sviluppare un'applicazione
web che soddisfi le seguenti richieste:

Per l’utente **medico** devono essere soddisfatti i seguenti requisiti:
1. Elenco settimanale contenente gli *appuntamenti* di ciascun medico *suddivisi per giorno e per ora*.
2. Elenco giornaliero delle visite <span style="color:green"><i>**prenotate**</i></span> ed *effettuate*.
3. Elenco giornaliero delle visite *prenotate e non ancora effettuate*.
4. Elenco cronologico delle visite *usufruite da ciascun paziente*.
5. <span style="color:violet">[Se c’è tempo]</span>: Selezionare gli eventuali infermieri che possono collaborare alle visite. [idea del check box in cui ho la lista di infermieri e seleziono solo quelli che mi interessano].
6. <span style="color:violet">[Se c’è tempo]</span>: Sapere se, ed eventualmente quali infermieri hanno *collaborato* ad una determinata visita.
7. <span style="color:violet">[Se c’è tempo]</span>: Compilare, *creare* ed *inserire* nella piattaforma l’*esito* relativo ad una determinata visita.

I requisiti per l’utente **paziente** sono:
1. Visualizzare l’*elenco delle prestazioni disponibili*.
2. *Prenotare* una o più *visite*, in base ai servizi offerti disponibili (*prestazioni* esistenti).
3. Visualizzare lo storico (elenco cronologico) delle visite da lui *prenotate ma non ancora effettuate*.
4. Visualizzare lo storico (elenco cronologico) delle visite di cui ha usufruito (*effettuate*).
5. <span style="color:violet">[ Se c’è tempo]</span>: Iscriversi al portale e gestire il proprio account. (Sviluppare parte di sign-in).
6. <span style="color:violet">[Se c’è tempo]</span>: Per ogni sua visita, visualizzarne l’*esito* e poterlo scaricare, solo dopo aver pagato un importo pari alla somma della prestazione di cui ha usufruito.
   
I requisiti per l’utente **infermiere** sono:
1. Visualizzare l'elenco delle *visite* a cui ha *collaborato*.
2. Visualizzare l’elenco dei *pazienti* presenti nel sistema.
3. <span style="color:violet">[Se c’è tempo]</span>: Visualizzare l’elenco degli *esiti* presenti nel sistema.

Realizzare la pagina web con la quale lo studio medico pubblicizza la propria attività fornendo l’indicazione dei
servizi (per utenti anonimi e pazienti) e il *quadro orario* (solo per i medici).

Un utente che non ha effettuato il login (**utente anonimo**) deve poter vedere solo l’elenco dei servizi offerti e la/le landing page.

Lo Studio Medico Associato lavora dal *lunedì al venerdì*, pertanto è possibile prenotare visite solo in tale intervallo di giorni (lavorativi).\
Inoltre, lo Studio Medico Associato lavora nelle seguenti fasce orarie: *07:00 - 12:00*, *14:00 - 21:00*.

Per un utente X, <u>non ci possono essere sovrapposizioni temporali</u>.\
Esempio: se l’utente X ha già prenotato (in caso di paziente) / ha già assegnata (in caso di medico) una visita fissata per martedì AAAA/MM/GG dalle 15:00 alle 16:30, allora, l’utente X non può gestire un’altra visita martedì AAAA/MM/GG nella fascia oraria 15:00 - 16:30.


Si noti che, come spesso accade in scenari reali, per alcune entità la cancellazione è puramente logica: non è prevista l'eliminazione fisica dalla base di dati. (In altre parole, l’eliminazione avviene impostando a “false” un certo campo anziché effettuare drop di records a database).

Il termine “**SMA-RT**” è un “acronimo stilizzato”, nel quale: le prime tre lettere stanno per “Studio Medico Associato”, 
l’intera parola nel suo insieme (“*SMART*”) vuole indicare il fatto che tale piattaforma è “intelligente”, automatizzata e funzionale per tutti i suoi utilizzatori.

### Diagramma ER
![Diagramma ER](https://raw.githubusercontent.com/marcoBelt99/StudioMedicoAssociato/main/src/main/resources/static/images/progetto/SMA-DiagrammaER.png)



## Specifiche
...\
...
#### (Statechart)
...\
...
#### (Specifica Algebrica)
...\
...
### (Specifiche Logica)
...\
...


## Utilizzo
- Solo per questa prima fase iniziale (incompleta):
  - Si consiglia di aprire questo progetto in IntelliJ IDEA
  - In `/src/main/resources/database` all'interno di un client DBMS (come pgAdmin o DBeabeaver),
  eseguire i seguenti due files:
    - `CREATE-INSERT-TANTE_PRENOTAZIONI_VISITE.sql`
    - `TRUNCATE-DROP.sql`.
  - Posizionarsi nella root directory dell'applicazione, e dare il comando 
   `mvn clean package`
  - Se dovessero esserci problemi con il comando precedente, 
  è possibile saltare i test con:
  `mvn clean package -DskipTests`
  - Avviare l'applicazione all'interno del proprio sistema operativo utilizzando:
    - direttamente il tool di avvio offerto da IntelliJ Idea
    - oppure in `/target/` eseguire il file `StudioMedicoAssociato-0.0.1.jar` con:
    `java -jar StudioMedicoAssociato-0.0.1.jar`
  - Con un client browser, recarsi in `localhost:8086`



## Tecnologie
- Java 17
- Spring Boot 3
- Spring Data JPA
- Thymeleaf
- HTML 5, CSS 3, Ecmascript, Ajax
- Bootstrap Italia
- Spring Security
- Maven (come strumento di buil automation e gestione progetto)
- JUnit (per test unitari)
- Mockito (per mocking)
- Spring Test (per integration test)
- Selenium (per test end-to-end)
- Java Code Coverage (per misurare la copertura dei test)
- Containerizzazione con Docker e Docker Compose
- CI/CD con GitHub Actions (in Pipeline)