package GUI;

import Controller.Controller;
import Model.Allokering;
import Model.Projekt;
import Model.RessourceBehov;
import Model.Enum.ØkonomiType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.*;

public class BehovOversigt extends BorderPane {

    private final Controller controller = Controller.getInstance();

    private List<Projekt> projekter;
    private List<Allokering> allokeringer = new ArrayList<>();

    private ListView<Projekt> lvwProjekter;
    private ListView<RessourceBehov> lvwBehov;

    private BarChart<String, Number> barChart;

    private Button btnOpret;
    private Button btnRediger;
    private Button btnSlet;

    private Runnable onBehovChanged;

    public BehovOversigt() {
        initUI();
        loadAllokeringer();
    }

    // =====================================================
    // CALLBACK
    // =====================================================
    public void setOnBehovChanged(Runnable callback) {
        this.onBehovChanged = callback;
    }

    private void fireBehovChanged() {
        if (onBehovChanged != null) onBehovChanged.run();
    }

    // =====================================================
    // LOAD ALLOKERINGER FRA DB
    // =====================================================
    private void loadAllokeringer() {
        try {
            allokeringer = controller.getAlleAllokeringer();
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af allokeringer:\n" + e.getMessage());
        }
    }

    private void initUI() {

        // =========================
        // LISTVIEWS
        // =========================
        lvwProjekter = new ListView<>();
        lvwProjekter.setPrefWidth(200);

        lvwProjekter.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Projekt p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getNavn());
            }
        });

        lvwBehov = new ListView<>();
        lvwBehov.setPrefWidth(220);

        // =========================
        // CHART
        // =========================
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Ressource behov pr. måned");
        barChart.setAnimated(false);
        barChart.setPrefWidth(600);
        barChart.setPrefHeight(450);

        lvwProjekter.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                lvwProjekter.prefHeightProperty().bind(newScene.heightProperty().subtract(80));
            }
        });

        lvwBehov.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                lvwBehov.prefHeightProperty().bind(newScene.heightProperty().subtract(120));
            }
        });

        barChart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                barChart.prefWidthProperty().bind(newScene.widthProperty().subtract(450));
                barChart.prefHeightProperty().bind(newScene.heightProperty().subtract(80));
            }
        });

        // =========================
        // BUTTONS
        // =========================
        btnOpret   = new Button("Opret");
        btnRediger = new Button("Rediger");
        btnSlet    = new Button("Slet");

        HBox buttonBox = new HBox(6, btnOpret, btnRediger, btnSlet);

        VBox left   = new VBox(6, new Label("Projekter"), lvwProjekter);
        VBox middle = new VBox(6, new Label("Behov"), lvwBehov, buttonBox);
        VBox right  = new VBox(6, barChart);

        left.setPadding(new Insets(5));
        middle.setPadding(new Insets(5));
        right.setPadding(new Insets(5));

        HBox root = new HBox(10, left, middle, right);
        HBox.setHgrow(barChart, Priority.ALWAYS);

        setCenter(root);

        // =========================
        // EVENTS
        // =========================
        lvwProjekter.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal == null) return;
                    lvwBehov.getItems().setAll(newVal.getRessourceBehov());
                    buildChart(newVal.getRessourceBehov(), newVal);
                }
        );

        btnOpret.setOnAction(e -> openCreateWindow());
        btnRediger.setOnAction(e -> openEditWindow());
        btnSlet.setOnAction(e -> deleteBehoev());
    }

    // =========================
    // SET DATA
    // =========================
    public void setProjekter(List<Projekt> projekter) {
        this.projekter = projekter;
        lvwProjekter.getItems().setAll(projekter);

        Projekt valgt = lvwProjekter.getSelectionModel().getSelectedItem();
        if (valgt != null) {
            lvwBehov.getItems().setAll(valgt.getRessourceBehov());
            buildChart(valgt.getRessourceBehov(), valgt);
        }
    }

    // =========================
    // CREATE
    // =========================
    private void openCreateWindow() {

        Stage stage = new Stage();
        stage.setTitle("Opret behov");

        GridPane pane = new GridPane();
        pane.setVgap(8);
        pane.setHgap(8);
        pane.setPadding(new Insets(10));

        TextField tfRolle = new TextField();
        TextField tfStart = new TextField();
        TextField tfSlut  = new TextField();
        TextField tfAndel = new TextField();

        pane.add(new Label("Rolle:"),           0, 0); pane.add(tfRolle, 1, 0);
        pane.add(new Label("Start (YYYY-MM):"), 0, 1); pane.add(tfStart, 1, 1);
        pane.add(new Label("Slut (YYYY-MM):"),  0, 2); pane.add(tfSlut,  1, 2);
        pane.add(new Label("Andel:"),           0, 3); pane.add(tfAndel, 1, 3);

        Button btnGem = new Button("Gem");
        pane.add(btnGem, 1, 4);

        btnGem.setOnAction(e -> {
            Projekt selected = lvwProjekter.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Vælg et projekt først.");
                return;
            }

            try {
                RessourceBehov rb = new RessourceBehov(
                        999,
                        tfRolle.getText(),
                        YearMonth.parse(tfStart.getText()),
                        YearMonth.parse(tfSlut.getText()),
                        Double.parseDouble(tfAndel.getText()),
                        1000,
                        ØkonomiType.CAPEX
                );

                selected.getRessourceBehov().add(rb);
                lvwBehov.getItems().setAll(selected.getRessourceBehov());
                buildChart(selected.getRessourceBehov(), selected);
                fireBehovChanged();
                stage.close();

            } catch (Exception ex) {
                showAlert("Ugyldig input: " + ex.getMessage());
            }
        });

        stage.setScene(new Scene(pane, 320, 260));
        stage.show();
    }

    // =========================
    // EDIT
    // =========================
    private void openEditWindow() {

        Projekt selectedProjekt = lvwProjekter.getSelectionModel().getSelectedItem();
        RessourceBehov selected = lvwBehov.getSelectionModel().getSelectedItem();

        if (selectedProjekt == null || selected == null) return;

        Stage stage = new Stage();
        stage.setTitle("Rediger behov");

        GridPane pane = new GridPane();
        pane.setVgap(8);
        pane.setHgap(8);
        pane.setPadding(new Insets(10));

        TextField tfRolle = new TextField(selected.getRolle());
        TextField tfStart = new TextField(selected.getStartPeriode().toString());
        TextField tfSlut  = new TextField(selected.getSlutPeriode().toString());
        TextField tfAndel = new TextField(String.valueOf(selected.getAndel()));

        pane.add(new Label("Rolle:"), 0, 0); pane.add(tfRolle, 1, 0);
        pane.add(new Label("Start:"), 0, 1); pane.add(tfStart, 1, 1);
        pane.add(new Label("Slut:"),  0, 2); pane.add(tfSlut,  1, 2);
        pane.add(new Label("Andel:"), 0, 3); pane.add(tfAndel, 1, 3);

        Button btnGem = new Button("Gem");
        pane.add(btnGem, 1, 4);

        btnGem.setOnAction(e -> {
            try {
                selected.setRolle(tfRolle.getText());
                selected.setStartPeriode(YearMonth.parse(tfStart.getText()));
                selected.setSlutPeriode(YearMonth.parse(tfSlut.getText()));
                selected.setAndel(Double.parseDouble(tfAndel.getText()));

                lvwBehov.refresh();
                buildChart(selectedProjekt.getRessourceBehov(), selectedProjekt);
                fireBehovChanged();
                stage.close();

            } catch (Exception ex) {
                showAlert("Ugyldig input: " + ex.getMessage());
            }
        });

        stage.setScene(new Scene(pane, 320, 260));
        stage.show();
    }

    // =========================
    // DELETE
    // =========================
    private void deleteBehoev() {

        Projekt selectedProjekt = lvwProjekter.getSelectionModel().getSelectedItem();
        RessourceBehov selected = lvwBehov.getSelectionModel().getSelectedItem();

        if (selectedProjekt == null || selected == null) return;

        selectedProjekt.getRessourceBehov().remove(selected);
        lvwBehov.getItems().setAll(selectedProjekt.getRessourceBehov());
        buildChart(selectedProjekt.getRessourceBehov(), selectedProjekt);
        fireBehovChanged();
    }

    // =========================
    // CHART
    // =========================
    private void buildChart(List<RessourceBehov> behovListe, Projekt projekt) {

        barChart.getData().clear();

        if (behovListe == null || behovListe.isEmpty()) return;

        // -------------------------------------------------------
        // SERIES 1: Ressourcebehov (blå)
        // -------------------------------------------------------
        XYChart.Series<String, Number> behovSeries = new XYChart.Series<>();
        behovSeries.setName("Behov");

        // -------------------------------------------------------
        // SERIES 2: Allokeret — grøn hvis mødt, rød hvis ikke
        // -------------------------------------------------------
        XYChart.Series<String, Number> allokeretSeries = new XYChart.Series<>();
        allokeretSeries.setName("Allokeret");

        // Saml behov per måned (sorteret)
        List<Map.Entry<YearMonth, Double>> behovPunkter = new ArrayList<>();

        for (RessourceBehov rb : behovListe) {
            YearMonth current = rb.getStartPeriode();
            while (!current.isAfter(rb.getSlutPeriode())) {
                behovPunkter.add(Map.entry(current, rb.getAndel()));
                current = current.plusMonths(1);
            }
        }

        behovPunkter.sort(Map.Entry.comparingByKey());

        // Beregn allokeret andel per måned for dette projekt
        for (Map.Entry<YearMonth, Double> punkt : behovPunkter) {

            YearMonth måned = punkt.getKey();
            double behov    = punkt.getValue();

            String label = måned.getMonth().name().substring(0, 3) + " " + måned.getYear();

            // Behov søjle
            behovSeries.getData().add(new XYChart.Data<>(label, behov));

            // Beregn samlet allokeret andel for denne måned og projekt
            double allokeret = 0;
            for (Allokering a : allokeringer) {
                if (a.getProjekt() != null && a.getProjekt().getProjektId() == projekt.getProjektId()) {
                    if (!måned.isBefore(a.getStartPeriode()) && !måned.isAfter(a.getSlutPeriode())) {
                        allokeret += a.getAndel();
                    }
                }
            }


            XYChart.Data<String, Number> data = new XYChart.Data<>(label, allokeret);
            allokeretSeries.getData().add(data);




            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: #222222;");
                }
            });
        }

        barChart.getData().addAll(behovSeries, allokeretSeries);

        javafx.application.Platform.runLater(() -> {
            java.util.List<javafx.scene.Node> symbols = new java.util.ArrayList<>(
                    barChart.lookupAll(".chart-legend-item-symbol")
            );
            // Kun den sidste (Allokeret) farves sort
            if (symbols.size() >= 2) {
                symbols.get(symbols.size() - 1).setStyle("-fx-background-color: #222222;");
            }
        });
    }

    private void showAlert(String besked) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel");
        alert.setHeaderText(null);
        alert.setContentText(besked);
        alert.showAndWait();
    }
}
