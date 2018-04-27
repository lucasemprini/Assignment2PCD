package view;

import com.sun.javafx.collections.ObservableListWrapper;
import exercise01.Folder;
import exercise01.WordCounter;
import exercise02.VerticleWordCounter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import utility.MathUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ViewController {
    public TextField pathField;
    public Spinner<Integer> maxDepthSpinner;
    public Button buttonSearch;
    public Label presentationLabel;
    public Label pathLabel;
    public Label maxDepthLabel;
    public Label filesPercentageLabel;
    public Label meanNumberOfMatchesLabel;
    public ListView<Pair<String, Long>> filesListView;
    public Label listPresentation;
    public ComboBox<Integer> comboExercise;

    private static final String WRONG_INPUT = "WRONG INPUT!";
    private static final String NO_FILES = " NO FILES FOUND!";
    private static final String LIST_PRESENTATION = "List of matching files:";

    /**
     * REGULAR EXPRESSION DA TROVARE.
     */
    private static final String REGEXP_TO_MATCH = "[n, i][a-z]*";

    private Map<String, Long> filesMap;

    public void initialize() {
        this.setComboBox();
        this.addSearchListener();
    }

    /**
     * Metodo che attiva l'alert se un input è errato.
     */
    private void showAlert() {
        final Alert alert = new Alert(Alert.AlertType.ERROR, WRONG_INPUT, ButtonType.CLOSE);
        alert.showAndWait();
    }

    /**
     * Metodo per settare la ComboBox.
     */
    private void setComboBox() {
        ObservableList<Integer> options =
                FXCollections.observableArrayList(1, 2, 3);

        this.comboExercise.setItems(options);
    }

    /**
     * Metodo che chiede al TextField il testo passatogli in input.
     *
     * @return il testo digitato nel Field del path.
     */
    private String getPathFromField() {
        return this.pathField.getText();
    }

    /**
     * Metodo che chiede allo Spinner il valore passatogli in input.
     *
     * @return il valore inserito nello Spinner della maxDepth.
     */
    private int getDepthFromSpinner() {
        return this.maxDepthSpinner.getValue();
    }

    /**
     * Metodo che ritorna il valore selezionato nella ComboBox.
     *
     * @return il valore della ComboBox che indica l'esercizio da lanciare.
     */
    private int getValueFromComboBox() {
        return this.comboExercise.getValue() == null ? 0 : this.comboExercise.getValue();
    }

    /**
     * Metodo che setta il Listener al Search Button.
     */
    private void addSearchListener() {
        this.buttonSearch.setOnAction(e -> {
            final WordCounter wordCounter = new WordCounter();
            try {
                final String path = this.getPathFromField();
                final int depth = this.getDepthFromSpinner();
                final int exerciseToRun = this.getValueFromComboBox();
                final File file = new File(path);

                if (file.isDirectory() && depth >= 0 && exerciseToRun > 0) {
                    final Folder folder = Folder.fromDirectory(file, depth);
                    switch (exerciseToRun) {
                        case 1:
                            this.callTasks(folder, wordCounter, depth);
                        case 2:
                            this.callVerticles(folder, new VerticleWordCounter(wordCounter), depth);
                        case 3:
                            break; //TODO
                    }
                } else {
                    this.showAlert();
                }
            } catch (IOException e1) {
                this.showAlert();
            }
        });
    }

    /**
     * Metodo che chiama il WordCounter e fa partire la ricerca.
     *
     * @param folder      la Folder di partenza digitata in input.
     * @param wordCounter l'istanza di WordCounter.
     * @param depth       la Max depth.
     */
    private void callTasks(final Folder folder, final WordCounter wordCounter, final int depth) {
        final List<Pair<String, Long>> list = new ArrayList<>();

        final long startTime = System.currentTimeMillis();
        this.filesMap = wordCounter.countOccurrencesInParallel(folder, Pattern.compile(REGEXP_TO_MATCH), depth);
        final long stopTime = System.currentTimeMillis();

        for (String s : filesMap.keySet()) {
            list.add(new Pair<>(s, filesMap.get(s)));
        }

        System.out.println(this.filesMap + " , fork / join search took " + (stopTime - startTime) + "ms");

        this.setListView(list);
        this.setLabels(list);
    }

    private void callVerticles(final Folder folder, final VerticleWordCounter wordCounter, final int depth) {
        final List<Pair<String, Long>> list = new ArrayList<>();
        this.filesMap = new HashMap<>();
        final long startTime = System.currentTimeMillis();
        /*Future<Map<String, Long>> fut = Future.future();
        fut.setHandler(map -> {
           map.result().forEach(this.filesMap::putIfAbsent);
        });*/
        wordCounter.countOccurrencesInParallel(folder, Pattern.compile(REGEXP_TO_MATCH),
                depth,
                map -> {
                    map.forEach((k,v) -> {
                        System.out.println("Path: " + k + " / Value: " + v);
                        this.filesMap.putIfAbsent(k, v);
                    });
                });
        final long stopTime = System.currentTimeMillis();


        this.setListView(list);
        this.setLabels(list);
    }

    /**
     * Metodo che setta la ListView con i valori trovati.
     *
     * @param list la lista di coppie String-Long.
     */
    private void setListView(final List<Pair<String, Long>> list) {

        this.filesListView.setVisible(!list.isEmpty());
        this.listPresentation.setText(LIST_PRESENTATION + (list.isEmpty() ? NO_FILES : ("   " + list.size())));

        final ObservableList<Pair<String, Long>> obs = new ObservableListWrapper<>(list);
        this.filesListView.setItems(obs);
        this.filesListView.setCellFactory(l -> new ListCell<Pair<String, Long>>() {

            @Override
            protected void updateItem(Pair<String, Long> entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(entry.getKey() + " :             " + entry.getValue()
                            + (entry.getValue() == 1 ? " occurrence " : " occurrences ") + "found");
                }
            }
        });
    }

    /**
     * Metodo che setta le due label.
     *
     * @param list la lista di coppie String-Long.
     */
    private void setLabels(final List<Pair<String, Long>> list) {
        final List<Pair<String, Long>> filesWithAtLeastOne = list.stream().filter(el -> el.getValue() > 0).collect(Collectors.toList());
        final Double percentage = ((double) filesWithAtLeastOne.size() * 100) / (double) list.size();
        double totMatches = 0;
        for (Pair<String, Long> p : filesWithAtLeastOne) {
            totMatches += p.getValue();
        }
        final Double meanMatches = totMatches / (double) filesWithAtLeastOne.size();
        this.meanNumberOfMatchesLabel.setText(Double.toString(MathUtility.roundAvoid(meanMatches)));
        this.filesPercentageLabel.setText(Double.toString(MathUtility.roundAvoid(percentage)) + " %");
    }
}
