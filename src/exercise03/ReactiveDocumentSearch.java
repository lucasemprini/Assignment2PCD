package exercise03;

import exercise01.Document;
import exercise01.WordCounter;
import io.reactivex.Observer;

import java.util.Map;
import java.util.regex.Pattern;

public class ReactiveDocumentSearch {
    private final Document document;
    private final Pattern searchedWord;
    private final WordCounter wc;
    private final Observer<Map<String, Long>> subscriber;

    public ReactiveDocumentSearch(final WordCounter wc, final Document document, final Pattern searchedWord,
                                  final Observer<Map<String, Long>> subscriber) {
        this.document = document;
        this.searchedWord = searchedWord;
        this.wc = wc;
        this.subscriber = subscriber;

        compute();
    }

    private void compute() {
        subscriber.onNext(wc.occurrencesCount(document, searchedWord));
    }
}
