package exercise01;

import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

/**
 * PROVE CON VERTX
 */
public class DummyFileReader {

    private Vertx vertx = Vertx.vertx();
    private List<String> fileList = new ArrayList<>();

    public DummyFileReader() {}

    public synchronized List<String> readFile() {
        // Read a file
        vertx.fileSystem().readDir( "./src/", ".*.java", result -> {
            if (result.succeeded()) {
                this.fileList.addAll(result.result());
                System.out.println(result.result());
            } else {
                System.err.println("Oh oh ..." + result.cause());
                this.fileList.add("No Results Found :(");
            }
        });
        return fileList;
    }
}
