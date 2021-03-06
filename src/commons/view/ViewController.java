package commons.view;

import commons.model.Folder;
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
import commons.utility.MathUtility;
import commons.utility.StringUtilities;

import java.io.File;
import java.io.IOException;
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
    private static final String DEBUG_EVENT_PRE = "Found a ";
    private static final String DEBUG_EVENT_POST = " event : list size = ";
    private static final String DEBUG_TOTMATCHES = " tot. matches = ";
    private static final String DEBUG_SETLISTVIEW = "Triggered method setListView : list size = ";
    private static final String DEBUG_SETLABELS = "Triggered method setLabels : tot. files found = ";
    private static final String DEBUG_SETLABELS_POST = " - tot. files with at least one match = ";

    /**
     * REGULAR EXPRESSION DA TROVARE.
     */
    private static final String REGEXP_TO_MATCH = "[n, i][a-z]*";

    public void initialize() {
        this.setComboBox();
        this.configureListView();
        this.addSearchListener();
    }

    /**
     * Metodo che aggiunge gli elementi correttamente alla lista, chiama i metodi di aggiornamento della GUI
     * e utilizza lo standard output ai fini di debug.
     * @param totFilesFoundAndMatching la coppia che serve per aggiornare le Label.
     * @param fileAndOccurrences la coppia da aggiungere alla Lista.
     * @param debug variabile che determina in quale esercizio ci si trova.
     */
    private void updateGUI(Pair<Integer, Integer> totFilesFoundAndMatching, Pair<String, Long> fileAndOccurrences, int debug) {

        synchronized (this.filesListView.getItems()) {
            Platform.runLater(() -> {
                this.filesListView.getItems().add(fileAndOccurrences);
                this.totMatches += fileAndOccurrences.getValue();
                switch (debug) {
                    case 1 : System.out.println(DEBUG_EVENT_PRE + "Task" + DEBUG_EVENT_POST
                            + this.filesListView.getItems().size()
                            + DEBUG_TOTMATCHES + totMatches); break;
                    case 2 : System.out.println(DEBUG_EVENT_PRE + "Verticle" + DEBUG_EVENT_POST
                            + this.filesListView.getItems().size()
                            + DEBUG_TOTMATCHES + totMatches); break;
                    case 3 : System.out.println(DEBUG_EVENT_PRE + "Reactive" + DEBUG_EVENT_POST
                            + this.filesListView.getItems().size()
                            + DEBUG_TOTMATCHES + totMatches); break;
                }

                System.out.flush();
                this.setListView();
                this.setLabels(totFilesFoundAndMatching, totMatches);
            });
        }

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
        System.out.println(DEBUG_SETLISTVIEW + this.filesListView.getItems().size());
        System.out.flush();
        this.filesListView.setVisible(!this.filesListView.getItems().isEmpty());
        this.listPresentation.setText(LIST_PRESENTATION +
                (this.filesListView.getItems().isEmpty() ? NO_FILES : ("\t" + this.filesListView.getItems().size())));
    }

    /**
     * Metodo che setta le due label.
     *
     * @param totAndMatching la coppia che rappresenta il numero totale di file trovati
     *                       e il numero totale dei file con almeno un matching.
     * @param totMatches il numero totale di parole che corrispondono alla REGEXP.
     */
    private void setLabels(Pair<Integer, Integer> totAndMatching, int totMatches) {
        System.out.println(DEBUG_SETLABELS + totAndMatching.getKey() + DEBUG_SETLABELS_POST + totAndMatching.getValue());
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
                this.filesListView.getItems().clear();
                this.totMatches = 0;
                this.setListView();
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
     * @param wordCounter il WordCounter.
     * @param depth  la Max depth.
     */
    private void callTasks(final Folder folder, final WordCounter wordCounter, final int depth) {
        new Thread(() -> {
            this.setEvents(wordCounter);
            final long startTime = System.currentTimeMillis();
            wordCounter.countOccurrencesInParallel(folder, Pattern.compile(REGEXP_TO_MATCH), depth);
            final long stopTime = System.currentTimeMillis();

            System.out.println("Fork / join search took " + (stopTime - startTime) + "ms. Slept "
                    + WordCounter.SLEEP_DEBUG + " ms for each document found");
        }).start();
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

        wordCounter.countOccurrencesInEventLoop(folder, Pattern.compile(REGEXP_TO_MATCH),
                depth,
                map -> map.forEach((k, v) -> {

                    synchronized (filesListView.getItems()) {
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
                    synchronized (filesListView.getItems()) {

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

        new Thread(() -> {
            final long startTime = System.currentTimeMillis();
            wordCounter.countOccurrencesReactively(folder, Pattern.compile(REGEXP_TO_MATCH), depth, observer );
            final long stopTime = System.currentTimeMillis();
            System.out.println("Reactive search took " + (stopTime - startTime) + "ms. Slept "
                + WordCounter.SLEEP_DEBUG + " ms for each document found");
        }).start();
    }


}
