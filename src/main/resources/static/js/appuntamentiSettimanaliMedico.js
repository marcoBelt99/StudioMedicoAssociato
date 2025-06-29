/** inizioSettimana e fineSettimana sono passati da schedule-x (cioè dal chiamante). */
export async function getAppuntamentiSettimanaliMedico(inizioSettimana, fineSettimana) {
    try {

        // Endpoint da chiamare:
        // nota che per interpolare le variabili devo usare i backquote
        const url = `/medico/appuntamenti?inizioSettimana=${inizioSettimana}&fineSettimana=${fineSettimana}`;


        const risposta = await fetch(url);
        console.log('Chiamando URL:', url ); // scopo di debug

        if (!risposta.ok) {
            throw new Error(`Errore nella richiesta HTTP degli appuntamenti.\n
                            Stato:${risposta.status}`);
        }

        const appuntamentiSettimanali = await risposta.json();
        console.log('Appuntamenti ricevuti:', appuntamentiSettimanali); // scopo di debug

        return appuntamentiSettimanali;

    } catch (error) {
        console.error('Errore nel recupero appuntamenti:', error);
        return [];
    }
}