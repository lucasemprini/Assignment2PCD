package view;

import com.sun.javafx.collections.ObservableListWrapper;
import exercise01.Folder;
import exercise01.WordCounter;
import exercise02.VerticleWordCounter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Pair;
import utility.MathUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

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

    private List<Pair<String, Long>> list = new ArrayList<>();
    private final ObservableList<Pair<String, Long>> obsForListView = new ObservableListWrapper<>(list);
    private WordCounter wordCounter;

    public void initialize() {
        pathField.setText("./src");
        maxDepthSpinner.increment(4);
        this.setComboBox();
        this.addSearchListener();
    }

    public void setEvents(WordCounter wordCounter) {
        this.wordCounter = wordCounter;
        this.wordCounter.addListener(ev -> {


            //TODO Migliorabile
            if (getValueFromComboBox() == 1) {


                Pair<Integer, Integer> totFilesFound = ev.getTotFilesFound();
                Pair<String, Long> fileAndOccurrences = ev.getFileFoundAndOccurences();
                synchronized (list) {
                    this.list.add(fileAndOccurrences);

                    Platform.runLater(() -> {
                        this.setListView();
                        this.setLabels(totFilesFound);
                    });
                }
            }

        });
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
            try {
                final String path = this.getPathFromField();
                final int depth = this.getDepthFromSpinner();
                final int exerciseToRun = this.getValueFromComboBox();
                final File file = new File(path);
                wordCounter.reset();
                this.list.clear();

                if (file.isDirectory() && depth >= 0 && exerciseToRun > 0) {
                    final Folder folder = Folder.fromDirectory(file, depth);
                    switch (exerciseToRun) {
                        case 1:
                            this.callTasks(folder, depth);
                            break;
                        case 2:
                            this.callVerticles(folder, new VerticleWordCounter(wordCounter), depth);
                            break;
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
     * @param folder la Folder di partenza digitata in input.
     * @param depth  la Max depth.
     */
    private void callTasks(final Folder folder, final int depth) {

        final long startTime = System.currentTimeMillis();
        Map<String, Long> filesMap = wordCounter.countOccurrencesInParallel(folder, Pattern.compile(REGEXP_TO_MATCH), depth);
        final long stopTime = System.currentTimeMillis();


        System.out.println(filesMap + " , fork / join search took " + (stopTime - startTime) + "ms");
    }


    /**
     * Metodo che lancia la ricerca sfruttando gli event loop di Vertx.
     * @param folder
     *  Directory di partenza
     * @param wordCounter
     *  Un wrapper di WordCounter che ne estende le funzionalità rendendolo un Verticle
     * @param depth
     *  Profondità da visitare
     */
    private void callVerticles(final Folder folder, final VerticleWordCounter wordCounter, final int depth) {
        AtomicInteger fileWithMatching = new AtomicInteger();
        AtomicInteger totFile = new AtomicInteger();

        wordCounter.countOccurrencesInParallel(folder, Pattern.compile(REGEXP_TO_MATCH),
                depth,
                map -> map.forEach((k, v) -> {
                    synchronized (list) {
                        this.list.add(new Pair<>(k, v));
                        totFile.incrementAndGet();
                        if (v > 0) {
                            fileWithMatching.incrementAndGet();
                        }

                        Platform.runLater(() -> {
                            this.setListView();
                            this.setLabels(new Pair<>(totFile.intValue(), fileWithMatching.intValue()));
                        });
                    }
                }));


    }


    /**
     * Metodo che setta la ListView con i valori trovati.
     */
    private void setListView() {
        this.filesListView.setVisible(!list.isEmpty());
       this.listPresentation.setText(LIST_PRESENTATION + (list.isEmpty() ? NO_FILES : ("   " + list.size())));


        this.filesListView.setItems(obsForListView);
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
     * @param totAndMatching la coppia che rappresenta il numero totale di file trovati
     *                       e il numero totale dei file con almeno un matching.
     */
    private void setLabels(final Pair<Integer, Integer> totAndMatching) {
        final Double percentage = ((double) totAndMatching.getValue() * 100) / (double) totAndMatching.getKey();
        double totMatches = 0;

        //System.out.println("FILE TOTALI: " + totAndMatching.getKey() + " FILE CON UN MATCH: " + totAndMatching.getValue());
        //System.out.println("List size: " + list.size());
        synchronized (list) {
            for (Pair<String, Long> el : list) {
                totMatches += el.getValue();
            }
            final Double meanMatches = totMatches / (double) totAndMatching.getValue();
            this.meanNumberOfMatchesLabel.setText(Double.toString(MathUtility.roundAvoid(meanMatches)));
            this.filesPercentageLabel.setText(Double.toString(MathUtility.roundAvoid(percentage)) + " %");

        }
    }


}
