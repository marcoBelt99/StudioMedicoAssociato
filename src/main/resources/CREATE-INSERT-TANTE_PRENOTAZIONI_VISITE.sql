--CREATE DATABASE IF NOT EXISTS sma_db; -- TODO: cambiare il nome al db
-- CREATE DATABASE sma_db; -- TODO: cambiare il nome al db

-- DROP DATABASE stud_med_assoc_test_db;


--CREATE DATABASE  sma_db; -- TODO: cambiare il nome al db


-- TODO: per tentare di risolvere problema: role "postgres" does not exists
--CREATE ROLE postgres WITH LOGIN PASSWORD 'mmsf22dp';

CREATE TABLE IF NOT EXISTS anagrafiche (
    id_anagrafica SERIAL PRIMARY KEY,
    cognome VARCHAR(50) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    data_nascita DATE NOT NULL,
    genere CHAR(1) CHECK (genere IN ('M', 'F')) NOT NULL -- aggiungo il CHECK
);




CREATE TABLE IF NOT EXISTS utenti (
    id_utente VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    attivo BOOLEAN DEFAULT TRUE,
    id_anagrafica INT NOT NULL,
    CONSTRAINT fk_utenti_anagrafiche FOREIGN KEY (id_anagrafica) REFERENCES anagrafiche(id_anagrafica) 
    ON UPDATE CASCADE
    ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS ruoli (
    id_ruolo SERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    id_utente VARCHAR(20) NOT NULL,
    CONSTRAINT fk_ruoli_utenti FOREIGN KEY (id_utente) REFERENCES utenti(id_utente)
    ON UPDATE CASCADE 
    ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS medici (
    id_anagrafica INT PRIMARY KEY,
    matricola VARCHAR(20) NOT NULL UNIQUE,
    specializzazione VARCHAR(100),
    CONSTRAINT fk_medici_anagrafiche FOREIGN KEY (id_anagrafica) REFERENCES anagrafiche(id_anagrafica)
    ON UPDATE CASCADE 
    ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS pazienti (
    id_anagrafica INT PRIMARY KEY,
    codice_fiscale VARCHAR(16) NOT NULL UNIQUE,
    -- eta INT NOT NULL, TODO: --> non la metto, me la calcolo a partire dalla data di nascita
    telefono VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL,
    residenza VARCHAR(100) NOT NULL, --> TODO: Faccio in modo di chiedere in input: comune, provincia, indirizzo, num. civico e mi persisto un singolo campo
    -- fatto così: '<indirizzo> <num. civico>, <citta> (<iniziali provincia>)'
    CONSTRAINT fk_pazienti_anagrafiche FOREIGN KEY (id_anagrafica) REFERENCES anagrafiche(id_anagrafica) 
    ON UPDATE CASCADE
    ON DELETE CASCADE
);




CREATE TABLE IF NOT EXISTS prestazioni (
    id_prestazione SERIAL PRIMARY KEY,
    titolo VARCHAR(100) NOT NULL,
    --descrizione VARCHAR(255),
	descrizione TEXT NOT NULL,
    durata_media INT NOT NULL,
    costo REAL NOT NULL,
    ticket REAL NOT NULL,
    deleted BOOLEAN DEFAULT FALSE
);




CREATE TABLE IF NOT EXISTS visite (
    id_visita SERIAL PRIMARY KEY,
    data_visita DATE NOT NULL,
    ora TIME NOT NULL,
    num_ambulatorio INT NOT NULL,
    id_anagrafica INT NOT NULL,
    id_prestazione INT,
    CONSTRAINT fk_visite_anagrafiche FOREIGN KEY (id_anagrafica) REFERENCES anagrafiche(id_anagrafica)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
    CONSTRAINT fk_visite_prestazioni FOREIGN KEY (id_prestazione) REFERENCES prestazioni(id_prestazione)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS infermieri (
    id_anagrafica INT PRIMARY KEY,
    num_cartellino VARCHAR(20) NOT NULL UNIQUE,
    tipologia VARCHAR(50),
    CONSTRAINT fk_infermieri_anagrafiche FOREIGN KEY (id_anagrafica) REFERENCES anagrafiche(id_anagrafica) 
    ON UPDATE CASCADE
    ON DELETE CASCADE
);


-- TODO: Tabella di associazione collaborazioni
/** Se desideri che un infermiere possa partecipare a più visite, significa che la relazione tra infermieri e visite è di tipo N:M 
(molti a molti). 
Devo quindi introdurre una tabella di associazione che gestisca la relazione molti-a-molti tra infermieri e visite.
*/
CREATE TABLE IF NOT EXISTS collaborazioni (
	id_collaborazione SERIAL PRIMARY KEY, -- TODO: necessaria per evitare record duplicati!
    id_visita INT NOT NULL,
    id_anagrafica INT NOT NULL,

    CONSTRAINT fk_collaborazioni_visite FOREIGN KEY (id_visita) REFERENCES visite(id_visita)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
    CONSTRAINT fk_collaborazioni_infermieri FOREIGN KEY (id_anagrafica) REFERENCES infermieri(id_anagrafica) 
    ON UPDATE CASCADE
    ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS prenotazioni (
    id_prenotazione SERIAL PRIMARY KEY,
    data_prenotazione DATE NOT NULL,
    effettuata BOOLEAN DEFAULT FALSE,
    id_visita INT NOT NULL,
    id_anagrafica INT NOT NULL,
    CONSTRAINT fk_prenotazioni_visite FOREIGN KEY (id_visita) REFERENCES visite(id_visita)
    ON UPDATE CASCADE 
    ON DELETE CASCADE,
    CONSTRAINT fk_prenotazioni_anagrafiche FOREIGN KEY (id_anagrafica) REFERENCES anagrafiche(id_anagrafica) 
    ON UPDATE CASCADE
    ON DELETE CASCADE
);




CREATE TABLE IF NOT EXISTS esiti (
    id_visita INT PRIMARY KEY,
    descrizione_referto VARCHAR(255),
    visible BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_esiti_visite FOREIGN KEY (id_visita) REFERENCES visite(id_visita) 
    ON UPDATE CASCADE
    ON DELETE CASCADE
);



-- ########################################################################################
-- ########################################################################################
-- ########################################################################################
-- ########################################################################################


-- INIZIO CON:
--  4 MEDICI, 5 PAZIENTI e 1 INFERMIERE
--  7 PRESTAZIONI (servizi offerti dallo studio medico)
--  4 VISITE con quindi 4 relative PRENOTAZIONI
--	2 COLLABORAZIONI (posso decidere io quanti infermieri impiegare per una determinata visita)
--	2 ESITI (posso decidere io di inserirlo quando voglio)

INSERT INTO anagrafiche (cognome, nome, data_nascita, genere)
VALUES
('Rossi', 'Mario', '1980-05-15', 'M'), -- medico
('Bianchi', 'Anna', '1990-07-22', 'F'), -- medico
('Mori', 'Geraldo', '1957-10-10', 'M'), -- medico
('Verdi', 'Luca', '1985-03-10', 'M'), -- paziente
('Beltrame', 'Marco', '1999-08-30', 'M'), -- paziente
('Stoppa', 'Matteo', '1998-01-10', 'M'), -- paziente
('Marchesini', 'Lina', '1964-07-30', 'F'), -- paziente
('Camisotti', 'Aldo', '1948-11-01', 'M'), -- paziente
('Biondi', 'Diletta', '1999-08-05', 'F'), -- infermiere

('Capanna', 'Leonardo', '1965-10-30', 'M'); -- medico


INSERT INTO utenti (id_utente, username, password, attivo, id_anagrafica)
VALUES
('UT0001', 'mario_rossi', '$2a$10$nFs8oN/ZX9aiepYKA6RLiOj0Uz.h8SUfKzw0rGvuBE3BkHWvXcAkO', TRUE, 1), -- medico       --> 'password123'
('UT0002', 'anna_bianchi1', '$2a$10$mwDltOyrLvqFBGI4HRljDOeDl3UKIaRDkgkBow28uG2JX1GSwqQ1m', TRUE, 2), -- medico     --> 'securepass'
('UT0003', 'mrgersaur', '$2a$10$YJ5FTkiIIdLXWWOtSYdV7upIRW/t9FEzZQkV0xcug35H34wnqfL0O', TRUE, 3), -- medico         --> 'bellagiornataa'
('UT0004', 'lucavv', '$2a$10$/TWLuNGEnXgx48x1cRmvLOvOk/g/nc5sjKtjQYatTIZOMmjaowcvy', TRUE, 4), -- paziente          --> 'ciao-gatto'
('UT0005', 'marcobeltra', '$2a$10$b2htiRD.5bSuLFod5nJ3CO5A96tfOQqn6kOqP8k19PMDoS9Ytafke', TRUE, 5), -- paziente     --> '123stellona'
('UT0006', 'stoppy123', '$2a$10$yiCZtPu4XPMzyVyIXPTkVehuvM4XgLxu9RsPLMNV.U/hzu0jK/JIa', TRUE, 6), -- paziente        --> '124bimbam'
('UT0007', 'linamarkes', '$2a$10$KiF/XsH2R7e3/7QsCWDflOocG.X0txxy2kAKEtHVXgdo0vumh.dDS', TRUE, 7),  -- paziente     --> 'abicidieh'
('UT0008', 'aldocam48', '$2a$10$wiXuB.xqcx/hVi/9qVeZm.q8b5go8MWKQQEqsCj7UDvn0Q0oqHDNO', TRUE, 8),  -- paziente      --> '22stellacometa22'
('UT0009', 'dil_tess_bnd', '$2a$10$aeiIdL4eZYQviVIyjEzd6.DstgTRhUEJsZ4IMm7RGQt0p2x0dTpby', TRUE, 9),  -- infermiere  --> 'ilovepastaalforno99'

('UT00010', 'leocapa', '$2a$10$YJ5FTkiIIdLXWWOtSYdV7upIRW/t9FEzZQkV0xcug35H34wnqfL0O', TRUE, 10); -- medico    --> 'bellagiornataa'

INSERT INTO ruoli (tipo, id_utente)
VALUES
('MEDICO', 'UT0001'), -- medico specialista
('MEDICO', 'UT0002'), -- medico specialista
('MEDICO', 'UT0003'), -- medico di base
('PAZIENTE', 'UT0004'), -- paziente
('PAZIENTE', 'UT0005'), -- paziente
('PAZIENTE', 'UT0006'), -- paziente
('PAZIENTE', 'UT0007'), -- paziente
('PAZIENTE', 'UT0008'), -- paziente
('INFERMIERE', 'UT0009'), -- infermiere

('MEDICO', 'UT00010'); -- medico di base


INSERT INTO medici (id_anagrafica, matricola, specializzazione)
VALUES
(1, 'MED0001', 'Cardiologia'), -- medico specialista
(2, 'MED0002', 'Dermatologia'), -- medico specialista
(3, 'MED0003', NULL), -- medico di base perche' specializzazione e' a NULL

(10, 'MED0004', 'Medicina Sportiva'); -- medico specialista



INSERT INTO pazienti (id_anagrafica, codice_fiscale, telefono, email, residenza)
VALUES                                                            -- TODO: '<indirizzo> <num. civico>, <citta> (<iniziali provincia>)'
(4, 'VRDLCC81L22Z123X', '3331234567', 'verdi.luca@gmail.com',           'Via Roma 10, Roma (RM)'),
(5, 'BLTMRC99M30A059W', '3403183848', 'marco01.beltrame@edu.unife.it',  'Via Ca Cima 23, Adria (RO)'),
(6, 'STPMTT98D01A059C', '3303482842', 'matteo.stoppa@homail.it',        'Via Peschiera 12, Adria (RO)'),
(7, 'MRCLNI64L30A059Y', '3280150503', 'lina.marchesini2@gmail.com',     'Via della Pace 11, Rovigo (RO)'),
(8, 'CMTLDO48E22C123G', '3221198456', 'cmsttld@libero.it',              'Viale della Seta, 15 Rosolina (RO)');


INSERT INTO infermieri (id_anagrafica, num_cartellino, tipologia)
VALUES
(9, 'INF0001', 'Strumentale'); -- !! Se presente ad una visita, richiede la creazione del record corrispondente sulla tabella 'collaborazioni'


INSERT INTO prestazioni (titolo, descrizione, durata_media, costo, ticket)
VALUES
('Visita Cardiologica', 'Controllo cardiaco completo', 60, 100.0, 20.0),

('Visita Ortopedica', 'Valutazione dello stato di salute di ossa, muscoli e articolazioni,
 in termini di struttura e funzionalità.', 45, 102.0, 20.0  ),

('TAC', 'Tecnica diagnostica che sfrutta le radiazioni ionizzanti (o raggi X) per ottenere immagini dettagliate,
 in versione tridimensionale, di aree anatomiche specifiche del corpo umano (es: encefalo, ossa, vasi sanguigni, organi addominali,
  organi toracici, vie respiratorie ecc.).', 50, 100.50, 0.0),

('Analisi del Sangue', 'Verificare i valori dei principali componenti ematici e fornire così importanti
informazioni sulla salute del paziente e sul funzionamento del suo organismo.', 20, 15.0, 3.95 ),

('Visita Dermatologica', 'Valutare la salute della pelle, delle unghie e dei capelli. Durante la visita il dermatologo esamina la cute
 alla ricerca di eventuali lesioni, macchie, verruche, nei o altre imperfezioni
  che possono essere segnale di malattie della pelle.', 70, 200.0, 10.0),

('Analisi delle Urine', 'Laboratorio prove per la scoperta di eventuali infezioni delle vie urinarie', 35, 50.50, 13.20 ),

('Visita Sportiva', 'Visita per ottenere ideneità per praticare una qualsiasi attività sportiva agonistica (dilettantistica e non). ', 100, 80.00, 10.00),

('Visita Odontoiatrica',  'Visita approfondita che riguarda: eventuali otturazioni; diagnosi di carie ai denti; gengiviti e parodontiti; pulizia dei denti; sostituzioni presidi medici. Sbiancamento denti', 120, 350.00, 25.00);




INSERT INTO visite (data_visita, ora, num_ambulatorio, id_anagrafica, id_prestazione)
VALUES -- TODO: le visite possono essere fatte solo dal lunedì al venerdì, dalle 07:00-12:00  e dalle 14:00-21:00
('2024-11-11', '07:05:00', 1, 3, 3), -- marco beltrame ha prenotato una 'TAC'.                   ==> Il medico 3 si incarica di svolgere questa visita.
('2024-11-20', '07:05:00', 2, 2, 4), -- lina marchesini ha prenotato una 'Visita Dermatologica'. ==> Il medico 2 si incarica di svolgere questa visita.
('2024-12-01', '07:05:00', 3, 3, 3), -- marco beltrame ha prenotato una 'TAC'.                   ==> Il medico 3 si incarica di svolgere questa visita.
('2024-12-01', '15:35:00', 4, 10, 7), -- matteo stoppa ha prenotato una 'Visita Sportiva'        ==> Il medico 10 si incarica di svolgere questa visita.


('2024-12-02', '07:05:00', 5, 3, 4), -- marco beltrame ha prenotato Analisi del sangue          ===> medico 3 incaricato
('2024-12-05', '07:05:00', 1, 1, 5), -- marco beltrame ha prenotato Visita Dermatologica        ===> medico 1 incaricato

('2024-12-09', '07:05:00', 2, 3, 6 ), -- lina marchesini ha prenotato Analisi delle Urine       ===> medico 3 incaricato
('2024-12-09', '16:05:00', 3, 1, 2), -- lina marchesini ha prenotato Visita Ortopedica          ===> medico 1 incaricato

('2025-01-08', '09:05:00', 4, 10, 7), --marco beltra ha prenotato Visita Sportiva                ===> medico 10 incaricato
('2025-01-09', '09:35:00', 5, 10, 7), -- lina marchesini ha prenotato Visita Sportiva            ===> medico 10 incaricato
('2025-01-10', '10:05:00', 1, 10, 7), -- matteo stoppa ha prenotato Visita Sportiva              ===> medico 10 incaricato


-- Prenotazione di un altro batch di visite. Nota bene il calcolo dell'ora di inizio visita vi: vi.ora = (vi-1.ora)+(vi-1.prestazione.durata)+(5minuti)
-- Data visita: 2024-12-06. Medici del sistema: 1, 2, 3, 10.
('2024-12-06', '07:05:00', 2, 1, 2), --  marco beltra ha prenotato Visita           Medici liberi: 2,3,10           occupati: 1
('2024-12-06', '07:05:00', 3, 2, 2), --  marco beltra ha prenotato Visita           Medici liberi: 3,10             occupati: 1,2
('2024-12-06', '07:05:00', 4, 3, 1), --  marco beltra ha prenotato Visita           Medici liberi: 10               occupati: 1, 2, 3
('2024-12-06', '07:05:00', 5, 10, 1),--  lina marchesini ha prenotato Visita        Medici liberi: <nessuno>        occupati: 1, 2, 3, 10
('2024-12-06', '07:55:00', 1, 1, 5), --  lina marchesini ha prenotato Visita        Medici liberi: 2                occupati: 1, 3, 10
('2024-12-06', '07:55:00', 2, 2, 6), --  lina marchesini ha prenotato Visita        Medici liberi: <nessuno>        occupati: 1, 2, 3, 10
('2024-12-06', '08:10:00', 3, 3, 3), --  luca verdi ha prenotato Visita             Medici liberi: 10               occupati: 1, 2, 3
('2024-12-06', '08:10:00', 4, 10, 2), --  luca verdi ha prenotato Visita            Medici liberi: <nessuno>        occupati: 1, 2, 3, 10
('2024-12-06', '08:35:00', 5, 2, 3),--  matteo stoppa ha prenotato Visita           Medici liberi: <nessuno>        occupati: 1, 2, 3, 10


-- Ulteriore batch di 20 visite nell'anno nuovo:
('2025-01-16', '07:05:00', 1, 1, 2), --  durata: 45   paziente: marco beltrame ,        Medici liberi: 2,3,10       occupati: 1
('2025-01-16', '07:05:00', 2, 2,2), --   durata: 45   paziente: marco beltrame ,        Medici liberi: 3,10         occupati: 1,2
('2025-01-16', '07:05:00', 3, 3, 1), --  durata: 60   paziente: aldo camisotti ,        Medici liberi: 10           occupati: 1,2,3
('2025-01-16', '07:05:00', 4, 10, 1), -- durata: 60   paziente: lina marchesini ,       Medici liberi: <nessuno>    occupati: 1,2,3,10

('2025-01-16', '07:55:00', 5, 1, 5), --  durata: 70   paziente: matteo stoppa ,         Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '07:55:00', 1, 2, 6), --  durata: 35   paziente: aldo camisotti ,        Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '08:10:00', 2, 3, 3), --  durata: 50   paziente: luca verdi ,            Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '08:10:00', 3, 10, 2), -- durata: 45   paziente: luca verdi ,            Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '08:35:00', 4, 2, 2), --  durata: 45   paziente: aldo camisotti ,        Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '09:00:00', 5, 10,3),--    durata: 50   paziente: matteo stoppa ,        Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '09:15:00', 1, 3, 8), --  durata: 120  paziente: marco beltrame ,        Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '09:15:00', 2, 1,2),--    durata: 45   paziente: matteo stoppa ,         Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '09:25:00', 3, 2, 1), -- durata: 60   paziente: marco beltrame ,         Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '09:55:00', 4, 10, 1),--   durata: 60   paziente: lina marchesini ,      Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '10:05:00', 5, 1, 5), --  durata: 70   paziente: luca verdi ,            Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '10:30:00', 1, 2, 6), --  durata: 35   paziente: lina marchesini ,       Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '11:00:00', 2, 10, 3), -- durata: 50   paziente: luca verdi ,            Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '11:10:00', 3, 2, 2), --  durata: 45   paziente: luca verdi ,            Medici liberi: <nessuno>    occupati: 1,2,3,10
('2025-01-16', '14:05:00', 4, 1, 8), --  durata: 120  paziente: lina marchesini ,       Medici liberi: 2,3, 10      occupati: 1
('2025-01-16', '14:05:00', 5, 2,3); --   durata: 50   paziente: aldo camisotti ,        Medici liberi: 3,10         occupati: 1,2



