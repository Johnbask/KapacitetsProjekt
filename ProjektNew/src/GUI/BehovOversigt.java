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
import javafx.geometry.Insets;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BehovOversigt extends BorderPane {

    private List<Projekt> projekter;

    private BarChart<String, Number> barChart;
    private ListView<Projekt> lvwProjekter;
    private Button btnOpret;

    public BehovOversigt() {
        initUI();
    }

    private void initUI() {

        lvwProjekter = new ListView<>();
        lvwProjekter.setPrefWidth(250);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Ressource behov pr. måned");

        setLeft(lvwProjekter);
        setCenter(barChart);

        btnOpret = new Button("Opret behov");

        GridPane bottom = new GridPane();
        bottom.setHgap(10);
        bottom.setPadding(new Insets(10));
        bottom.add(btnOpret, 0, 0);

        setBottom(bottom);

        lvwProjekter.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        buildChart(newVal.getRessourceBehov());
                    }
                }
        );

        btnOpret.setOnAction(e -> openCreateWindow());
    }

    public void setProjekter(List<Projekt> projekter) {
        this.projekter = projekter;
        lvwProjekter.getItems().setAll(projekter);
    }

    // =========================
    // FIXED CHART (START/SLUT)
    // =========================
    private void buildChart(List<RessourceBehov> behovListe) {

        barChart.getData().clear();

        if (behovListe == null || behovListe.isEmpty()) return;

        Map<YearMonth, Double> counts = new TreeMap<>();

        for (RessourceBehov b : behovListe) {

            YearMonth current = b.getStartPeriode();

            while (!current.isAfter(b.getSlutPeriode())) {

                counts.put(
                        current,
                        counts.getOrDefault(current, 0.0) + b.getAndel()
                );

                current = current.plusMonths(1);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Behov");

        for (Map.Entry<YearMonth, Double> entry : counts.entrySet()) {

            String label = entry.getKey().getMonth().name().substring(0, 3)
                    + " " + entry.getKey().getYear();

            series.getData().add(
                    new XYChart.Data<>(label, entry.getValue())
            );
        }

        barChart.getData().add(series);
    }

    // =========================
    // CREATE WINDOW (FIXED)
    // =========================
    private void openCreateWindow() {

        Stage stage = new Stage();
        stage.setTitle("Opret behov");

        GridPane pane = new GridPane();
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20));

        TextField tfRolle = new TextField();
        TextField tfStartÅr = new TextField();
        TextField tfStartMåned = new TextField();
        TextField tfSlutÅr = new TextField();
        TextField tfSlutMåned = new TextField();
        TextField tfAntal = new TextField();

        pane.add(new Label("Rolle:"), 0, 0);
        pane.add(tfRolle, 1, 0);

        pane.add(new Label("Start år:"), 0, 1);
        pane.add(tfStartÅr, 1, 1);

        pane.add(new Label("Start måned:"), 0, 2);
        pane.add(tfStartMåned, 1, 2);

        pane.add(new Label("Slut år:"), 0, 3);
        pane.add(tfSlutÅr, 1, 3);

        pane.add(new Label("Slut måned:"), 0, 4);
        pane.add(tfSlutMåned, 1, 4);

        pane.add(new Label("Antal:"), 0, 5);
        pane.add(tfAntal, 1, 5);

        Button btnGem = new Button("Gem");
        pane.add(btnGem, 1, 6);

        btnGem.setOnAction(e -> {

            Projekt selected = lvwProjekter.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            RessourceBehov ny = new RessourceBehov(
                    999,
                    tfRolle.getText(),
                    YearMonth.of(
                            Integer.parseInt(tfStartÅr.getText()),
                            Integer.parseInt(tfStartMåned.getText())
                    ),
                    YearMonth.of(
                            Integer.parseInt(tfSlutÅr.getText()),
                            Integer.parseInt(tfSlutMåned.getText())
                    ),
                    Double.parseDouble(tfAntal.getText()),
                    1000,
                    ØkonomiType.CAPEX
            );

            selected.getRessourceBehov().add(ny);

            buildChart(selected.getRessourceBehov());

            stage.close();
        });

        stage.setScene(new Scene(pane, 350, 320));
        stage.show();
    }
}