package exercise03;

import commons.model.Folder;
import exercise01.WordCounter;
import io.reactivex.Observer;
//import org.reactivestreams.Subscriber;

import java.util.Map;
import java.util.regex.Pattern;

public class ReactiveWordCounter {
    private final WordCounter wc;

    public ReactiveWordCounter(final WordCounter wc) {
        this.wc = wc;
    }

    /**
     * Lancia la ricerca su una cartella.
     * @param folder
     *  Directory di riferimento
     * @param regexp
     *  Regular expression
     * @param depth
     *  Profondità
     * @param subscriber
     * Operazione da passare alla future da scatenare al momento del completamento
     */
    public void countOccurrencesInParallel(Folder folder, Pattern regexp,
                                           int depth, Observer<Map<String, Long>> subscriber) {
        new ReactiveFolderSearch(wc, folder, regexp, depth, subscriber);
    }

}
