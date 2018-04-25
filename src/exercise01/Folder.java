package exercise01;

import utility.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Folder {
    private final List<Folder> subFolders;
    private final List<Document> documents;
    private String dirName;
    
    private Folder(final String dirName,
                   final List<Folder> subFolders,
                   final List<Document> documents) {
        this.subFolders = subFolders;
        this.documents = documents;
        this.dirName = dirName;
    }
    
    public List<Folder> getSubFolders() {
        return this.subFolders;
    }
    
    public List<Document> getDocuments() {
        return this.documents;
    }
    
    public static Folder fromDirectory(final File dir, int depth) throws IOException {
        List<Document> documents = new LinkedList<>();
        List<Folder> subFolders = new LinkedList<>();

        for (File entry : Objects.requireNonNull(dir.listFiles())) {
            if (entry.isDirectory() && depth >= 0) {
                subFolders.add(Folder.fromDirectory(entry, depth-1));
            } else if (StringUtilities.isFileTextual(entry.getName())){
                documents.add(Document.fromFile(entry));
            }
        }
        return new Folder(dir.getName(), subFolders, documents);
    }

    @Override
    public String toString() {
        String toReturn = dirName;
        for (Folder fol : subFolders) {
            toReturn = toReturn.concat("\n" + fol.toString());
        }
        for (Document d : documents) {
            toReturn = toReturn.concat(" -- " + d.toString());
        }
        return toReturn;
    }
}

