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

    /**
     * Costruttore della classe Folder.
     *
     * @param dirName    il nome della Directory.
     * @param subFolders la lista di subFolder che la Folder contiene.
     * @param documents  la lista di documenti che la Folder contiene.
     */
    private Folder(final String dirName,
                   final List<Folder> subFolders,
                   final List<Document> documents) {
        this.subFolders = subFolders;
        this.documents = documents;
        this.dirName = dirName;
    }

    private static boolean checkDirAndSub(File dir) {
        return !(dir == null || dir.listFiles() == null);
    }

    /**
     * Metodo getter per il campo subFolders.
     *
     * @return la lista di subFolders.
     */
    public List<Folder> getSubFolders() {
        return this.subFolders;
    }

    /**
     * Metodo getter per il campo documents.
     *
     * @return la lista di documents.
     */
    public List<Document> getDocuments() {
        return this.documents;
    }

    /**
     * Metodo builder che crea un'istanza di Folder.
     *
     * @param dir   la directory.
     * @param depth la profondità corrente.
     * @return un'istanza di Folder corrispondente alla directory.
     * @throws IOException se il File tira una IOException.
     */
    public static Folder fromDirectory(File dir, int depth) throws IOException {

        List<Document> documents = new LinkedList<>();
        List<Folder> subFolders = new LinkedList<>();
        if (checkDirAndSub(dir)) {
            for (File entry : Objects.requireNonNull(dir.listFiles())) {
                if (entry.isDirectory() && depth >= 0) {
                    subFolders.add(Folder.fromDirectory(entry, depth - 1));
                } else if (StringUtilities.isFileTextual(entry.getName())) {
                    documents.add(Document.fromFile(entry));
                }
            }
            return new Folder(dir.getName(), subFolders, documents);
        } else {
            throw new IOException();
        }
    }

    @Override
    public String toString () {
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

