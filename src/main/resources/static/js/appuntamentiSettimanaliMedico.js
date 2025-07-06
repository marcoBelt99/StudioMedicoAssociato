
/**
 * ################################################################
 *      Funzione per caricare gli appuntamenti dal backend.
 * ################################################################
 */
export async function getAppuntamentiSettimanaliMedico(inizioSettimana, fineSettimana) {
    try {

        // Necessario richiamarlo così per chiamare correttamente il backend.
        const dataInizio = inizioSettimana.split(' ')[0];
        const dataFine = fineSettimana.split(' ')[0];

        console.log('🔃 Caricamento appuntamenti:', inizioSettimana, fineSettimana);

        // Endpoint da chiamare (nota che per interpolare le variabili devo usare i backquote).
        const url = `/medico/appuntamenti?inizioSettimana=${dataInizio}&fineSettimana=${dataFine}`;

        console.log('Chiamando URL:', url ); // scopo di debug

        // Fetch
        const risposta = await fetch(url);


        if (!risposta.ok) {
            console.error('Errore nella richiesta HTTP degli appuntamenti. Stato:', risposta.status);
            return [];
        }


        let  appuntamentiSettimanali = await risposta.json();


        appuntamentiSettimanali = appuntamentiSettimanali.map(app => ({
            ...app,
            start: formatToScheduleX(app.start),
            end: formatToScheduleX(app.end),
        }));


        // Debug
        console.table(appuntamentiSettimanali); // figo l'uso di console.table( json );


        return appuntamentiSettimanali;

    } catch (e) {
        console.error('Errore nel recupero appuntamenti:', e);
        return [];
    }
}



/**
 * ################################################################
 *      Funzione per ottenere gli appuntamenti iniziali (della settimana corrente)
 * ################################################################
 */
export async function getAppuntamentiIniziali() {
    const oggi = new Date();
    const inizioSettimana = getInizioSettimana(oggi);
    const fineSettimana = getFineSettimana(oggi);

    return await getAppuntamentiSettimanaliMedico(inizioSettimana, fineSettimana);
}



/**
 * ################################################################
 *      Funzioni di utilità per calcolare inizio e fine settimana
 * ################################################################
 */
function getInizioSettimana(date) {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(d.setDate(diff)).toISOString().split('T')[0];
}

function getFineSettimana(date) {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? 0 : 7);
    return new Date(d.setDate(diff)).toISOString().split('T')[0];
}



// Per problema della non formattazione in formato comprensibile da schedule-x
function formatToScheduleX(dateStr) {
    const d = new Date(dateStr);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    const hh = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
}
