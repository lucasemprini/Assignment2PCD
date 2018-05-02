package utility;

import javafx.util.Pair;

public final class StringUtilities {
    private StringUtilities() {}

    /**
     * Metodo che filtra i file in base alla loro estensione.
     * @param fileName il nome di un file.
     * @return true se risulta testuale, false altrimenti.
     */
    public static boolean isFileTextual(final String fileName) {
        return fileName.endsWith("java")
                || fileName.endsWith("txt")
                || fileName.endsWith("xml")
                || fileName.endsWith("md")
                || fileName.endsWith(".c")
                || fileName.endsWith(".h")
                || fileName.endsWith(".cpp")
                || fileName.endsWith(".hpp")
                || fileName.endsWith("html")
                || fileName.endsWith("css")
                || fileName.endsWith("js")
                || fileName.endsWith("sql")
                || fileName.endsWith("sh");
    }

    /**
     * Metodo che costruisce la rappresentazione ad albero a partire dal percorso di un file.
     * @param path il percorso di cui costruire l'albero.
     * @return la Stringa del percorso modificata.
     */
    public static String treePath(final String path) {
        final String regex = "(/)|(\\\\)";
        final String toReturn = path.replaceAll(regex, "\n\\\\");
        return toReturn.substring(2);
    }

    /**
     * Metodo utile per settare le stringhe da mostrare nelle entry della ListView.
     * @param entry una entry della List.
     * @return la stringa formattata.
     */
    public static String setEntryListView(final Pair<String, Long> entry) {
        return entry.getKey() + " :             " + entry.getValue()
                + (entry.getValue() == 1 ? " occurrence " : " occurrences ") + "found";
    }
}
