package exercise01;

import io.vertx.core.Vertx;

public class DummyFileReader {

    private Vertx vertx = Vertx.vertx();

    public DummyFileReader() {}

    public void readFile() {
        // Read a file
        vertx.fileSystem().readFile( "C:/Users/lucse/IdeaProjects/Assignment2/src/exercise01/file.txt", result -> {
            if (result.succeeded()) {
                System.out.println(result.result());
            } else {
                System.err.println("Oh oh ..." + result.cause());
            }
        });
    }
}
