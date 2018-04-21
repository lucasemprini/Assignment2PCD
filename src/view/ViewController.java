package view;

import exercise01.DummyFileReader;
import javafx.scene.control.*;

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
    public ListView filesListView;

    public void initialize() {
        this.addSearchListener();
    }

    private void addSearchListener() {
        this.buttonSearch.setOnAction( e -> {
            DummyFileReader df = new DummyFileReader();
            df.readFile();
        });
    }
}
