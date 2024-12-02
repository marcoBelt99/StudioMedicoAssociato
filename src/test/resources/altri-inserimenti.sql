-- Altri inserimenti (da testare per bene !!!!)
INSERT INTO prestazioni (titolo, descrizione, durata_media, costo, ticket)
VALUES
    ('ECG', 'Elettrocardiogramma', 30, 50.00, 20.00),
    ('Visita Dermatologica', 'Valutazione della pelle', 40, 70.00, 25.00),
    ('Visita Cardiologica', 'Valutazione cardiaca', 60, 120.00, 40.00),
    ('Controllo Generale', 'Visita di controllo generale', 20, 30.00, 10.00),
    ('Prelievo del Sangue', 'Analisi ematiche', 15, 25.00, 5.00);

INSERT INTO visite (data_visita, ora, num_ambulatorio, id_anagrafica, id_prestazione)
VALUES
    ('2024-12-10', '10:30:00', 1, 4, 1), -- Paziente Luca Verdi, ECG
    ('2024-12-11', '14:00:00', 2, 5, 2), -- Paziente Marco Beltrame, Visita Dermatologica
    ('2024-12-12', '09:00:00', 1, 6, 3), -- Paziente Matteo Stoppa, Visita Cardiologica
    ('2024-12-13', '15:30:00', 3, 7, 4), -- Paziente Lina Marchesini, Controllo Generale
    ('2024-12-14', '08:00:00', 2, 8, 5); -- Paziente Aldo Camisotti, Prelievo del Sangue


INSERT INTO prenotazioni (data_prenotazione, effettuata, id_visita, id_anagrafica)
VALUES
    ('2024-12-05', TRUE, 1, 4), -- Prenotazione effettuata per la visita del 10/12
    ('2024-12-06', TRUE, 2, 5), -- Prenotazione effettuata per la visita dell'11/12
    ('2024-12-07', FALSE, 3, 6), -- Prenotazione non effettuata per la visita del 12/12
    ('2024-12-08', TRUE, 4, 7), -- Prenotazione effettuata per la visita del 13/12
    ('2024-12-09', FALSE, 5, 8); -- Prenotazione non effettuata per la visita del 14/12


INSERT INTO collaborazioni (id_visita, id_anagrafica)
VALUES
    (1, 9), -- Infermiere Diletta Biondi partecipa alla visita 1
    (3, 9); -- Infermiere Diletta Biondi partecipa alla visita 3



INSERT INTO esiti (id_visita, descrizione_referto, visible)
VALUES
    (1, 'ECG regolare, nessuna anomalia rilevata.', TRUE), -- Esito della visita 1
    (2, 'Rash cutaneo curabile con crema specifica.', TRUE); -- Esito della visita 2
