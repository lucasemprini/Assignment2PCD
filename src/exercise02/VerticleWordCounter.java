package exercise02;

import commons.model.Folder;
import exercise01.WordCounter;
import io.vertx.core.Vertx;

import java.util.regex.Pattern;

public class VerticleWordCounter{

    private final WordCounter wc;

    public VerticleWordCounter(final WordCounter wc) {
        super();
        this.wc = wc;
    }


    /**
     * Lancia la ricerca su una cartella.
     * @param folder
     *  Directory di riferimento
     * @param regexp
     *  Regular expression
     * @param depth
     *  Profondità
     * @param future
     * Operazione da passare alla future da scatenare al momento del completamento
     */
    public void countOccurrencesInParallel(Folder folder, Pattern regexp, int depth, FutureOperation future) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new VerticleFolderSearchTask(this.wc, folder, regexp, depth, future));
    }


}
