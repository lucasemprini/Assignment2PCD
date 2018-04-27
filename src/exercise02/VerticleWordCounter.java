package exercise02;

import exercise01.Folder;
import exercise01.WordCounter;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.Map;
import java.util.regex.Pattern;

public class VerticleWordCounter{

    private final WordCounter wc;

    public VerticleWordCounter(final WordCounter wc) {
        super();
        this.wc = wc;
    }



    public void countOccurrencesInParallel(Folder folder, Pattern regexp, int depth, FutureOperation future) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new VerticleFolderSearchTask(this.wc, folder, regexp, depth, future));
    }
}
