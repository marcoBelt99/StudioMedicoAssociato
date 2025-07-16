select * from utenti;

select * from anagrafiche;

select * from pazienti;

select * from medici;

select * from infermieri;

select * from visite;

select * from prestazioni;

select * from prenotazioni;

-- Elenco delle visite prenotate ed effettuate
select data_visita, ora, num_ambulatorio, anagrafiche.nome as nome_paziente, anagrafiche.cognome as cognome_paziente
from prenotazioni, visite, anagrafiche
where prenotazioni.id_visita = visite.id_visita
  AND prenotazioni.effettuata = TRUE
  AND prenotazioni.id_anagrafica = anagrafiche.id_anagrafica;


-- ELENCO DELLE VISITE PRENOTATE E NON EFFETTUATE
select data_visita, ora, num_ambulatorio, anagrafiche.nome as "nome paziente", anagrafiche.cognome as "cognome paziente"
from prenotazioni, visite, anagrafiche
where prenotazioni.id_visita = visite.id_visita
  AND prenotazioni.effettuata = FALSE
  AND prenotazioni.id_anagrafica = anagrafiche.id_anagrafica;


-- ELENCO CRONOLOGICO DELLE VISITE USUFRUITE DA CIASCUN PAZIENTE
select 	prenotazioni.data_prenotazione as "data prenotazione", visite.data_visita as "data visita", visite.ora as "ora visita",
          visite.num_ambulatorio as "num ambulatorio",
          anagrafiche.nome as "nome paziente", anagrafiche.cognome as "cognome paziente",
          prestazioni.titolo as "tipo prestazione"
from
    visite, prenotazioni, prestazioni, anagrafiche
where
    visite.id_visita = prenotazioni.id_visita
  and prenotazioni.id_anagrafica = anagrafiche.id_anagrafica
  and visite.id_prestazione = prestazioni.id_prestazione
order by visite.data_visita;


-- ELENCO delle visite prenotate e non effettuate di un determinato paziente
SELECT
    prenotazioni.data_prenotazione,
    visite.data_visita, visite.ora, visite.num_ambulatorio,
    anagrafiche.nome, anagrafiche.cognome,
    prestazioni.titolo,
    prenotazioni.effettuata AS "Effettuata"
FROM prenotazioni, visite, prestazioni, utenti, anagrafiche
WHERE
    visite.id_visita = prenotazioni.id_visita
  AND prestazioni.id_prestazione = visite.id_prestazione
  AND prenotazioni.effettuata = FALSE
  AND anagrafiche.id_anagrafica = utenti.id_anagrafica
  AND utenti.id_anagrafica = prenotazioni.id_anagrafica
  AND utenti.username = 'marcobeltra';

