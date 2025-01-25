-- CREATE DATABASE  sma_db;


-- PostgreSQL non accetta la semplice CREATE DATABASE IF NOT EXISTS <nome_db>
DO $$
BEGIN
   IF NOT EXISTS (
       SELECT FROM pg_database WHERE datname = 'sma_db'
   ) THEN
       CREATE DATABASE sma_db;
END IF;
END $$;


-- TODO: per tentare di risolvere problema: role "postgres" does not exists
CREATE ROLE postgres WITH LOGIN PASSWORD 'mmsf22dp';

-- TODO: per risolvere problemi di permessi relativi alle operazioni sul DB
-- Granting privileges to the postgres role
GRANT ALL PRIVILEGES ON DATABASE sma_db TO postgres;

-- Grant usage on schema public to postgres
GRANT USAGE ON SCHEMA public TO postgres;

-- Grant privileges on all tables to postgres
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO postgres;

-- Ensure privileges on tables created in the future
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO postgres;

-- Grant privileges on all sequences to postgres
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- Ensure privileges on sequences created in the future
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT ON SEQUENCES TO postgres;



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
    ticket REAL NOT NULL
);




CREATE TABLE IF NOT EXISTS visite (
    id_visita SERIAL PRIMARY KEY,
    data_visita DATE NOT NULL,
    ora TIME NOT NULL,
    num_ambulatorio INT NOT NULL,
    id_anagrafica INT NOT NULL,
    id_prestazione INT NOT NULL,
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
--  3 MEDICI, 6 PAZIENTI e 1 INFERMIERE
--  5 PRESTAZIONI (servizi offerti dallo studio medico) 
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
('Biondi', 'Diletta', '1999-08-05', 'F'); -- infermiere


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
('UT0009', 'dil_tess_bnd', '$2a$10$aeiIdL4eZYQviVIyjEzd6.DstgTRhUEJsZ4IMm7RGQt0p2x0dTpby', TRUE, 9);  -- infermiere  --> 'ilovepastaalforno99'


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
('INFERMIERE', 'UT0009'); -- infermiere



INSERT INTO medici (id_anagrafica, matricola, specializzazione)
VALUES
(1, 'MED0001', 'Cardiologia'), -- medico specialista
(2, 'MED0002', 'Dermatologia'), -- medico specialista
(3, 'MED0003', NULL); -- medico di base perche' specializzazione e' a NULL



INSERT INTO pazienti (id_anagrafica, codice_fiscale, telefono, email, residenza)
VALUES                                                            -- TODO: '<indirizzo> <num. civico>, <citta> (<iniziali provincia>)'
(4, 'VRDLCC81L22Z123X', '3331234567', 'verdi.luca@gmail.com',           'Via Roma 10, Roma (RM)'),
(5, 'BLTMRC99M30A059W', '3403183848', 'marco01.beltrame@edu.unife.it',  'Via Ca Cima 23, Adria (RO)'),
(6, 'STPMTT98D01A059C', '3303482842', 'matteo.stoppa@homail.it',        'Via Peschiera 12, Adria (RO)'),
(7, 'MRCLNI64L30A059Y', '3280150503', 'lina.marchesini2@gmail.com',     'Via della Pace 11, Rovigo (RO)'),
(8, 'CMTLDO48E22C123G', '3221198456', 'cmsttld@libero.it',              'Viale della Seta 15, Rosolina (RO)');


INSERT INTO infermieri (id_anagrafica, num_cartellino, tipologia)
VALUES
(9, 'INF0001', 'Strumentale'); -- !! Se presente ad una visita, richiede la creazione del record corrispondente sulla tabella 'collaborazioni'


INSERT INTO prestazioni (titolo, descrizione, durata_media, costo, ticket)
VALUES
('Visita Cardiologica', 'Controllo cardiaco completo', 30, 100.0, 20.0),

('Visita Ortopedica', 'Valutazione dello stato di salute di ossa, muscoli e articolazioni,
 in termini di struttura e funzionalità.', 25, 102.0, 20.0  ),

('TAC', 'Tecnica diagnostica che sfrutta le radiazioni ionizzanti (o raggi X) per ottenere immagini dettagliate,
 in versione tridimensionale, di aree anatomiche specifiche del corpo umano (es: encefalo, ossa, vasi sanguigni, organi addominali,
  organi toracici, vie respiratorie ecc.).', 30, 100.50, 0.0),

('Analisi del Sangue', 'Verificare i valori dei principali componenti ematici e fornire così importanti
 informazioni sulla salute del paziente e sul funzionamento del suo organismo.', 10, 15.0, 3.95 ),

('Visita Dermatologica', 'Valutare la salute della pelle, delle unghie e dei capelli. Durante la visita il dermatologo esamina la cute
 alla ricerca di eventuali lesioni, macchie, verruche, nei o altre imperfezioni
  che possono essere segnale di malattie della pelle.', 30, 200.0, 10.0),
  
('Analisi delle Urine', 'Laboratorio prove per la scoperta di eventuali infezioni delle vie urinarie', 15, 50.50, 13.20 );



INSERT INTO visite (data_visita, ora, num_ambulatorio, id_anagrafica, id_prestazione)
VALUES -- TODO: le visite possono essere fatte solo dal lunedì al venerdì
('2024-11-11', '15:00:00', 5, 3, 3), -- marco beltrame ha prenotato una 'TAC'.                    ==> Il medico 3 si incarica di svolgere questa visita.
('2024-11-20', '09:20:00', 1, 2, 4), -- lina marchesini ha prenotato una 'Visita Dermatologica'. ==> Il medico 2 si incarica di svolgere questa visita.
('2024-12-01', '10:30:00', 5, 3, 3), -- marco beltrame ha prenotato una 'TAC'.                   ==> Il medico 3 si incarica di svolgere questa visita.
('2024-12-01', '15:30:00', 2, 3, 6); -- matteo stoppa ha prenotato una 'Analisi del Sangue'      ==> Il medico 3 si incarica di svolgere questa visita.   



INSERT INTO collaborazioni (id_visita, id_anagrafica) -- tabella strettamente collegata alla tabella 'infermieri'
VALUES
(1, 9), -- Infermiere (che ha id_anagrafica pari a 9) partecipa alla Visita 1
(1, 9); -- Infermiere (che ha id_anagrafica pari a 9) partecipa alla Visita 3


INSERT INTO prenotazioni (data_prenotazione, effettuata, id_visita, id_anagrafica)
VALUES
('2024-10-10', TRUE, 1, 5), -- marco beltrame prenota per la visita (tipo prestazione è TAC) che avverrà in futuro (2024-11-11). Essendo effettuata=TRUE significa che la visita è già stata effettuata.
('2024-11-10', TRUE, 2, 7), -- lina marchesini prenota per la visita (tipo prestazione è 'Visita Dermatologica') che avverrà in futuro (2024-20-11). Essendo effettuata=TRUE significa che la visita è già stata effettuata.
('2024-11-23', FALSE, 3, 5), -- marco beltrame prenota per la visita (tipo prestazione è TAC) che avverrà in futuro (2024-12-01). Essendo effettuata=FALSE significa che la visita NON è già stata effettuata.
('2024-11-20', FALSE, 4, 6); -- matteo stoppa ha prenotato per la visita (tipo prestazione è 'Analisi del Sangue') che avverrà in futuro (2024-12-01). Essendo effettuata=FALSE significa che la visita NON è già stata effettuata.


INSERT INTO esiti (id_visita, descrizione_referto, visible)
VALUES
(1, 'Esito positivo senza anomalie.', TRUE), -- essendo visible= TRUE può anche scaricarsi il suo referto (perchè ad esempio ha pagato la cifra di una somma pari a quella riportata sulla prestazione)
(2, 'Trovato soffio al cuore ventricolo sinistro', FALSE); -- essendo visible=FALSE non si può scaricare il suo referto (perchè ad esempio non ha ancora pagato)
-- Gli alri esiti sono inseriti e compilati dai medici
-- L'esito deve riportare i nomi dei medici, gli eventuali infermieri che hanno collaborato, e la descrizione.
-- Un paziente si vede il suo esito tramite i collegamenti con le chiavi esterne:
--      esiti.id_visita ----> visite.id_visita 
--      ----> cerco la prenotazione corretta prenotazione collegata a quella specifica visita, dato visite.id_visita (con un join)
--      ----> di quella specifica prenotazione, tramite prenotazioni.id_anagrafica mi recupero l'id_anagrafica del paziente (con un join).
