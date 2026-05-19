package GUI;

import Model.Projekt;
import Model.RessourceBehov;
import Model.Enum.ØkonomiType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BehovOversigt extends BorderPane {

    private List<Projekt> projekter;

    private ListView<Projekt> lvwProjekter;
    private ListView<RessourceBehov> lvwBehov;

    private BarChart<String, Number> barChart;

    private Button btnOpret;
    private Button btnRediger;
    private Button btnSlet;

    // Callback der kaldes når behov oprettes, redigeres eller slettes
    private Runnable onBehovChanged;

    public BehovOversigt() {
        initUI();
    }

    // =====================================================
    // CALLBACK REGISTRERING
    // =====================================================
    public void setOnBehovChanged(Runnable callback) {
        this.onBehovChanged = callback;
    }

    // Intern hjælpemetode — kalder callback hvis den er sat
    private void fireBehovChanged() {
        if (onBehovChanged != null) {
            onBehovChanged.run();
        }
    }

    private void initUI() {

        // =========================
        // LISTVIEWS
        // =========================
        lvwProjekter = new ListView<>();
        lvwProjekter.setPrefWidth(200);

        // Vis projektnavnet i listen
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
        btnOpret  = new Button("Opret");
        btnRediger = new Button("Rediger");
        btnSlet   = new Button("Slet");

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
                    buildChart(newVal.getRessourceBehov());
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

        // Hvis et projekt allerede er valgt, behold det valgte
        Projekt valgt = lvwProjekter.getSelectionModel().getSelectedItem();
        if (valgt != null) {
            lvwBehov.getItems().setAll(valgt.getRessourceBehov());
            buildChart(valgt.getRessourceBehov());
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

        TextField tfRolle  = new TextField();
        TextField tfStart  = new TextField();
        TextField tfSlut   = new TextField();
        TextField tfAndel  = new TextField();

        pane.add(new Label("Rolle:"),            0, 0); pane.add(tfRolle,  1, 0);
        pane.add(new Label("Start (YYYY-MM):"),  0, 1); pane.add(tfStart,  1, 1);
        pane.add(new Label("Slut (YYYY-MM):"),   0, 2); pane.add(tfSlut,   1, 2);
        pane.add(new Label("Andel:"),            0, 3); pane.add(tfAndel,  1, 3);

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

                // Opdater listview
                lvwBehov.getItems().setAll(selected.getRessourceBehov());
                buildChart(selected.getRessourceBehov());

                // Fortæl ProjektOversigt at den skal opdatere sin tidslinje
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

        TextField tfRolle  = new TextField(selected.getRolle());
        TextField tfStart  = new TextField(selected.getStartPeriode().toString());
        TextField tfSlut   = new TextField(selected.getSlutPeriode().toString());
        TextField tfAndel  = new TextField(String.valueOf(selected.getAndel()));

        pane.add(new Label("Rolle:"),  0, 0); pane.add(tfRolle,  1, 0);
        pane.add(new Label("Start:"),  0, 1); pane.add(tfStart,  1, 1);
        pane.add(new Label("Slut:"),   0, 2); pane.add(tfSlut,   1, 2);
        pane.add(new Label("Andel:"),  0, 3); pane.add(tfAndel,  1, 3);

        Button btnGem = new Button("Gem");
        pane.add(btnGem, 1, 4);

        btnGem.setOnAction(e -> {
            try {
                selected.setRolle(tfRolle.getText());
                selected.setStartPeriode(YearMonth.parse(tfStart.getText()));
                selected.setSlutPeriode(YearMonth.parse(tfSlut.getText()));
                selected.setAndel(Double.parseDouble(tfAndel.getText()));

                lvwBehov.refresh();
                buildChart(selectedProjekt.getRessourceBehov());

                // Fortæl ProjektOversigt at den skal opdatere sin tidslinje
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
        buildChart(selectedProjekt.getRessourceBehov());

        fireBehovChanged();
    }

    // =========================
    // CHART
    // =========================
    private void buildChart(List<RessourceBehov> behovListe) {

        barChart.getData().clear();

        if (behovListe == null || behovListe.isEmpty()) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Behov");

        // Saml alle (YearMonth, andel) par så vi kan sortere dem
        List<Map.Entry<YearMonth, Double>> punkter = new ArrayList<>();

        for (RessourceBehov b : behovListe) {
            YearMonth current = b.getStartPeriode();
            YearMonth slut    = b.getSlutPeriode();

            while (!current.isAfter(slut)) {
                punkter.add(Map.entry(current, Math.ceil(b.getAndel())));
                current = current.plusMonths(1);
            }
        }


        punkter.sort(Map.Entry.comparingByKey());

        for (Map.Entry<YearMonth, Double> punkt : punkter) {
            String label = punkt.getKey().getMonth().name().substring(0, 3)
                    + " " + punkt.getKey().getYear();
            series.getData().add(new XYChart.Data<>(label, punkt.getValue()));
        }

        barChart.getData().add(series);
    }

    private void showAlert(String besked) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel");
        alert.setHeaderText(null);
        alert.setContentText(besked);
        alert.showAndWait();
    }
}