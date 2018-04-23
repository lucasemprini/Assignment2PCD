package view;

import exercise01.DummyFileReader;
import exercise01.Folder;
import exercise01.WordCounter;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewController {
    public TextField maxDepthField;
    public TextField pathField;
    public Button buttonSearch;
    public Label presentationLabel;
    public Label pathLabel;
    public Label maxDepthLabel;
    public ScrollPane scrollPaneForListView;
    public Label filesPercentage;
    public Label meanNumberOfMatches;
    public ListView<String> filesListView;

    private static final String WRONG_INPUT = "WRONG INPUT!";

    //private List<String> list = new ArrayList<>();
    //private ListProperty<String> listProperty = new SimpleListProperty<>();

    public void initialize() {
        this.addSearchListener();
    }

    private void showAlert() {
        final Alert alert = new Alert(Alert.AlertType.ERROR, WRONG_INPUT, ButtonType.CLOSE);
        alert.showAndWait();
    }

    private String getPathFromField() {
        return this.pathField.getText();
    }

    private int getDepthFromField() {
        if(this.isDepthANumber(this.maxDepthField.getText())) {
            return Integer.parseInt(this.maxDepthField.getText());
        } else {
            return -1;
        }
    }

    private boolean isDepthANumber(final String depth) {
        final int integerDepth;
        try {
            integerDepth = Integer.parseInt(depth);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return integerDepth >= 0;
    }

    private void addSearchListener() {
        this.buttonSearch.setOnAction( e -> {

            final WordCounter wordCounter = new WordCounter();
            try {
                final String path = this.getPathFromField();
                final int depth = this.getDepthFromField();
                File file = new File(path);
                if(file.isDirectory() && depth >= 0) {
                    final Folder folder = Folder.fromDirectory(file, depth);
                    System.out.println(folder);

                    this.callTasks(folder, wordCounter, depth);
                } else {
                    this.showAlert();
                }
            } catch (IOException e1) {
                this.showAlert();
            }

            /*
            DummyFileReader df = new DummyFileReader();
            list = df.readFile();
            listProperty.set(FXCollections.observableArrayList(list));
            this.filesListView.itemsProperty().bind(listProperty);
            System.out.println(list);
            */
        });
    }

    private void callTasks(final Folder folder, final WordCounter wordCounter, final int depth) {
        Map<String, Long> counts;
        long startTime;
        long stopTime;

        /*

        startTime = System.currentTimeMillis();
        counts = wordCounter.countOccurrencesOnSingleThread(folder, "new", depth);
        stopTime = System.currentTimeMillis();
        System.out.println(counts + " , single thread search took " + (stopTime - startTime) + "ms");

        */

        startTime = System.currentTimeMillis();
        counts = wordCounter.countOccurrencesInParallel(folder, "new", depth);
        stopTime = System.currentTimeMillis();
        System.out.println(counts + " , fork / join search took " + (stopTime - startTime) + "ms");

    }
}
