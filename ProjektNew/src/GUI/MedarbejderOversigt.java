package GUI;

import Controller.Controller;
import Model.*;
import Model.Enum.MedarbejderType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

public class MedarbejderOversigt extends GridPane {

    private final Controller controller = Controller.getInstance();

    private TableView<Medarbejder> tvwMedarbejdere;
    private GridPane timelineGrid;
    private ScrollPane scrollPane;

    private List<Allokering> allokeringer;

    private Button btnOpret;
    private Button btnRediger;

    public MedarbejderOversigt() {
        initContent();
        initActions();
        loadMedarbejdere();
    }

    private void initContent() {

        this.setPadding(new Insets(10));
        this.setHgap(20);
        this.setVgap(10);

        // ==========================================
        // TABLEVIEW
        // ==========================================
       tvwMedarbejdere = new TableView<>();
       tvwMedarbejdere.setPrefSize(944.8, 400);

       // resizer inhold til at passe med vindue størrelse
        tvwMedarbejdere.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                tvwMedarbejdere.prefWidthProperty().bind(newScene.widthProperty());
                tvwMedarbejdere.prefHeightProperty().bind(newScene.heightProperty().subtract(80));
            }
        });


       TableColumn<Medarbejder, Integer> colMedId = new TableColumn<>("Medarbejder ID");
       colMedId.setCellValueFactory(
               new PropertyValueFactory<>("medId")
       );
       colMedId.setPrefWidth(80);

       TableColumn<Medarbejder, String> colInit = new TableColumn<>("Initialer");
       colInit.setCellValueFactory(new PropertyValueFactory<>("initialer"));
       colInit.setPrefWidth(80);

       TableColumn<Medarbejder, String> colNavn = new TableColumn<>("Navn");
       colNavn.setCellValueFactory(new PropertyValueFactory<>("navn"));
       colNavn.setPrefWidth(150);

       TableColumn<Medarbejder, MedarbejderType> colType = new TableColumn<>("Type");
       colType.setCellValueFactory(new PropertyValueFactory<>("type"));
       colType.setPrefWidth(100);

       TableColumn<Medarbejder, String> colStilling = new TableColumn<>("Stilling");
       colStilling.setCellValueFactory(new PropertyValueFactory<>("stilling"));
       colStilling.setPrefWidth(120);

       TableColumn<Medarbejder, Boolean> colFratrådt = new TableColumn<>("Fratrådt");
       colFratrådt.setCellValueFactory(new PropertyValueFactory<>("fratrådt"));
       colFratrådt.setPrefWidth(70);

       TableColumn<Medarbejder, Afdeling> colAfdeling = new TableColumn<>("Afdeling");
       colAfdeling.setCellValueFactory(new PropertyValueFactory<>("afdeling"));
       colAfdeling.setPrefWidth(120);

       TableColumn<Medarbejder, Organisation> colOrganisation = new TableColumn<>("Organisation");
       colOrganisation.setCellValueFactory(new PropertyValueFactory<>("organisation"));
       colOrganisation.setPrefWidth(120);

       TableColumn<Medarbejder, Team> colTeam = new TableColumn<>("Team");
       colTeam.setCellValueFactory(new PropertyValueFactory<>("team"));
       colTeam.setPrefWidth(100);

       tvwMedarbejdere.getColumns().addAll(
               colMedId, colInit, colNavn, colType, colStilling, colFratrådt, colAfdeling, colOrganisation, colTeam
       );

       this.add(tvwMedarbejdere, 0, 0);

        // ==========================================
        // BUTTONS (BOTTOM LEFT)
        // ==========================================
        HBox buttonBox = new HBox(10);

        btnOpret = new Button("Opret Medarbejder");
        btnRediger = new Button("Rediger Medarbejder");

        buttonBox.getChildren().addAll(btnOpret, btnRediger);

        this.add(buttonBox, 0, 1);

        tvwMedarbejdere.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showTimelineWindow(newValue);
            }
        });
    }

    private void initActions() {

        btnOpret.setOnAction(e -> createMedarbejderWindow());

        btnRediger.setOnAction(e -> {
            Medarbejder selected =
                    tvwMedarbejdere.getSelectionModel().getSelectedItem();

            if (selected != null) {
                editMedarbejderWindow(selected);
            }
        });
    }

    // ==========================================
    // DATA INPUT
    // ==========================================
    public void setMedarbejdere(List<Medarbejder> medarbejdere) {
        tvwMedarbejdere.getItems().setAll(medarbejdere);
    }

    public void setAllokeringer(List<Allokering> allokeringer) {
        this.allokeringer = allokeringer;
    }

    public void loadMedarbejdere() {
        try {
            List<Medarbejder> medarbejdere = controller.getAlleMedarbejdere();
            tvwMedarbejdere.getItems().setAll(medarbejdere);
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af medarbejdere:\n" + e.getMessage());
        }
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
                .filter(p -> p != null)
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

                    if (a.getProjekt() != null && a.getProjekt() == projekt &&
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

    private void showTimelineWindow(Medarbejder medarbejder) {
        Stage stage = new Stage();
        stage.setTitle("Timeline - " + medarbejder.getNavn());

        timelineGrid = new GridPane();
        timelineGrid.setHgap(2);
        timelineGrid.setVgap(2);

        scrollPane = new ScrollPane(timelineGrid);
        scrollPane.setPrefWidth(850);
        scrollPane.setPrefHeight(500);

        buildTimeline(medarbejder);

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.show();
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
        TextField txfAfdId = new TextField();
        TextField txfOrgId = new TextField();
        TextField txfTeamId = new TextField();
        ComboBox<MedarbejderType> cmbType = new ComboBox<>();
        cmbType.getItems().addAll(MedarbejderType.values());

        pane.add(new Label("Initialer:"), 0, 0);
        pane.add(txfInitialer, 1, 0);

        pane.add(new Label("Navn:"), 0, 1);
        pane.add(txfNavn, 1, 1);

        pane.add(new Label("Stilling:"), 0, 2);
        pane.add(txfStilling, 1, 2);

        pane.add(new Label("Type:"), 0, 3);
        pane.add(cmbType, 1, 3);

        pane.add(new Label("Afdeling ID:"), 0, 4);
        pane.add(txfAfdId, 1, 4);

        pane.add(new Label("Org. ID:"), 0, 5);
        pane.add(txfOrgId, 1, 5);

        pane.add(new Label("Team ID:"), 0, 6);
        pane.add(txfTeamId, 1, 6);

        Button btnGem = new Button("Gem");
        Button btnCancel = new Button("Luk");

        pane.add(btnGem, 0, 7);
        pane.add(btnCancel, 1, 7);

        btnGem.setOnAction(e -> {
                Medarbejder ny = null;
                try {
                    ny = controller.createMedarbejder(
                            0,
                            txfInitialer.getText(),
                            txfNavn.getText(),
                            cmbType.getValue(),
                            txfStilling.getText(),
                            false,
                            Integer.parseInt(txfAfdId.getText()),
                            Integer.parseInt(txfOrgId.getText()),
                            Integer.parseInt(txfTeamId.getText())
                    );

                    if (ny != null) {
                        tvwMedarbejdere.getItems().add(ny);
                        stage.close();
                    } else {
                        showAlert("Afdeling, Organisation eller Team blev ikke fundet");
                    }
                } catch (SQLException ex) {
                    showAlert("Fejl ved oprettelse af medarbejder: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    showAlert("Afdeling, Org. og Team skal være tal.");
                }
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

        TextField txfInitialer = new TextField(medarbejder.getInitialer());
        TextField txfNavn = new TextField(medarbejder.getNavn());
        TextField txfStilling = new TextField(medarbejder.getStilling());
        TextField txfAfdId = new TextField(medarbejder.getAfdeling() != null
                ? String.valueOf(medarbejder.getAfdeling().getAfdId()) : "");
        TextField txfOrgId = new TextField(medarbejder.getOrganisation() != null
                ? String.valueOf(medarbejder.getOrganisation().getOrgId()) : "");
        TextField txfTeamId = new TextField(medarbejder.getTeam() != null
                ? String.valueOf(medarbejder.getTeam().getTeamId()) : "");

        ComboBox<MedarbejderType> cmbType = new ComboBox<>();
        cmbType.getItems().addAll(MedarbejderType.values());
        cmbType.setValue(medarbejder.getType());

        CheckBox chkFratrådt = new CheckBox();
        chkFratrådt.setSelected(medarbejder.isFratrådt());

        pane.add(new Label("Initialer:"), 0, 0);
        pane.add(txfInitialer, 1, 0);

        pane.add(new Label("Navn:"), 0, 1);
        pane.add(txfNavn, 1, 1);

        pane.add(new Label("Stilling:"), 0, 2);
        pane.add(txfStilling, 1, 2);

        pane.add(new Label("Type:"), 0, 3);
        pane.add(cmbType, 1, 3);

        pane.add(new Label("Fratrådt"), 0, 4);
        pane.add(chkFratrådt, 1, 4);

        pane.add(new Label("Afdeling ID:"), 0, 5);
        pane.add(txfAfdId, 1, 5);

        pane.add(new Label("Org. ID:"), 0, 6);
        pane.add(txfOrgId, 1, 6);

        pane.add(new Label("Team ID:"), 0, 7);
        pane.add(txfTeamId, 1, 7);

        Button btnGem = new Button("Gem");
        Button btnSlet = new Button("Slet");
        Button btnCancel = new Button("Luk");

        HBox btnBox = new HBox(10, btnGem, btnSlet, btnCancel);
        pane.add(btnBox, 0, 8, 2, 1);

        btnGem.setOnAction(e -> {
            try {
                Medarbejder opdateret = controller.updateMedarbejder(
                        medarbejder.getMedId(),
                        txfInitialer.getText(),
                        txfNavn.getText(),
                        cmbType.getValue(),
                        txfStilling.getText(),
                        chkFratrådt.isSelected(),
                        Integer.parseInt(txfAfdId.getText()),
                        Integer.parseInt(txfOrgId.getText()),
                        Integer.parseInt(txfTeamId.getText())
                );

                tvwMedarbejdere.getItems().set(tvwMedarbejdere.getItems().indexOf(medarbejder), opdateret);

                stage.close();
            } catch (SQLException ex) {
                showAlert("Fejl ved opdatering: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showAlert("Afdeling, Org. og Team skal være tal.");
            }
        });

        btnSlet.setOnAction(e -> {
            try {
                controller.deleteMedarbejder(medarbejder.getMedId());
                tvwMedarbejdere.getItems().remove(medarbejder);
                stage.close();
            } catch (SQLException ex) {
                showAlert("Fejl ved sletning: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 350, 380));
        stage.showAndWait();
    }

    private void showAlert(String besked) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel");
        alert.setHeaderText(null);
        alert.setContentText(besked);
        alert.showAndWait();
    }
}