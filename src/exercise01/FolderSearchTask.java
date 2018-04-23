package exercise01;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class FolderSearchTask extends RecursiveTask<Map<String, Long>> {
    private final Folder folder;
    private final String searchedWord;
    private final WordCounter wc;
    private final Map<String, Long> map;
    private int maxDepth;
    
    FolderSearchTask(final WordCounter wc, final Folder folder,
                     final String searchedWord, final int maxDepth, final Map<String, Long> map) {
        super();
        this.wc = wc;
        this.folder = folder;
        this.searchedWord = searchedWord;
        this.maxDepth = maxDepth;
        this.map = map;
    }
    
    @Override
    protected Map<String, Long> compute() {
        List<RecursiveTask<Map<String, Long>>> forks = new LinkedList<>();
        if(maxDepth > 0 && folder.getSubFolders() != null) {
            for (Folder subFolder : folder.getSubFolders()) {
                FolderSearchTask task = new FolderSearchTask(wc, subFolder, searchedWord, --maxDepth, map);
                forks.add(task);
                task.fork();
            }
        }

        for (Document document : folder.getDocuments()) {
            DocumentSearchTask task = new DocumentSearchTask(wc, document, searchedWord);
            System.out.println("DOCUMENT: " + document);
            forks.add(task);
            task.fork();
        }

        for (RecursiveTask<Map<String, Long>> task : forks) {
            System.out.println("IN TASK: " + task.join());
            task.join().forEach(map::putIfAbsent);
        }
        return map;
    }
}
    