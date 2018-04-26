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
    
    FolderSearchTask(final WordCounter wc, final Folder folder,
                     final Pattern regexp, final int maxDepth) {
        super();
        this.wc = wc;
        this.folder = folder;
        this.searchedWord = regexp;
        this.maxDepth = maxDepth;
    }
    
    @Override
    protected Map<String, Long> compute() {
        List<RecursiveTask<Map<String, Long>>> forks = new LinkedList<>();
        Map<String, Long> map = new HashMap<>();
        if(maxDepth > 0 && !folder.getSubFolders().isEmpty()) {
            for (Folder subFolder : folder.getSubFolders()) {
                FolderSearchTask task = new FolderSearchTask(wc, subFolder, searchedWord, maxDepth-1);
                forks.add(task);
                task.fork();
            }
        }

        for (Document document : folder.getDocuments()) {
            DocumentSearchTask task = new DocumentSearchTask(wc, document, searchedWord);
            forks.add(task);
            task.fork();
        }

        for (RecursiveTask<Map<String, Long>> task : forks) {
            task.join().forEach(map::putIfAbsent);
        }
        return map;
    }
}
    