package view;

import com.sun.javafx.collections.ObservableListWrapper;
import exercise01.Folder;
import exercise01.WordCounter;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewController {
    public TextField maxDepthField;
    public TextField pathField;
    public Button buttonSearch;
    public Label presentationLabel;
    public Label pathLabel;
    public Label maxDepthLabel;
    public ScrollPane scrollPaneForListView;
    public Label filesPercentageLabel;
    public Label meanNumberOfMatchesLabel;
    public ListView<Pair<String, Long>> filesListView;
    public Label listPresentation;

    private static final String WRONG_INPUT = "WRONG INPUT!";
    private static final String NO_FILES = " NO FILES FOUND!";
    private static final String LIST_PRESENTATION = "List of matching files:";

    private Map<String, Long> filesMap;


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
        final List<Pair<String, Long>> list = new ArrayList<>();
        final long startTime = System.currentTimeMillis();
        this.filesMap = wordCounter.countOccurrencesInParallel(folder, "new", depth);
        for(String s : filesMap.keySet()) {
            list.add(new Pair<>(s, filesMap.get(s)));
        }
        final long stopTime = System.currentTimeMillis();
        System.out.println(this.filesMap + " , fork / join search took " + (stopTime - startTime) + "ms");
        this.setListView(list);
        this.setLabels(list);
    }

    private void setListView(final List<Pair<String, Long>> list) {

        this.filesListView.setVisible(!list.isEmpty());
        this.listPresentation.setText(LIST_PRESENTATION + (list.isEmpty() ? NO_FILES : ""));

        final ObservableList<Pair<String, Long>> obs = new ObservableListWrapper<>(list);
        this.filesListView.setItems(obs);
        this.filesListView.setCellFactory( l -> new ListCell<Pair<String, Long>>() {

            @Override
            protected void updateItem(Pair<String, Long> entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(entry.getKey() + ":             " + entry.getValue()
                            + (entry.getValue() == 1 ? " occurrence " : " occurrences ") +" found");
                }
            }
        });
    }

    private void setLabels(final List<Pair<String, Long>> list) {
        final List<Pair<String, Long>> filesWithAtLeastOne = list.stream().filter(el -> el.getValue() > 0).collect(Collectors.toList());
        final Double percentage = ((double) filesWithAtLeastOne.size() * 100) / (double) list.size();
        double totMatches = 0;
        for (Pair<String, Long> p : filesWithAtLeastOne) {
            totMatches += p.getValue();
        }
        final Double meanMatches = totMatches / (double) filesWithAtLeastOne.size();
        this.meanNumberOfMatchesLabel.setText(Double.toString(roundAvoid(meanMatches)));
        this.filesPercentageLabel.setText(Double.toString(roundAvoid(percentage))+ " %");
    }

    private static double roundAvoid(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }
}