INSERT INTO collaborazioni (id_visita, id_anagrafica) -- tabella strettamente collegata alla tabella 'infermieri'
VALUES
(1, 9), -- Infermiere (che ha id_anagrafica pari a 9) partecipa alla Visita 1
(1, 9), -- Infermiere (che ha id_anagrafica pari a 9) partecipa alla Visita 3
(8, 9); -- Infermiere partecipa alla visita 8


INSERT INTO prenotazioni (data_prenotazione, effettuata, id_visita, id_anagrafica)
VALUES
('2024-10-10', TRUE, 1, 5), -- marco beltrame prenota per la visita (tipo prestazione è TAC) che avverrà in futuro (2024-11-11). Essendo effettuata=TRUE significa che la visita è già stata effettuata.
('2024-11-10', TRUE, 2, 7), -- lina marchesini prenota per la visita (tipo prestazione è 'Visita Dermatologica') che avverrà in futuro (2024-20-11). Essendo effettuata=TRUE significa che la visita è già stata effettuata.
('2024-11-23', FALSE, 3, 5), -- marco beltrame prenota per la visita (tipo prestazione è TAC) che avverrà in futuro (2024-12-01). Essendo effettuata=FALSE significa che la visita NON è già stata effettuata.
('2024-11-20', FALSE, 4, 6), -- matteo stoppa ha prenotato per la visita (tipo prestazione è 'Analisi del Sangue') che avverrà in futuro (2024-12-01). Essendo effettuata=FALSE significa che la visita NON è già stata effettuata.


