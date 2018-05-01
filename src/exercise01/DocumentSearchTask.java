package exercise01;

import exercise01.events.FileEventListener;
import exercise01.events.FileFoundEvent;
import exercise01.events.FileFoundEventImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

public class DocumentSearchTask extends RecursiveTask<Map<String, Long>> {
    
	private final Document document;
    private final Pattern searchedWord;
    private final WordCounter wc;

    /**
     * Costruttore per il Task del Document.
     * @param wc l'istanza del WordCounter.
     * @param document il Documento su cui cercare.
     * @param searchedWord la Regular Expression da cercare.
     */
    DocumentSearchTask(WordCounter wc, Document document, Pattern searchedWord) {
        super();
        this.document = document;
        this.searchedWord = searchedWord;
        this.wc = wc;
    }

    @Override
    protected Map<String, Long> compute() {
        return wc.occurrencesCount(document, searchedWord);
    }
}

