package GUI;

import Controller.Controller;
import Model.*;
import Model.Enum.MedarbejderType;
import Model.Enum.MeldingType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedarbejderOversigt extends GridPane {

    private final Controller controller = Controller.getInstance();

    private TableView<Medarbejder> tvwMedarbejdere;
    private GridPane timelineGrid;
    private ScrollPane scrollPane;

    private List<Allokering> allokeringer;

    private Button btnOpret;
    private Button btnRediger;
    private Button btnOpenAllokering;
    private Button btnUpdate;
    private Button btnDelete;
    private Button btnMeldinger;


    /*
    Search function
     */
    private TextField txfSøg;
    private Button btnSøg;
    private Button btnReset;

    /*
    TODO: Ret allokering
     */


    public MedarbejderOversigt() {
        initContent();
        initActions();
        loadMedarbejdere();
        loadAllokering();
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
       colAfdeling.setCellFactory(col -> new TableCell<>() {
           @Override
           protected void updateItem(Afdeling item, boolean empty) {
               super.updateItem(item, empty);
               if (empty || item == null) {
                   setText(null);
               } else if (item.getNavn() == null || item.getNavn().isEmpty()) {
                   setText(item.getLeder());
               } else {
                   setText(item.getNavn());
               }
           }
       });

       TableColumn<Medarbejder, Organisation> colOrganisation = new TableColumn<>("Organisation");
       colOrganisation.setCellValueFactory(new PropertyValueFactory<>("organisation"));
       colOrganisation.setPrefWidth(120);
       colOrganisation.setCellFactory(col -> new TableCell<>() {
           @Override
           protected void updateItem(Organisation item, boolean empty) {
               super.updateItem(item, empty);
               setText(empty || item == null ? null : item.getNavn());
           }
       });

       TableColumn<Medarbejder, Team> colTeam = new TableColumn<>("Team");
       colTeam.setCellValueFactory(new PropertyValueFactory<>("team"));
       colTeam.setPrefWidth(100);
       colTeam.setCellFactory(col -> new TableCell<>() {
           @Override
           protected void updateItem(Team item, boolean empty) {
               super.updateItem(item, empty);
               setText(empty || item == null ? null : item.getNavn());
           }
       });

       tvwMedarbejdere.getColumns().addAll(
               colMedId, colInit, colNavn,
               colType, colStilling, colFratrådt,
               colAfdeling, colOrganisation, colTeam
       );



        // ==========================================
        // BUTTONS (BOTTOM LEFT)
        // ==========================================
        HBox buttonBox = new HBox(10);

        btnOpret = new Button("Opret Medarbejder");
        btnRediger = new Button("Rediger Medarbejder");
        btnDelete = new Button("Slet medarbejder");
        btnOpenAllokering = new Button("Åben allokering");
        btnUpdate = new Button("Update");
        btnMeldinger = new Button("Meldinger");

        buttonBox.getChildren().addAll(btnOpret, btnRediger, btnDelete, btnOpenAllokering, btnUpdate, btnMeldinger);

        /*
        =============================
        |       Search Function     |
        =============================
         */
        txfSøg = new TextField();
        txfSøg.setPromptText("Skrive et navn eller initialer...");
        txfSøg.setPrefWidth(250);

        btnSøg = new Button("Søg");
        btnReset = new Button("Vis alle");

        HBox searchBox = new HBox(10, new Label("Søg:"), txfSøg, btnSøg, btnReset);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        this.add(searchBox, 0, 0);
        this.add(tvwMedarbejdere, 0, 1);
        this.add(buttonBox, 0, 2);

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

        btnUpdate.setOnAction(e -> loadMedarbejdere());

        btnOpenAllokering.setOnAction(e -> {
            Medarbejder valgtMedarbejder = tvwMedarbejdere.getSelectionModel().getSelectedItem();

            if (valgtMedarbejder != null) {
                showTimelineWindow(valgtMedarbejder);
            } else {
                showAlert("Vælg et medarbejder først.");
            }
        });

        btnDelete.setOnAction(e -> {
            Medarbejder valgtMedarbejder = tvwMedarbejdere.getSelectionModel().getSelectedItem();

            if (valgtMedarbejder != null) {
                try {
                    deleteMedarbejderAction(valgtMedarbejder);
                } catch (SQLException ex) {
                    showAlert("Fejl ved sletning af medarbejder: " + ex.getMessage());
                }
            } else {
                showAlert("Vælg et medarbejder først");
            }
        });

        btnMeldinger.setOnAction(e -> meldingerWindow());

        btnSøg.setOnAction(e -> {
            String søgeord = txfSøg.getText().trim();
            if (søgeord.isEmpty()) {
                showAlert("Skriv et navn at søge på.");
                return;
            }

            try {
                ArrayList<Medarbejder> resultater = controller.søgMedarbejderNavn(søgeord);
                tvwMedarbejdere.getItems().setAll(resultater);
                if (resultater.isEmpty()) {
                    showAlert("Ingen medarbejder fundet med navn: " + søgeord);
                }
            } catch (SQLException ex) {
                showAlert("Fejl ved søgning:\n" + ex.getMessage());
            }
        });

        btnReset.setOnAction(e -> loadMedarbejdere());
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

    public void loadAllokering() {
        try {
            List<Allokering> allokeringer = controller.getAlleAllokeringer();
            this.allokeringer = allokeringer;
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af allokeringer:" + e.getMessage());
        }
    }

    // ==========================================
    // BUILD TIMELINE
    // ==========================================
    private void buildTimeline(Medarbejder medarbejder) {

        timelineGrid.getChildren().clear();

        if (allokeringer == null || allokeringer.isEmpty()) return;

        List<Allokering> relevant = allokeringer.stream()
                .filter(a -> a.getMedarbejdere().stream().anyMatch(m -> m.getMedId() == medarbejder.getMedId())).toList();

        if (relevant.isEmpty()) return;

        // ==========================================
        // FIND RANGE
        // ==========================================
        YearMonth min = null;
        YearMonth max = null;

        for (Allokering a : relevant) {
            YearMonth start = a.getStartPeriode();
            YearMonth slut = a.getSlutPeriode();

            if (start == null || slut == null) continue;

            if (start.isAfter(slut)) {
                System.out.println("Ugyldig allokering (start -> slut): " + a);
                continue;
            }

            if (min == null || start.isBefore(min)) min = start;
            if (max == null || slut.isAfter(max)) max = slut;
        }

        if (min == null || max == null) return;

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
        int row = 3;


        List<Projekt> projekter = relevant.stream()
                .map(Allokering::getProjekt)
                .filter(p -> p != null)
                .collect(Collectors.toMap(
                        Projekt::getProjektId,
                        p -> p,
                        (existing, duplicate) -> existing
                ))
                .values()
                .stream()
                .toList();


        for (Projekt projekt : projekter) {

            Label projektLabel = new Label(projekt.getNavn());
            projektLabel.setMinSize(120, 30);
            projektLabel.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-border-color: black;"
            );

            timelineGrid.add(projektLabel, 0, row);

            List<Allokering> projektAllokeringer = relevant.stream()
                    .filter(a -> a.getProjekt() != null
                    && a.getProjekt().getProjektId() == projekt.getProjektId())
                    .toList();

            current = min;
            col = 1;

            while (!current.isAfter(max)) {

                final YearMonth currentMonth = current;

                Label cell = new Label();
                cell.setMinSize(80, 30);
                cell.setAlignment(Pos.CENTER);

                Optional<Allokering> match = projektAllokeringer.stream()
                        .filter(a -> a.getStartPeriode() != null && a.getSlutPeriode() != null
                        && !currentMonth.isBefore(a.getStartPeriode())
                        && !currentMonth.isAfter(a.getSlutPeriode()))
                        .findFirst();

                if (match.isPresent()) {
                    cell.setText(String.valueOf(match.get().getAndel()));
                    cell.setStyle(
                            "-fx-background-color: #dee2e6;" +
                                    "-fx-border-color: black;"
                    );
                } else {
                    cell.setStyle("-fx-border-color: lightgray;");
                }



        /*
        for (Allokering allokering : relevant) {

            Projekt projekt = allokering.getProjekt();
            if (projekt == null) continue;
            if (allokering.getStartPeriode() == null || allokering.getSlutPeriode() == null) continue;
            if (allokering.getStartPeriode().isAfter(allokering.getSlutPeriode())) continue;

            Label projektLabel = new Label(projekt.getNavn());
            projektLabel.setMinSize(120, 30);
            projektLabel.setStyle("-fx-font-weight: bold;" +
                    "-fx-border-color: black;");
            timelineGrid.add(projektLabel, 0, row);

            current = min;
            col = 1;

            while (!current.isAfter(max)) {
                final YearMonth currentMonth = current;

                Label cell = new Label();
                cell.setMinSize(80, 30);
                cell.setAlignment(Pos.CENTER);

                YearMonth start = allokering.getStartPeriode();
                YearMonth slut = allokering.getSlutPeriode();

                if (!currentMonth.isBefore(start) && !currentMonth.isAfter(slut)) {
                    cell.setText(String.valueOf(allokering.getAndel()));
                    cell.setStyle("-fx-background-color: #dee2e6; -fx-border-color: black;");
                } else {
                    cell.setStyle("-fx-border-color: lightgray;");
                }

         */
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
        TextField txfAfdNavn = new TextField();
        TextField txfOrgNavn = new TextField();
        TextField txfTeamNavn = new TextField();
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

        pane.add(new Label("Afdeling navn/leder"), 0, 4);
        pane.add(txfAfdNavn, 1, 4);

        pane.add(new Label("Org. navn"), 0, 5);
        pane.add(txfOrgNavn, 1, 5);

        pane.add(new Label("Team navn"), 0, 6);
        pane.add(txfTeamNavn, 1, 6);

        Button btnGem = new Button("Gem");
        Button btnCancel = new Button("Luk");

        pane.add(btnGem, 0, 7);
        pane.add(btnCancel, 1, 7);

        btnGem.setOnAction(e -> {
                Medarbejder ny = null;
                try {
                    ny = controller.createMedarbejder(
                            getNextMedId(),
                            txfInitialer.getText(),
                            txfNavn.getText(),
                            cmbType.getValue(),
                            txfStilling.getText(),
                            false,
                            txfAfdNavn.getText(),
                            txfOrgNavn.getText(),
                            txfTeamNavn.getText()
                    );

                    tvwMedarbejdere.getItems().add(ny);
                    stage.close();

                } catch (SQLException ex) {
                    showAlert("Fejl ved oprettelse af medarbejder: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    showAlert("Afdeling, Org. og Team skal være tal.");
                }
        });

        btnCancel.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 350, 350));
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
        TextField txfAfdNavn = new TextField(medarbejder.getAfdeling() != null ?
                (medarbejder.getAfdeling().getNavn() == null || medarbejder.getAfdeling().getNavn().isEmpty()
                 ? medarbejder.getAfdeling().getLeder() : medarbejder.getAfdeling().getNavn()) : "");
        TextField txfOrgNavn = new TextField(medarbejder.getOrganisation() != null ?
                medarbejder.getOrganisation().getNavn() : "");
        TextField txfTeamNavn = new TextField(medarbejder.getTeam() != null ?
                medarbejder.getTeam().getNavn() : "");

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

        pane.add(new Label("Afdeling Navn/leder:"), 0, 5);
        pane.add(txfAfdNavn, 1, 5);

        pane.add(new Label("Org. Navn:"), 0, 6);
        pane.add(txfOrgNavn, 1, 6);

        pane.add(new Label("Team Navn:"), 0, 7);
        pane.add(txfTeamNavn, 1, 7);

        /*
        =====================
        |       Buttons     |
        =====================
         */

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
                        txfAfdNavn.getText(),
                        txfOrgNavn.getText(),
                        txfTeamNavn.getText()
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

    /*
    =============================================
    |                Meldinger                  |
    =============================================
     */

    private void meldingerWindow() {
        Stage stage = new Stage();
        stage.setTitle("Meldinger af medarbejdere");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        /*
        =========================
        |       Search(Top)     |
        =========================
         */

        TextField txfSøg = new TextField();
        txfSøg.setPromptText("Søg på medarbejder navn...");
        txfSøg.setPrefWidth(250);

        Button btnSøg = new Button("Søg");
        Button btnVis = new Button("Vis alle");

        HBox searchBox = new HBox(10, new Label("Søg:"), txfSøg, btnSøg, btnVis);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(0, 0, 10, 0));

        root.setTop(searchBox);

        /*
        =========================
        |       TABLEVIEW       |
        =========================
         */

        TableView<Melding> tvwMelding = new TableView<>();
        tvwMelding.setPrefSize(900, 300);

        /*
        int meldingsId,
        MeldingType type,
        LocalDate startDato,
        LocalDate slutDato,
        String noter,
        Medarbejder medarbejder
         */
        TableColumn<Melding, Integer> colId = new TableColumn<>("Meldings ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("meldingsId"));
        colId.setPrefWidth(80);

        TableColumn<Melding, MeldingType> colType = new TableColumn<>("Meldings type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(100);

        TableColumn<Melding, LocalDate> colStart = new TableColumn<>("Start Dato");
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDato"));
        colStart.setPrefWidth(100);

        TableColumn<Melding, LocalDate> colSlut = new TableColumn<>("Slut Dato");
        colSlut.setCellValueFactory(new PropertyValueFactory<>("slutDato"));
        colSlut.setPrefWidth(100);

        TableColumn<Melding, String> colNoter = new TableColumn<>("Noter");
        colNoter.setCellValueFactory(new PropertyValueFactory<>("noter"));
        colNoter.setPrefWidth(200);

        TableColumn<Melding, Medarbejder> colMedarbejder = new TableColumn<>("Medarbejder");
        colMedarbejder.setCellValueFactory(new PropertyValueFactory<>("medarbejder"));
        colMedarbejder.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Medarbejder item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNavn());
            }
        });
        colMedarbejder.setPrefWidth(150);

        tvwMelding.getColumns().addAll(
          colId, colType, colStart, colSlut, colNoter, colMedarbejder
        );

        try {
            List<Melding> meldinger = controller.getAlleMeldinger();
            tvwMelding.getItems().setAll(meldinger);
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af meldinger:\n" + e.getMessage());
        }

        root.setCenter(tvwMelding);
        /*
        =============================
        |       Opret melding       |
        =============================
         */

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 0, 0, 0));
        pane.setHgap(10);
        pane.setVgap(6);

        ComboBox<MeldingType> cmbType = new ComboBox<>();
        cmbType.getItems().addAll(MeldingType.values());

        TextField txfStartDato = new TextField();
        txfStartDato.setPromptText("YYYY-MM-DD");

        TextField txfSlutDato = new TextField();
        txfSlutDato.setPromptText("YYYY-MM-DD");

        TextArea txaNoter = new TextArea();
        txaNoter.setPrefRowCount(2);

        TextField txfMedarbejderNavn = new TextField();
        txfMedarbejderNavn.setPromptText("Medarbejder navn");

        pane.add(new Label("Type:"),             0, 0); pane.add(cmbType,            1, 0);
        pane.add(new Label("Start dato:"),       0, 1); pane.add(txfStartDato,       1, 1);
        pane.add(new Label("Slut dato:"),        0, 2); pane.add(txfSlutDato,        1, 2);
        pane.add(new Label("Noter:"),            0, 3); pane.add(txaNoter,           1, 3);
        pane.add(new Label("Medarbejder Navn:"), 0, 4); pane.add(txfMedarbejderNavn, 1, 4);

        Button btnOpret = new Button("Opret Melding");
        Button btnRediger = new Button("Gem ændringer");
        Button btnDelete = new Button("Slet Melding");
        Button btnLuk = new Button("Luk");

        HBox btnBox = new HBox(10, btnOpret, btnRediger, btnDelete, btnLuk);
        btnBox.setPadding(new Insets(8, 0, 0, 0));

        VBox bottom = new VBox(10, pane, btnBox);
        root.setBottom(bottom);

        tvwMelding.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cmbType.setValue(newValue.getType());
                txfStartDato.setText(newValue.getStartDato().toString());
                txfSlutDato.setText(newValue.getSlutDato().toString());
                txaNoter.setText(newValue.getNoter());
                txfMedarbejderNavn.setText(newValue.getMedarbejder().getNavn());
            }
        });

        /*
        =============================
        |       Button Actions      |
        =============================
         */
        btnSøg.setOnAction(e -> {
            String søgeord = txfSøg.getText().trim();
            if (søgeord.isEmpty()) {
                showAlert("Skriv et navn at søge på.");
                return;
            }

            try {
                tvwMelding.getItems().setAll(controller.getMeldingerForMedarbejder(søgeord));
            } catch (SQLException ex) {
                showAlert("Fejl ved søgning:\n" + ex.getMessage());
            }
        });

        btnVis.setOnAction(e -> {
            try {
                tvwMelding.getItems().setAll(controller.getAlleMeldinger());
            } catch (SQLException ex) {
                showAlert("Fejl ved hentning:\n" + ex.getMessage());
            }
        });

        btnOpret.setOnAction(e -> {
            try {
                LocalDate start = LocalDate.parse(txfStartDato.getText().trim());
                LocalDate slut = LocalDate.parse(txfSlutDato.getText().trim());

                int nextId = tvwMelding.getItems().stream().mapToInt(Melding::getMeldingsId).max().orElse(0) + 1;

                Melding ny = controller.createMelding(
                        nextId,
                        cmbType.getValue(),
                        start, slut,
                        txaNoter.getText().trim(),
                        txfMedarbejderNavn.getText().trim()
                );

                if (ny != null) {
                    tvwMelding.getItems().add(ny);
                } else {
                    showAlert("Medarbejder ikke fundet");
                }
            } catch (Exception exception1) {
                showAlert("Fejl ved oprettelse:\n" + exception1.getMessage());
            }
        });

        btnRediger.setOnAction(e -> {
            Melding valgt = tvwMelding.getSelectionModel().getSelectedItem();
            if (valgt == null) {
                showAlert("Vælg en melding først.");
                return;
            }

            try {
                LocalDate start = LocalDate.parse(txfStartDato.getText().trim());
                LocalDate slut = LocalDate.parse(txfSlutDato.getText().trim());

                Melding ny = controller.updateMelding(
                        valgt.getMeldingsId(),
                        cmbType.getValue(),
                        start, slut,
                        txaNoter.getText().trim(),
                        txfMedarbejderNavn.getText().trim()
                );

                tvwMelding.getItems().setAll(controller.getAlleMeldinger());
            } catch (Exception ex) {
                showAlert("Fejl ved opdatering:\n" + ex.getMessage());
            }
        });

        btnDelete.setOnAction(e -> {
            Melding valgt = tvwMelding.getSelectionModel().getSelectedItem();
            if (valgt == null) {
                showAlert("Vælg en melding først.");
                return;
            }

            try {
                controller.deleteMelding(valgt.getMeldingsId());
                tvwMelding.getItems().remove(valgt);
            } catch (SQLException ex) {
                showAlert("Fejl ved sletning:\n" + ex.getMessage());
            }
        });

        btnLuk.setOnAction(e -> stage.close());

        stage.setScene(new Scene(root, 950, 600));
        stage.showAndWait();
    }

    /*
    =============================================
    |               HjælpeMetoder               |
    =============================================
     */

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

    private int getNextMedId() {
        return tvwMedarbejdere.getItems().stream()
                .mapToInt(Medarbejder::getMedId)
                .max()
                .orElse(0) + 1;
    }

    private void deleteMedarbejderAction(Medarbejder medarbejder) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel sletning af valgt Medarbejder");
        alert.setHeaderText("Er du sikker på du vil slette valgt Medarbejder?");
        alert.setContentText("Hvis du trykker 'Ok' sletter du alt ved valgte Medarbejder");

        Optional<ButtonType> resultat = alert.showAndWait();

        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            controller.deleteMedarbejder(medarbejder.getMedId());
            System.out.println("Slettet medarbejder");
        }
    }

    private void showAlert(String besked) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel");
        alert.setHeaderText(null);
        alert.setContentText(besked);
        alert.showAndWait();
    }
}