('2024-11-20', TRUE, 5, 5), -- marco beltrame prenota visita
('2024-11-21', FALSE, 6, 5), -- marco beltrame prenota visita

('2024-11-21', TRUE, 7, 7), -- lina marchesini prenota visita
('2024-11-22', FALSE, 8, 7), -- lina marchesini prenota visita

-- prenoto delle visite sportive
('2024-11-23', FALSE, 9, 5), -- marco beltrame prenota visita
('2024-11-24', FALSE, 10, 7), -- lina marchesini prenota visita
('2024-11-25', FALSE, 11, 6), -- matteo stoppa prenota visita

-- Prenoto un batch di visite
('2024-11-26', TRUE, 12, 5), -- marco beltrame prenota visita
('2024-11-26', TRUE, 13, 5), -- marco beltrame prenota visita
('2024-11-26', FALSE, 14,5), -- marco beltrame prenota visita
('2024-11-26', TRUE, 15, 7), -- lina marchesini prenota visita
('2024-11-26', FALSE, 16, 7), --lina marchesini prenota visita
('2024-11-26', FALSE, 17, 7), --lina marchesini prenota visita
('2024-11-26', FALSE, 18, 4), -- luca verdi prenota visita
('2024-11-26', TRUE, 19, 4), -- luca verdi prenota visita
('2024-11-26', FALSE, 20, 6), -- matteo stoppa prenota visita


