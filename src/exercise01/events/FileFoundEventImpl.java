package exercise01.events;

import exercise01.Document;

public class FileFoundEventImpl implements FileFoundEvent {

    private Document doc;
    private int totFilesFound;

    public FileFoundEventImpl(final Document doc, final int totFilesFound) {
        this.doc = doc;
        this.totFilesFound = totFilesFound;
    }

    @Override
    public Document getFileFound() {
        return doc;
    }

    @Override
    public int getTotFilesFound() {
        return this.totFilesFound;
    }
}
