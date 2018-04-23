package exercise01;

import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class DocumentSearchTask extends RecursiveTask<Map<String, Long>> {
    
	private final Document document;
    private final String searchedWord;
    private final WordCounter wc;
    
    DocumentSearchTask(WordCounter wc, Document document, String searchedWord) {
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