-- Ulteriore batch di 20 prenotazioni di visite nell'anno nuovo
('2025-01-10', FALSE, 21, 5), -- marco beltrame prenota visita
('2025-01-10', FALSE, 22, 5), -- marco beltrame prenota visita
('2025-01-10', FALSE, 23,8), --  aldo camisotti prenota visita
('2025-01-10', FALSE, 24, 7), -- lina marchesini prenota visita
('2025-01-10', FALSE, 25, 6), -- matteo stoppa prenota visita
('2025-01-10', FALSE, 26, 8), -- aldo camisotti prenota visita
('2025-01-10', FALSE, 27, 4), -- luca verdi prenota visita
('2025-01-10', FALSE, 28, 4), -- luca verdi prenota visita
('2025-01-10', FALSE, 29, 8), -- aldo camisotti prenota visita
('2025-01-11', FALSE, 30, 6), -- matteo stoppa prenota visita
('2025-01-11', FALSE, 31, 5), -- marco beltrame prenota visita
('2025-01-11', FALSE, 32, 6), -- matteo stoppa prenota visita
('2025-01-11', FALSE, 33, 5), -- marco beltrame prenota visita
('2025-01-11', FALSE, 34, 7), -- lina marchesini prenota visita
('2025-01-11', FALSE, 35, 4), -- luca verdi prenota visita
('2025-01-12', FALSE, 36, 7), -- lina marchesini prenota visita
('2025-01-12', FALSE, 37, 4), -- luca verdi prenota visita
('2025-01-12', FALSE, 38, 4), -- luca verdi prenota visita
('2025-01-12', FALSE, 39, 7), -- lina marchesini prenota visita
('2025-01-12', FALSE, 40, 8); -- aldo camisotti prenota visita


