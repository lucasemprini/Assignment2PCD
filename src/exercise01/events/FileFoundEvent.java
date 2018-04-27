package exercise01.events;

import exercise01.Document;

public interface FileFoundEvent {

    /**
     * Metodo che ritorna il file trovato.
     * @return la il Document trovato.
     */
    public Document getFileFound();

    /**
     * Metodo che ritorna il numero di file trovati al momento dell'evento.
     * @return il numero di file trovati al momento.
     */
    public int getTotFilesFound();
}
