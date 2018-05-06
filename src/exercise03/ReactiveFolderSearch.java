package exercise03;

import commons.model.Folder;
import exercise01.WordCounter;
import io.reactivex.Observer;
//import org.reactivestreams.Subscriber;

import java.util.Map;
import java.util.regex.Pattern;

public class ReactiveFolderSearch {
    private final Folder folder;
    private final Pattern searchedWord;
    private final WordCounter wc;
    private int maxDepth;
    private final Observer<Map<String, Long>> subscriber;


    /**
     * Costruttore per l'oggetto reattivo di ricerca nelle Folder.
     * @param wc l'istanza di WordCounter.
     * @param folder la Folder su cui cercare.
     * @param searchedWord la Regular Expression da cercare.
     * @param maxDepth la maxDepth limite.
     */
    ReactiveFolderSearch(final WordCounter wc, final Folder folder, final Pattern searchedWord,
                         final int maxDepth, final Observer<Map<String, Long>> subscriber) {
        this.wc = wc;
        this.folder = folder;
        this.searchedWord = searchedWord;
        this.maxDepth = maxDepth;
        this.subscriber = subscriber;

        compute();
    }

    private void searchAmongSubFolders(boolean hasNotReachedLimit) {
        if (hasNotReachedLimit) {
            folder.getSubFolders().forEach(subFolder -> new ReactiveFolderSearch(wc, subFolder, searchedWord, maxDepth - 1, subscriber));
        }
    }

    private void searchAmongDocs() {
        folder.getDocuments().forEach(document -> new ReactiveDocumentSearch(wc, document, searchedWord, subscriber));
    }

    private void compute() {
        searchAmongSubFolders(maxDepth > 0 && !folder.getSubFolders().isEmpty());
        searchAmongDocs();
    }


}
