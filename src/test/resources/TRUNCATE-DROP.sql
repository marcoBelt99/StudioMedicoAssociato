-- TODO: l'ordine e' importante!!
TRUNCATE anagrafiche cascade;
TRUNCATE utenti cascade;
TRUNCATE ruoli cascade;
TRUNCATE medici;
TRUNCATE pazienti;
TRUNCATE prestazioni cascade;
TRUNCATE visite cascade;
TRUNCATE infermieri cascade;
TRUNCATE collaborazioni;
TRUNCATE prenotazioni;
TRUNCATE esiti;


DROP TABLE esiti;
DROP TABLE prenotazioni;
DROP TABLE collaborazioni;
DROP TABLE infermieri;
DROP TABLE visite;
DROP TABLE prestazioni;
DROP TABLE pazienti;
DROP TABLE medici;
DROP TABLE ruoli;
DROP TABLE utenti;
DROP TABLE anagrafiche;


-- --ELIMINAZIONE DELLE SEQUENCES (PER GLI ID AUTOINCREMENT)
-- DROP SEQUENCE anagrafiche_id_anagrafica_seq cascade;
-- DROP SEQUENCE ruoli_id_ruolo_seq cascade;
-- DROP SEQUENCE ruoli_id_prestazione_seq cascade;
-- DROP SEQUENCE ruoli_id_prenotazione_seq cascade;
-- DROP SEQUENCE visite_id_visita_seq cascade;
-- DROP SEQUENCE prenotazioni_id_prenotazione_seq cascade;
-- DROP SEQUENCE prestazioni_id_prestazione_seq cascade;
-- DROP SEQUENCE collaborazioni_id_collaborazione_seq cascade;