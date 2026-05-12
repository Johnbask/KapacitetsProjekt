package GUI;

import Model.Allokering;
import Model.Medarbejder;
import Model.Projekt;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.List;

public class MedarbejderOversigt extends GridPane {

    private ListView<Medarbejder> lvwMedarbejdere;
    private GridPane timelineGrid;
    private ScrollPane scrollPane;

    private List<Allokering> allokeringer;

    private Button btnOpret;
    private Button btnRediger;

    public MedarbejderOversigt() {
        initContent();
        initActions();
    }

    private void initContent() {

        this.setPadding(new Insets(10));
        this.setHgap(20);
        this.setVgap(10);

        // ==========================================
        // LISTVIEW
        // ==========================================
        lvwMedarbejdere = new ListView<>();
        lvwMedarbejdere.setPrefWidth(250);
        lvwMedarbejdere.setPrefHeight(450);

        this.add(lvwMedarbejdere, 0, 0);

        // ==========================================
        // BUTTONS (BOTTOM LEFT)
        // ==========================================
        HBox buttonBox = new HBox(10);

        btnOpret = new Button("Opret Medarbejder");
        btnRediger = new Button("Rediger Medarbejder");

        buttonBox.getChildren().addAll(btnOpret, btnRediger);

        this.add(buttonBox, 0, 1);

        // ==========================================
        // TIMELINE
        // ==========================================
        timelineGrid = new GridPane();
        timelineGrid.setHgap(2);
        timelineGrid.setVgap(2);

        scrollPane = new ScrollPane(timelineGrid);
        scrollPane.setPrefWidth(850);
        scrollPane.setPrefHeight(500);

        // span over begge rows
        this.add(scrollPane, 1, 0, 1, 2);

        // ==========================================
        // CLICK HANDLER
        // ==========================================
        lvwMedarbejdere.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && allokeringer != null) {
                        buildTimeline(newVal);
                    }
                });
    }

    private void initActions() {

        btnOpret.setOnAction(e -> createMedarbejderWindow());

        btnRediger.setOnAction(e -> {
            Medarbejder selected =
                    lvwMedarbejdere.getSelectionModel().getSelectedItem();

            if (selected != null) {
                editMedarbejderWindow(selected);
            }
        });
    }

    // ==========================================
    // DATA INPUT
    // ==========================================
    public void setMedarbejdere(List<Medarbejder> medarbejdere) {
        lvwMedarbejdere.getItems().setAll(medarbejdere);
    }

    public void setAllokeringer(List<Allokering> allokeringer) {
        this.allokeringer = allokeringer;
    }

    // ==========================================
    // BUILD TIMELINE
    // ==========================================
    private void buildTimeline(Medarbejder medarbejder) {

        timelineGrid.getChildren().clear();

        if (allokeringer == null || allokeringer.isEmpty()) return;

        List<Allokering> relevant = allokeringer.stream()
                .filter(a -> a.getMedarbejdere().contains(medarbejder))
                .toList();

        if (relevant.isEmpty()) return;

        // ==========================================
        // FIND RANGE
        // ==========================================
        YearMonth min = null;
        YearMonth max = null;

        for (Allokering a : relevant) {
            YearMonth ym = a.getPeriode();

            if (min == null || ym.isBefore(min)) min = ym;
            if (max == null || ym.isAfter(max)) max = ym;
        }

        // ==========================================
        // HEADER
        // ==========================================
        YearMonth current = min;
        int col = 1;

        int lastYear = -1;
        int lastQuarter = -1;

        while (!current.isAfter(max)) {

            int year = current.getYear();
            int month = current.getMonthValue();
            int quarter = ((month - 1) / 3) + 1;

            // YEAR
            if (year != lastYear) {
                Label yearLabel = new Label(String.valueOf(year));
                yearLabel.setMinSize(80, 25);
                yearLabel.setAlignment(Pos.CENTER);
                yearLabel.setStyle(
                        "-fx-font-weight: bold;" +
                                "-fx-border-color: black;" +
                                "-fx-background-color: #dddddd;"
                );
                timelineGrid.add(yearLabel, col, 0);

                lastYear = year;
                lastQuarter = -1;
            }

            // QUARTER
            if (quarter != lastQuarter) {
                Label qLabel = new Label("Q" + quarter);
                qLabel.setMinSize(80, 25);
                qLabel.setAlignment(Pos.CENTER);
                qLabel.setStyle(
                        "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-background-color: #eeeeee;"
                );
                timelineGrid.add(qLabel, col, 1);

                lastQuarter = quarter;
            }

            // MONTH
            Label monthLabel = new Label(
                    current.getMonth().name().substring(0, 3)
            );
            monthLabel.setMinSize(80, 25);
            monthLabel.setAlignment(Pos.CENTER);
            monthLabel.setStyle("-fx-border-color: lightgray;");
            timelineGrid.add(monthLabel, col, 2);

            current = current.plusMonths(1);
            col++;
        }

        // ==========================================
        // PROJECT ROWS
        // ==========================================
        List<Projekt> projekter = relevant.stream()
                .map(Allokering::getProjekt)
                .distinct()
                .toList();

        int row = 3;

        for (Projekt projekt : projekter) {

            Label projektLabel = new Label(projekt.getNavn());
            projektLabel.setMinSize(120, 30);
            projektLabel.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-border-color: black;"
            );

            timelineGrid.add(projektLabel, 0, row);

            current = min;
            col = 1;

            while (!current.isAfter(max)) {

                Label cell = new Label();
                cell.setMinSize(80, 30);
                cell.setAlignment(Pos.CENTER);

                boolean found = false;

                for (Allokering a : relevant) {

                    if (a.getProjekt() == projekt &&
                            a.getPeriode().equals(current)) {

                        cell.setText(String.valueOf(a.getAndel()));
                        cell.setStyle(
                                "-fx-background-color: #dee2e6;" +
                                        "-fx-border-color: black;"
                        );

                        found = true;
                        break;
                    }
                }

                if (!found) {
                    cell.setStyle("-fx-border-color: lightgray;");
                }

                timelineGrid.add(cell, col, row);

                current = current.plusMonths(1);
                col++;
            }

            row++;
        }
    }

    // ==========================================
    // CREATE WINDOW
    // ==========================================
    private void createMedarbejderWindow() {

        Stage stage = new Stage();
        stage.setTitle("Opret Medarbejder");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        TextField txfInitialer = new TextField();
        TextField txfNavn = new TextField();
        TextField txfStilling = new TextField();

        pane.add(new Label("Initialer:"), 0, 0);
        pane.add(txfInitialer, 1, 0);

        pane.add(new Label("Navn:"), 0, 1);
        pane.add(txfNavn, 1, 1);

        pane.add(new Label("Stilling:"), 0, 2);
        pane.add(txfStilling, 1, 2);

        Button btnGem = new Button("Gem");
        Button btnCancel = new Button("Luk");

        pane.add(btnGem, 0, 3);
        pane.add(btnCancel, 1, 3);

        btnGem.setOnAction(e -> {

            Medarbejder ny = new Medarbejder(
                    999,
                    txfInitialer.getText(),
                    txfNavn.getText(),
                    null,
                    txfStilling.getText(),
                    false,
                    null,
                    null,
                    null
            );

            lvwMedarbejdere.getItems().add(ny);
            stage.close();
        });

        btnCancel.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 350, 220));
        stage.showAndWait();
    }

    // ==========================================
    // EDIT WINDOW
    // ==========================================
    private void editMedarbejderWindow(Medarbejder medarbejder) {

        Stage stage = new Stage();
        stage.setTitle("Rediger Medarbejder");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        TextField txfInitialer =
                new TextField(medarbejder.getInitialer());
        TextField txfNavn =
                new TextField(medarbejder.getNavn());
        TextField txfStilling =
                new TextField(medarbejder.getStilling());

        pane.add(new Label("Initialer:"), 0, 0);
        pane.add(txfInitialer, 1, 0);

        pane.add(new Label("Navn:"), 0, 1);
        pane.add(txfNavn, 1, 1);

        pane.add(new Label("Stilling:"), 0, 2);
        pane.add(txfStilling, 1, 2);

        Button btnGem = new Button("Gem");
        Button btnCancel = new Button("Luk");

        pane.add(btnGem, 0, 3);
        pane.add(btnCancel, 1, 3);

        btnGem.setOnAction(e -> {
            medarbejder.setInitialer(txfInitialer.getText());
            medarbejder.setNavn(txfNavn.getText());
            medarbejder.setStilling(txfStilling.getText());

            lvwMedarbejdere.refresh();
            stage.close();
        });

        btnCancel.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 350, 220));
        stage.showAndWait();
    }
}