package GUI;


import Controller.Controller;
import Model.*;
import Model.Enum.MedarbejderType;
import com.sun.javafx.font.freetype.HBGlyphLayout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TeamOversigt extends BorderPane {

    private final Controller controller = Controller.getInstance();

    private TableView<Team> tvwTeams;
    private Button btnTilføjMedarbejder;
    private Button btnFjernMedarbejder;
    private Button btnOpdater;
    private Button btnTilføjTeam;
    private Button btnOpdaterTeam;
    private Button btnSletTeam;

    private List<Allokering> allokeringer = List.of();

    public TeamOversigt() {
        initContent();
        initActions();
        loadTeams();
    }

    private void initContent() {

        this.setPadding(new Insets(10));

        // ==========================================
        // TABLE
        // ==========================================
        tvwTeams = new TableView<>();
        tvwTeams.setPrefSize(1000, 500);

        TableColumn<Team, Integer> colId = new TableColumn<>("Team ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("teamId"));
        colId.setPrefWidth(70);

        TableColumn<Team, String> colNavn = new TableColumn<>("Navn");
        colNavn.setCellValueFactory(new PropertyValueFactory<>("navn"));
        colNavn.setPrefWidth(150);

        TableColumn<Team, Integer> colAntal = new TableColumn<>("Antal medarbejdere");
        colAntal.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cellData.getValue()
                                .getMedarbejdere()
                                .size()
                )
        );
        colAntal.setPrefWidth(60);

        TableColumn<Team, Void> colMedarbejdere = new TableColumn<>("Medarbejdere");
        colMedarbejdere.setPrefWidth(250);
        colMedarbejdere.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Team team = (Team) getTableRow().getItem();
                    VBox navneBox = new VBox(2);
                    navneBox.setPadding(new Insets(4, 0, 4, 0));

                    for (Medarbejder m : team.getMedarbejdere()) {
                        Label lblNavn = new Label(m.getNavn());
                        navneBox.getChildren().add(lblNavn);
                    }

                    if (team.getMedarbejdere().isEmpty()) {
                        navneBox.getChildren().add(new Label("Ingen medarbejdere"));
                    }

                    setGraphic(navneBox);
                }
            }
        });

        TableColumn<Team, Void> colProjekter = new TableColumn<>("Projekter");
        colProjekter.setPrefWidth(200);
        colProjekter.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Team team = (Team) getTableRow().getItem();

                List<Projekt> projekter = allokeringer.stream()
                        .filter(a -> a.getMedarbejdere().stream()
                                .anyMatch(am -> team.getMedarbejdere().stream()
                                        .anyMatch(tm -> tm.getMedId() == am.getMedId())))
                        .map(Allokering::getProjekt)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

                VBox box = new VBox(2);
                box.setPadding(new Insets(4, 0, 4, 0));

                if (projekter.isEmpty()) {
                    box.getChildren().add(new Label("Ingen projekter"));
                } else {
                    for (Projekt p : projekter) {
                        box.getChildren().add(new Label(p.getNavn()));
                    }
                }
                setGraphic(box);
            }
        });


        tvwTeams.getColumns().addAll(
                colId,
                colNavn,
                colAntal,
                colMedarbejdere,
                colProjekter
        );

        tvwTeams.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (!empty && team != null) {
                    int count = Math.max(1, team.getMedarbejdere().size());
                    setPrefHeight(count * 28 + 8);
                } else {
                    setPrefHeight(USE_COMPUTED_SIZE);
                }
            }
        });

        this.setCenter(tvwTeams);

        // ==========================================
        // BUTTON BOTTOM LEFT
        // ==========================================
        btnTilføjMedarbejder = new Button("Tilføj medarbejder");
        btnFjernMedarbejder  = new Button("Fjern medarbejder");
        btnTilføjTeam        = new Button("Tilføj Team");
        btnOpdaterTeam       = new Button("Opdater Team");
        btnSletTeam          = new Button("Slet");
        btnOpdater           = new Button("Opdater");

        HBox bottomBox = new HBox(10, btnTilføjMedarbejder, btnFjernMedarbejder,
                btnTilføjTeam, btnOpdaterTeam, btnSletTeam, btnOpdater);
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));

        this.setBottom(bottomBox);
    }

    private void initActions() {

        btnTilføjMedarbejder.setOnAction(e -> {
            Team valgtTeam = tvwTeams.getSelectionModel().getSelectedItem();

            if (valgtTeam != null) {
                openTilføjMedarbejderWindow(valgtTeam);
            } else {
                showAlert("Vælg et team først.");
            }
        });

        btnFjernMedarbejder.setOnAction(e -> {
            Team valgtTeam = tvwTeams.getSelectionModel().getSelectedItem();
            if (valgtTeam == null) {
                showAlert("Vælg et team først.");
                return;
            }
            if (valgtTeam.getMedarbejdere().isEmpty()) {
                showAlert("Dette team har ingen medarbejdere.");
                return;
            }
            openFjernMedarbejderWindow(valgtTeam);
        });

        btnOpdater.setOnAction(e -> {
            loadAllokeringer();
            loadTeams();
        });

        btnTilføjTeam.setOnAction(e -> openTilføjTeamWindow());

        btnOpdaterTeam.setOnAction(e -> {
            Team valgtTeam = tvwTeams.getSelectionModel().getSelectedItem();

            if (valgtTeam != null) {
                openOpdaterTeamWindow(valgtTeam);
            } else {
                showAlert("Vælg et team først.");
            }
        });

        btnSletTeam.setOnAction(e -> {
            Team valgtTeam = tvwTeams.getSelectionModel().getSelectedItem();

            if (valgtTeam != null) {
                try {
                    deleteTeamAction(valgtTeam);
                } catch (SQLException ex) {
                    showAlert("Fejl ved sletning: " + ex.getMessage());
                }
            } else {
                showAlert("Vælg et team først");
            }
        });
    }

    /*
    =================================
    |           DATA INPUTS         |
    =================================
     */
    private void loadTeams() {
        try {
            loadAllokeringer();
            List<Team> teams = controller.getAlleTeams();
            tvwTeams.getItems().setAll(teams);
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af teams:\n" + e.getMessage());
        }
    }

    private void loadAllokeringer() {
        try {
            allokeringer = controller.getAlleAllokeringer();
            System.out.println("Allokeringer loaded: " + allokeringer.size());
            for (Allokering a : allokeringer) {
                System.out.println("    Allokering " + a.getAllokeringsId() +
                        " - medarbejdere: " + a.getMedarbejdere().size() +
                        " - projekt: " + (a.getProjekt() != null ? a.getProjekt().getNavn() : "NULL"));;
            }
        } catch (SQLException e) {
            allokeringer = List.of();
            System.out.println("Fejl ved hentning af allokeringer: " + e.getMessage());
        }
    }

    /*
    ==========================================
    |       TILFØJ MEDARBEJDER TIL TEAM      |
    ==========================================
     */
    private void openTilføjMedarbejderWindow(Team team) {

        Stage stage = new Stage();
        stage.setTitle("Tilføj medarbejder til " + team.getNavn());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

       TextField txfSøg = new TextField();
       txfSøg.setPromptText("Skriv et navn eller initialer...");

       ListView<Medarbejder> lvwResultater = new ListView<>();
       lvwResultater.setPrefHeight(150);
       lvwResultater.setCellFactory(lv -> new ListCell<>() {
           @Override
           protected void updateItem(Medarbejder m, boolean empty) {
               super.updateItem(m, empty);
               setText(empty || m == null ? null : m.getNavn() + " (" + m.getInitialer() + ")");
           }
       });

       Button btnSøg = new Button("Søg");
       Button btnGem = new Button("Gem");
       Button btnLuk = new Button("Luk");

       pane.add(new Label("Søg medarbejder:"), 0, 0);
       pane.add(txfSøg, 1, 0);
       pane.add(btnSøg, 2, 0);
       pane.add(lvwResultater, 0, 1, 3, 1);
       pane.add(new HBox(10, btnGem, btnLuk), 1, 2);

       btnSøg.setOnAction(e -> {
           String søgeord = txfSøg.getText().trim();
           if (søgeord.isEmpty()) {
               showAlert("Skriv et navn at søge på.");
               return;
           }
           try {
               ArrayList<Medarbejder> resultater = controller.søgMedarbejderNavn(søgeord);
               lvwResultater.getItems().setAll(resultater);
               if (resultater.isEmpty()) {
                   showAlert("Ingen medarbejder fundet med navn: " + søgeord);
               }
           } catch (SQLException ex) {
               showAlert("Fejl ved søgning:\n" + ex.getMessage());
           }
       });

       btnGem.setOnAction(e -> {
           Medarbejder valgt = lvwResultater.getSelectionModel().getSelectedItem();
           if (valgt == null) {
               showAlert("Vælg en medarbejder fra listen først.");
               return;
           }
           try {
               controller.updateMedarbejder(
                       valgt.getMedId(),
                       valgt.getInitialer(),
                       valgt.getNavn(),
                       valgt.getType(),
                       valgt.getStilling(),
                       valgt.isFratrådt(),
                       valgt.getAfdeling() != null ? valgt.getAfdeling().getNavn() : "",
                       valgt.getOrganisation() != null ? valgt.getOrganisation().getNavn() : "",
                       team.getNavn()
               );
               team.addMedarbejder(valgt);
               tvwTeams.refresh();
               stage.close();
           } catch (SQLException ex) {
               showAlert("Fejl ved opdatering:\n" + ex.getMessage());
           }


       });
       btnLuk.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 400, 300));
        stage.showAndWait();
    }

    private void openFjernMedarbejderWindow(Team team) {
        Stage stage = new Stage();
        stage.setTitle("Fjern medarbejder fra " + team.getNavn());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        ComboBox<Medarbejder> cmbMedarbejder = new ComboBox<>();
        cmbMedarbejder.getItems().addAll(team.getMedarbejdere());
        cmbMedarbejder.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Medarbejder m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : m.getNavn() + " (" + m.getInitialer() + ")");
            }
        });

        cmbMedarbejder.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Medarbejder m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : m.getNavn() + " (" + m.getInitialer() + ")");
            }
        });

        cmbMedarbejder.getSelectionModel().selectFirst();

        pane.add(new Label("Medarbejder:"), 0, 0);
        pane.add(cmbMedarbejder, 1, 0);

        Button btnFjern = new Button("Fjern");
        Button btnLuk = new Button("Luk");
        pane.add(new HBox(10, btnFjern, btnLuk), 1, 1);

        btnFjern.setOnAction(e -> {
            Medarbejder valgt = cmbMedarbejder.getValue();
            if (valgt == null) {
                showAlert("Vælg en medarbejder");
                return;
            }

            try {
                controller.fjernMedarbejderFraTeam(valgt.getMedId());
                team.deleteMedarbejder(valgt);
                tvwTeams.refresh();
                cmbMedarbejder.getItems().remove(valgt);

                if (team.getMedarbejdere().isEmpty()) {
                    stage.close();
                }
            } catch (SQLException ex) {
                showAlert("Fejl ved fjernelse:\n" + ex.getMessage());
            }
        });

        btnLuk.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 320, 120));
        stage.showAndWait();
    }

    /*
    =========================
    |       CREATE TEAM     |
    =========================
     */

    private void openTilføjTeamWindow() {
        Stage stage = new Stage();
        stage.setTitle("Tilføj et nyt team");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        TextField txfTeamId = new TextField();
        TextField txfTeamNavn = new TextField();

        pane.add(new Label("Team ID"),   0, 0); pane.add(txfTeamId, 1, 0);
        pane.add(new Label("Team Navn"), 0, 1); pane.add(txfTeamNavn, 1, 1);

        Button btnGem = new Button("Gem");
        Button btnLuk = new Button("Luk");
        pane.add(new HBox(10, btnGem, btnLuk), 1, 2);

        btnGem.setOnAction(e -> {
            if (txfTeamNavn.getText().trim().isEmpty()) {
                showAlert("Team Navn skal udfyldes.");
                return;
            }

            try {
                int teamId = Integer.parseInt(txfTeamId.getText().trim());
                Team team = controller.createTeam(teamId, txfTeamNavn.getText().trim());
                if (team != null) {
                    tvwTeams.getItems().add(team);
                    stage.close();
                }
            } catch (NumberFormatException ex) {
                showAlert("Team ID skal være et heltal");
            } catch (SQLException ex) {
                showAlert("Fejl ved oprettelse af Team: " + ex.getMessage());
            }
        });

        btnLuk.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 260, 150));
        stage.showAndWait();
    }

    /*
    =============================
    |       Hjælpe Metoder      |
    =============================
     */

    private void deleteTeamAction(Team team) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel Sletning af valgt Team");
        alert.setHeaderText("Er du sikker på du vil slette valgt Team?");
        alert.setContentText("Hvis du trykker 'Ok' sletter du alt ved valgte Team");

        Optional<ButtonType> resultat = alert.showAndWait();

        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            controller.deleteTeam(team.getTeamId());
            System.out.println("Slettet");
        }
    }

    private void openOpdaterTeamWindow(Team team) {
        Stage stage = new Stage();
        stage.setTitle("Opdater Team: " + team.getNavn());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        TextField txfTeamNavn = new TextField(team.getNavn());

        pane.add(new Label("Team Navn"), 0, 1); pane.add(txfTeamNavn, 1, 0);

        Button btnGem = new Button("Gem");
        Button btnLuk = new Button("Luk");
        pane.add(new HBox(10, btnGem, btnLuk), 1, 1);

        btnGem.setOnAction(e -> {
            if (txfTeamNavn.getText().trim().isEmpty()) {
                showAlert("Team Navn skal udfyldes.");
                return;
            }

            try {
                Team opdateret = controller.updateTeam(team.getTeamId(), txfTeamNavn.getText().trim());
                if (opdateret != null) {
                    loadTeams();
                    stage.close();
                }
            } catch (SQLException ex) {
                showAlert("Fejl ved opdatering af Team: " + ex.getMessage());
            }
        });

        btnLuk.setOnAction(e -> stage.close());

        Scene scene = new Scene(pane, 250, 150);
        stage.setScene(scene);
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