-- L'esito esiste solo se prenotazione.effettuate = TRUE
INSERT INTO esiti (id_visita, descrizione_referto, visible)
VALUES
(1, 'Esito positivo senza anomalie.', TRUE), -- essendo visible= TRUE può anche scaricarsi il suo referto (perchè ad esempio ha pagato la cifra di una somma pari a quella riportata sulla prestazione)
(2, 'Trovato soffio al cuore ventricolo sinistro. Fare ulteriori accertamenti', FALSE), -- essendo visible=FALSE non si può scaricare il suo referto (perchè ad esempio non ha ancora pagato)

(5, 'Carenza di ferro nel sangue', TRUE),
(7, 'Cistite: prendere Monuril 1 volta al giorno per 2 giorni consecutivi', FALSE),
(12, 'Indossare plantari per deambulazione assistiva', FALSE ),
(13, 'Ginocchio: patologia artrosica', FALSE),
(15, 'Palpitazioni: disturbo causato da un battito cardiaco accelerato. Assumere farmaco x.', TRUE),
(19, 'Presenza di traumi relativi all’apparato muscolo-scheletrico.', TRUE);

-- Gli altri esiti sono inseriti e compilati dai medici
-- L'esito deve riportare i nomi dei medici, gli eventuali infermieri che hanno collaborato, e la descrizione.
-- Un paziente si vede il suo esito tramite i collegamenti con le chiavi esterne:
--      esiti.id_visita ----> visite.id_visita 
--      ----> cerco la prenotazione corretta prenotazione collegata a quella specifica visita, dato visite.id_visita (con un join)
--      ----> di quella specifica prenotazione, tramite prenotazioni.id_anagrafica mi recupero l'id_anagrafica del paziente (con un join).

select * from prenotazioni where effettuata=true;