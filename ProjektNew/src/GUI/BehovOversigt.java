package GUI;

import Model.Projekt;
import Model.RessourceBehov;
import Model.Enum.ØkonomiType;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BehovOversigt extends BorderPane {

    private List<RessourceBehov> behovListe;
    private List<Projekt> projekter;

    private BarChart<String, Number> barChart;
    private ListView<Projekt> lvwProjekter;

    private Button btnOpret;

    public BehovOversigt() {
        initUI();
    }

    private void initUI() {

        // =========================
        // LISTVIEW (VENSTRE)
        // =========================
        lvwProjekter = new ListView<>();
        lvwProjekter.setPrefWidth(250);

        // =========================
        // CHART
        // =========================
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Ressource behov pr. måned");

        setLeft(lvwProjekter);
        setCenter(barChart);

        // =========================
        // BOTTOM LEFT BUTTON
        // =========================
        btnOpret = new Button("Opret behov");

        GridPane bottom = new GridPane();
        bottom.setHgap(10);
        bottom.add(btnOpret, 0, 0);

        setBottom(bottom);

        // =========================
        // EVENTS
        // =========================
        lvwProjekter.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        buildChart(newVal.getRessourceBehov());
                    }
                }
        );

        btnOpret.setOnAction(e -> openCreateWindow());
    }

    // =========================
    // DATA
    // =========================
    public void setProjekter(List<Projekt> projekter) {
        this.projekter = projekter;
        lvwProjekter.getItems().setAll(projekter);
    }

    // =========================
    // CHART LOGIC (UNCHANGED)
    // =========================
    private void buildChart(List<RessourceBehov> behovListe) {

        barChart.getData().clear();

        if (behovListe == null || behovListe.isEmpty()) return;

        Map<YearMonth, Integer> counts = new TreeMap<>();

        for (RessourceBehov b : behovListe) {
            counts.put(
                    b.getPeriode(),
                    counts.getOrDefault(b.getPeriode(), 0) + (int) Math.ceil(b.getAndel())
            );
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bevhov");

        for (Map.Entry<YearMonth, Integer> entry : counts.entrySet()) {

            String label = entry.getKey().getMonth().name().substring(0, 3)
                    + " " + entry.getKey().getYear();

            series.getData().add(
                    new XYChart.Data<>(label, entry.getValue())
            );
        }

        barChart.getData().add(series);
    }

    // =========================
    // CREATE WINDOW
    // =========================
    private void openCreateWindow() {

        Stage stage = new Stage();
        stage.setTitle("Opret behov");

        GridPane pane = new GridPane();
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new javafx.geometry.Insets(20));

        TextField tfRolle = new TextField();
        TextField tfÅr = new TextField();
        TextField tfMåned = new TextField();
        TextField tfAntal = new TextField();

        pane.add(new Label("Rolle:"), 0, 0);
        pane.add(tfRolle, 1, 0);

        pane.add(new Label("År:"), 0, 1);
        pane.add(tfÅr, 1, 1);

        pane.add(new Label("Måned:"), 0, 2);
        pane.add(tfMåned, 1, 2);

        pane.add(new Label("Antal:"), 0, 3);
        pane.add(tfAntal, 1, 3);

        Button btnGem = new Button("Gem");

        pane.add(btnGem, 1, 4);

        btnGem.setOnAction(e -> {

            RessourceBehov ny = new RessourceBehov(
                    999,
                    tfRolle.getText(),
                    YearMonth.of(
                            Integer.parseInt(tfÅr.getText()),
                            Integer.parseInt(tfMåned.getText())
                    ),
                    Double.parseDouble(tfAntal.getText()),
                    1000,
                    ØkonomiType.CAPEX
            );

            Projekt selected = lvwProjekter.getSelectionModel().getSelectedItem();

            if (selected != null) {
                selected.getRessourceBehov().add(ny);
                buildChart(selected.getRessourceBehov());
            }

            stage.close();
        });

        stage.setScene(new Scene(pane, 300, 250));
        stage.show();
    }
}