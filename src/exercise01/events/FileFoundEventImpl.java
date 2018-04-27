package exercise01.events;

import javafx.util.Pair;

public class FileFoundEventImpl implements FileFoundEvent {

    private Pair<String, Long> doc;
    private Pair<Integer, Integer> totFilesFound;

    public FileFoundEventImpl(final Pair<String, Long> doc,
                              final Pair<Integer, Integer> totFilesFound) {
        this.doc = doc;
        this.totFilesFound = totFilesFound;
    }

    @Override
    public Pair<String, Long> getFileFoundAndOccurences() {
        return doc;
    }

    @Override
    public Pair<Integer, Integer> getTotFilesFound() {
        return this.totFilesFound;
    }
}
