/*
 * Fork-Join example, adapted from
 * http://www.oracle.com/technetwork/articles/java/fork-join-422606.html
 * 
 */
package exercise01;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {    

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    private String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }
    
    public Map<String, Long> occurrencesCount(Document document, Pattern regexp) {
        long count = 0;
        Map<String, Long> map = new HashMap<>();
        for (String line : document.getLines()) {
            for (String word : wordsIn(line)) {
                Matcher m =regexp.matcher(word);
                if (m.matches()) {
                    count = count + 1;
                }
            }
        }
        map.put(document.toString(), count);
        return map;
    }

    /*
    public Long countOccurrencesOnSingleThread(final Folder folder, final String searchedWord, int depth) {
        long count = 0;
        if(depth > 1 && folder.getSubFolders() != null) {
            for (Folder subFolder : folder.getSubFolders()) {
                count = count + countOccurrencesOnSingleThread(subFolder, searchedWord, --depth);
            }
        } else {
            for (Document document : folder.getDocuments()) {
                count = count + occurrencesCount(document, searchedWord);
            }
        }
        return count;
    }
    */
    public Map<String, Long> countOccurrencesInParallel(Folder folder, Pattern regexp, int depth) {
        return forkJoinPool.invoke(new FolderSearchTask(this, folder, regexp, depth));
    }

}
