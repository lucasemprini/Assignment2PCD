package commons.model;

import commons.utility.StringUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Document {

	private final List<String> lines;
	private String docName;

    /**
     * Costruttore per un Document.
     * @param docName Il nome del  documento sottoforma di Stringa
     * @param lines la lista delle lines del File.
     */
    private Document(final String docName, final List<String> lines) {
        this.docName = docName;
        this.lines = lines;
    }

    /**
     * Metodo getter per il campo lines.
     * @return la lista contenente le linee del File.
     */
    public List<String> getLines() {
        return this.lines;
    }

    /**
     * Metodo builder che costruisce un Document a partire da un file.
     * @param file il File da cui costruire il Document.
     * @return un'istanza di Document.
     * @throws IOException Se il BufferedReader tira IOException.
     */
    public static Document fromFile(File file) throws IOException {
        List<String> lines = new LinkedList<String>();
        BufferedReader reader;
        try {
        	reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (Exception ex){
        	ex.printStackTrace();
        }
        return new Document(StringUtilities.treePath(file.getPath()), lines);
    }

    @Override
    public String toString() {
        return docName;
    }
}
