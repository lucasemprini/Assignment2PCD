package exercise02;

import exercise01.Document;
import exercise01.WordCounter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.util.Map;
import java.util.regex.Pattern;

public class VerticleDocumentSearchTask extends AbstractVerticle {

    private final Document document;
    private final Pattern searchedWord;
    private final WordCounter wc;
    private final Future<Map<String, Long>> future;

    /**
     * Costruttore per il Task del Document.
     * @param wc l'istanza del VertWordCounter.
     * @param document il Documento su cui cercare.
     * @param searchedWord la Regular Expression da cercare.
     */
    VerticleDocumentSearchTask(WordCounter wc, Document document, Pattern searchedWord, final Future<Map<String, Long>> future) {
        super();
        this.document = document;
        this.searchedWord = searchedWord;
        this.wc = wc;
        this.future = future;

    }





    @Override
    public void start() {
        Map<String, Long> app = wc.occurrencesCount(document, searchedWord);

        future.complete(app);

    }

}
