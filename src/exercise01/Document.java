package exercise01;

import utility.StringUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Document {

	private final List<String> lines;
	private String docName;
    
    private Document(final String docName, final List<String> lines) {
        this.docName = docName;
        this.lines = lines;
    }
    
    public List<String> getLines() {
        return this.lines;
    }
    
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
