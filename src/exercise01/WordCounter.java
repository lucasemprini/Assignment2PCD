package exercise01;

import exercise01.events.FileEventListener;
import exercise01.events.FileFoundEvent;
import exercise01.events.FileFoundEventImpl;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {    

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    private final List<FileEventListener> listenersList = new ArrayList<>();
    private int totDocumentsFound = 0;
    private int documentsMatching = 0;
    private static final int SLEEP_DEBUG = 100;

    /**
     * Metodo che ritorna un array di parole a partire da una line di un Document.
     * @param line la riga di un documento.
     * @return la riga splittata in parole.
     */
    private String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    /**
     * Crea un nuovo Event e lo notifica alla lista dei listeners.
     */
    private void notifyFileFoundEvent(Pair<String, Long> doc) {
        FileFoundEvent event = new FileFoundEventImpl(doc, new Pair<>(totDocumentsFound, documentsMatching));
        for(FileEventListener l : this.listenersList) {
            l.newFileFound(event);
        }
    }

    /**
     * Aggiunge un nuovo Listener per l'aggiornamento del numero di file trovati.
     * @param l il Listener da aggiungere.
     */
    public void addListener(FileEventListener l) {
        this.listenersList.add(l);
    }

    /**
     * Metodo che conta le occorrenze all'interno di un Document.
     * @param document il documento in cui cercare.
     * @param regexp la regular expression da cercare.
     * @return una mappa che associa al nome del documento il numero di occorrenze trovate.
     */
    public Map<String, Long> occurrencesCount(Document document, Pattern regexp) {
        long count = 0;
        Map<String, Long> map = new HashMap<>();
        for (String line : document.getLines()) {
            for (String word : wordsIn(line)) {
                Matcher m =regexp.matcher(word);
                if (m.matches()) {
                    count = count + 1;
                }
            }
        }

        this.totDocumentsFound++;
        if(count > 0) {
            this.documentsMatching++;
        }

        map.put(document.toString(), count);
        try {
            Thread.sleep(SLEEP_DEBUG);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.notifyFileFoundEvent(new Pair<>(document.toString(), count));

        return map;
    }

    /**
     * Metodo che triggera l'Executor.
     * @param folder la cartella su cui cercare.
     * @param regexp la regular expression da cercare.
     * @param depth la max depth oltre cui non andare.
     */
    public void countOccurrencesInParallel(Folder folder, Pattern regexp, int depth) {
        forkJoinPool.invoke(new FolderSearchTask(this, folder, regexp, depth));
    }
}
