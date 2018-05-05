package exercise01.events;

import exercise01.Document;
import javafx.util.Pair;

public interface FileFoundEvent {

    /**
     * Metodo che ritorna il file trovato.
     * @return la il Document trovato e il numero di occorrenze.
     */
    Pair<String, Long> getFileFoundAndOccurences();

    /**
     * Metodo che ritorna il numero di file trovati al momento dell'evento.
     * @return il numero di file trovati al momento e il numero di file con almeno un match.
     */
    Pair<Integer, Integer> getTotFilesFound();
}
