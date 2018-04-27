package exercise02;

import exercise01.Document;
import exercise01.Folder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VertWordCounter extends AbstractVerticle {



    /**
     * Metodo che ritorna un Lista di parole a partire da una line di un Document.
     *
     * @param line la riga di un documento.
     * @return la riga splittata in parole.
     */
    private List<String> wordsIn(String line) {
        return Arrays.asList(line.trim().split("(\\s|\\p{Punct})+"));
    }


    /**
     * Metodo che conta le occorrenze all'interno di un Document.
     *
     * @param document il documento in cui cercare.
     * @param regexp   la regular expression da cercare.
     * @return una mappa che associa al nome del documento il numero di occorrenze trovate.
     */
    public Map<String, Long> occurencesCount(Document document, Pattern regexp) {
        AtomicLong count = new AtomicLong();
        Map<String, Long> map = new HashMap<>();

        document.getLines().forEach(line -> {
            wordsIn(line).forEach(word -> {
                Matcher m = regexp.matcher(word);
                if (m.matches()) {
                    count.getAndIncrement();
                }
            });
        });

        map.put(document.toString(), new Long(count.get()));

        return map;
    }

    public void start(Future<Void> future) {

    }
}
