import exercise01.WordCounter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.ViewController;

import java.io.IOException;

public class Main extends Application{

    private static final String LAYOUT_PATH = "/view/view.fxml";
    private static final String WINDOW_TITLE = "REGEXP MATCHING TOOL";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final FXMLLoader loader = initGui(primaryStage);
        final ViewController initialWindow = loader.getController();
        initialWindow.setEvents(new WordCounter());
        primaryStage.show();
    }

    /**
     * Metodo per inizializzare la GUI dell'applicazione.
     * @param primaryStage lo stage primario della GUI JavaFX.
     * @return il FXMLLoader.
     */
    private FXMLLoader initGui(final Stage primaryStage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUT_PATH));
        final Parent root = loader.load();
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        return loader;
    }
}
