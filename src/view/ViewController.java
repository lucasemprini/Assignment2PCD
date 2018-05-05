package view;

import com.sun.javafx.collections.ObservableListWrapper;
import exercise01.Folder;
import exercise01.WordCounter;
import exercise02.VerticleWordCounter;
import exercise03.ReactiveWordCounter;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Pair;
import utility.MathUtility;
import utility.StringUtilities;

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

    private int totMatches = 0;

    private static final String WRONG_INPUT = "WRONG INPUT!";
    private static final String NO_FILES = " NO FILES FOUND!";
    private static final String LIST_PRESENTATION = "List of matching files:";

    /**
     * REGULAR EXPRESSION DA TROVARE.
     */
    private static final String REGEXP_TO_MATCH = "[n, i][a-z]*";

    private final List<Pair<String, Long>> list = new ArrayList<>();
    private final ObservableList<Pair<String, Long>> obsForListView = new ObservableListWrapper<>(list);

    public void initialize() {
        this.setComboBox();
        this.configureListView();
        this.addSearchListener();
    }

    /**
     * Metodo che aggiunge gli elementi correttamente alla lista, chiama i metodi di aggiornamento della GUI
     * e utilizza lo standard output ai fini di debug.
     * @param totFilesFound la coppia da aggiungere alla Lista.
     * @param fileAndOccurrences la coppia che serve per aggiornare le Label.
     * @param debug variabile che determina in quale esercizio ci si trova.
     */
    private void updateGUI(Pair<Integer, Integer> totFilesFound, Pair<String, Long> fileAndOccurrences, int debug) {
        synchronized (list) {
            this.list.add(fileAndOccurrences);
            this.totMatches += fileAndOccurrences.getValue();
            switch (debug) {
                case 1 : System.out.println("Qui c'è un Task event"); break;
                case 2 : System.out.println("Qui c'è un Verticle event"); break;
                case 3 : System.out.println("Qui c'è un Reactive event"); break;
            }

            System.out.flush();

        }
        Platform.runLater(() -> {
            System.out.println("Qui c'è un platform runLater");
            System.out.flush();
            this.setListView();
            this.setLabels(totFilesFound, totMatches);
        });
    }
    /**
     * Metodo da chiamare UNA volta alla creazione della GUI:
     * setta la CellFactory della ListView.
     */
    private void configureListView() {
        this.filesListView.setCellFactory(l -> new ListCell<Pair<String, Long>>() {
            @Override
            protected void updateItem(Pair<String, Long> entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(StringUtilities.setEntryListView(entry));
                }
            }
        });
    }

    /**
     * Metodo che setta la ListView con i valori trovati.
     */
    private void setListView() {
        System.out.println("Qui c'è una setListView");
        System.out.flush();
        this.filesListView.setVisible(!list.isEmpty());
        this.listPresentation.setText(LIST_PRESENTATION + (list.isEmpty() ? NO_FILES : ("   " + list.size())));
        this.filesListView.setItems(obsForListView);
    }

    /**
     * Metodo che setta le due label.
     *
     * @param totAndMatching la coppia che rappresenta il numero totale di file trovati
     *                       e il numero totale dei file con almeno un matching.
     * @param totMatches il numero totale di parole che corrispondono alla REGEXP.
     */
    private void setLabels(Pair<Integer, Integer> totAndMatching, int totMatches) {
        System.out.println("Qui c'è una setLabels: totMatches = " + totMatches + "      "
                            + "totAndMatching : " +totAndMatching.getKey() + " - " + totAndMatching.getValue());
        System.out.flush();
        final Double percentage = ((double) totAndMatching.getValue() * 100) / (double) totAndMatching.getKey();
        final Double meanMatches = totMatches / (double) totAndMatching.getValue();

        this.meanNumberOfMatchesLabel.setText(Double.toString(MathUtility.roundAvoid(meanMatches)));
        this.filesPercentageLabel.setText(Double.toString(MathUtility.roundAvoid(percentage)) + " %");
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
                this.list.clear();
                this.totMatches = 0;

                if (file.isDirectory() && depth >= 0 && exerciseToRun > 0) {
                    final Folder folder = Folder.fromDirectory(file, depth);
                    switch (exerciseToRun) {
                        case 1:
                            this.callTasks(folder, new WordCounter(), depth);
                            break;
                        case 2:
                            callVerticles(folder, new VerticleWordCounter(new WordCounter()), depth);
                            break;
                        case 3:
                            callReactive(folder, new ReactiveWordCounter(new WordCounter()), depth);
                            break;
                        default: showAlert();
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
     * Metodo utile all'esercizio 1: setta gli eventi del wordCounter.
     */
    private void setEvents(WordCounter wordCounter) {
        wordCounter.addListener(ev -> {
            Pair<Integer, Integer> totFilesFound = ev.getTotFilesFound();
            Pair<String, Long> fileAndOccurrences = ev.getFileFoundAndOccurences();
            this.updateGUI(totFilesFound, fileAndOccurrences, 1);
        });

    }

    /**
     * Metodo che chiama il WordCounter e fa partire la ricerca.
     *
     * @param folder la Folder di partenza digitata in input.
     * @param depth  la Max depth.
     */
    private void callTasks(final Folder folder, WordCounter wordCounter, final int depth) {
        this.setEvents(wordCounter);
        final long startTime = System.currentTimeMillis();
        Map<String, Long> filesMap = wordCounter.countOccurrencesInParallel(folder, Pattern.compile(REGEXP_TO_MATCH), depth);
        final long stopTime = System.currentTimeMillis();

        System.out.println(filesMap.size() + " , fork / join search took " + (stopTime - startTime) + "ms");
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
                        totFile.incrementAndGet();
                        if (v > 0) {
                            fileWithMatching.incrementAndGet();
                        }
                        this.updateGUI(new Pair<>(totFile.intValue(),
                                fileWithMatching.intValue()), new Pair<>(k, v),
                                2);
                    }
                }));


    }


    private void callReactive(final Folder folder, final ReactiveWordCounter wordCounter, final int depth) {
        AtomicInteger fileWithMatching = new AtomicInteger();
        AtomicInteger totFile = new AtomicInteger();

        final Observer<Map<String, Long>> observer = new Observer<Map<String, Long>>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                //Nothing
            }

            @Override
            public void onNext(Map<String, Long> map) {
                map.forEach((k,v) -> {
                    synchronized (list) {

                        totFile.incrementAndGet();
                        if (v > 0) {
                            fileWithMatching.incrementAndGet();
                        }
                        updateGUI(new Pair<>(totFile.intValue(),
                                fileWithMatching.intValue()), new Pair<>(k, v),
                                3);
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Some problems..");
            }

            @Override
            public void onComplete() {
                System.out.println("Finish of reactive part");
            }
        };

        wordCounter.countOccurrencesInParallel(folder, Pattern.compile(REGEXP_TO_MATCH),
                depth, observer );

    }


}
