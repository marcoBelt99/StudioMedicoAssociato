/** Funzione separata per fare la richiesta fetch */
function getPazienteDetails( codiceFiscale ) {

    if (codiceFiscale) {

        // TODO: Uso fetch per inviare la richiesta al controller
        fetch(`/pazienti/pazienteDettagli?codiceFiscale=${codiceFiscale}`)

            .then(response => {
                console.log(response); // Debug risposta prima di convertirla in JSON

                if (!response.ok) {
                    throw new Error('Errore nella richiesta');
                }
                return response.json(); // Risultato in formato JSON
            })
            .then(data => {

                // Popola i campi del form con i dati del paziente
                document.getElementsByName('nomePaziente')[0].value = data.nome;
                document.getElementsByName('cognomePaziente')[0].value = data.cognome;
                document.getElementsByName('codiceFiscale')[0].value = data.codiceFiscale;
            })
            .catch(error => {
                console.error('Errore:', error);
                alert('Errore nel recupero dei dati del paziente');
            });
    }
}

// TODO: Event listener separato per gestire la selezione del paziente
//       gestione evento 'change' della select box
document.getElementById('pazienteSelect').addEventListener('change', function() {

    // Ottieni il codice fiscale selezionato
    var codiceFiscale = this.value;

    // TODO: Chiama la funzione separata per ottenere i dati
    getPazienteDetails(codiceFiscale);

});