package exercise02;

import commons.model.Folder;
import exercise01.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.util.Map;
import java.util.regex.Pattern;

public class VerticleFolderSearch extends AbstractVerticle {

    private final Folder folder;
    private final Pattern searchedWord;
    private final WordCounter wc;
    private final int maxDepth;
    private final FutureOperation fut;

    public VerticleFolderSearch(final WordCounter wc, final Folder folder, final Pattern regexp, final int maxDepth, final FutureOperation fut) {
        super();
        this.wc = wc;
        this.folder = folder;
        this.searchedWord = regexp;
        this.maxDepth = maxDepth;
        this.fut = fut;
    }



    /**
     * Metodo che chiama i task sulle subfolders se presenti e se non si è raggiunta la MaxDepth.
     * @param hasNotReachedLimit true se posso cercare nelle subFolders, false altrimenti.
     */
    private void  searchAmongSubFolders(boolean hasNotReachedLimit) {
        if (hasNotReachedLimit) {
            folder.getSubFolders().forEach(subFolder -> getVertx().deployVerticle(new VerticleFolderSearch(wc, subFolder, searchedWord, maxDepth - 1, fut)));
        }
    }


    /**
     * Metodo che chiama i task per cercare nei documenti.
     */
    private void searchAmongDocs() {

        folder.getDocuments().forEach(document -> {
            Future<Map<String, Long>> fut = Future.future();
            fut.setHandler(map -> this.fut.onCompleted(map.result()));
            getVertx().deployVerticle(new VerticleDocumentSearch(wc, document, searchedWord, fut));
        });
    }



    @Override
    public void start() {
       this.searchAmongSubFolders((maxDepth > 0 && !folder.getSubFolders().isEmpty()));
       this.searchAmongDocs();
    }
}
