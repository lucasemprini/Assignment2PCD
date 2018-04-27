package exercise01;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

public class FolderSearchTask extends RecursiveTask<Map<String, Long>> {
    private final Folder folder;
    private final Pattern searchedWord;
    private final WordCounter wc;
    private int maxDepth;

    /**
     * Costruttore per il task di ricerca nelle Folder.
     * @param wc l'istanza di WordCounter.
     * @param folder la Folder su cui cercare.
     * @param regexp la Regular Expression da cercare.
     * @param maxDepth la maxDepth limite.
     */
    FolderSearchTask(final WordCounter wc, final Folder folder,
                     final Pattern regexp, final int maxDepth) {
        super();
        this.wc = wc;
        this.folder = folder;
        this.searchedWord = regexp;
        this.maxDepth = maxDepth;
    }

    /**
     * Metodo che chiama i task sulle subfolders se presenti e se non si è raggiunta la MaxDepth.
     * @param forks la lista di Task.
     * @param hasNotReachedLimit true se posso cercare nelle subFolders, false altrimenti.
     */
    private void searchAmongSubFolders(List<RecursiveTask<Map<String, Long>>> forks, boolean hasNotReachedLimit) {
        if(hasNotReachedLimit) {
            for (Folder subFolder : folder.getSubFolders()) {
                FolderSearchTask task = new FolderSearchTask(wc, subFolder, searchedWord, maxDepth-1);
                forks.add(task);
                task.fork();
            }
        }
    }

    /**
     * Metodo che chiamai task per cercare nei documenti.
     * @param forks la lista dei task.
     */
    private void searchAmongDocs(List<RecursiveTask<Map<String, Long>>> forks) {
        for (Document document : folder.getDocuments()) {
            DocumentSearchTask task = new DocumentSearchTask(wc, document, searchedWord);

            forks.add(task);
            task.fork();
        }
    }
    
    @Override
    protected Map<String, Long> compute() {
        List<RecursiveTask<Map<String, Long>>> forks = new LinkedList<>();
        Map<String, Long> map = new HashMap<>();

        this.searchAmongSubFolders(forks, (maxDepth > 0 && !folder.getSubFolders().isEmpty()));
        this.searchAmongDocs(forks);

        for (RecursiveTask<Map<String, Long>> task : forks) {
            task.join().forEach(map::putIfAbsent);
        }
        return map;
    }
}
    