package exercise01;

import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

public class DocumentSearchTask extends RecursiveTask<Map<String, Long>> {
    
	private final Document document;
    private final Pattern searchedWord;
    private final WordCounter wc;
    
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

