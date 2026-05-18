package GUI;


import Controller.Controller;
import Model.*;
import Model.Enum.MedarbejderType;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TeamOversigt extends BorderPane {

    private final Controller controller = Controller.getInstance();

    private TableView<Team> tvwTeams;
    private Button btnTilføjMedarbejder;
    private Button btnOpdater;
    private Button btnTilføjTeam;
    private Button btnOpdaterTeam;
    private Button btnSletTeam;

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
        tvwTeams.setPrefSize(700, 400);

        TableColumn<Team, Integer> colId =
                new TableColumn<>("Team ID");
        colId.setCellValueFactory(
                new PropertyValueFactory<>("teamId")
        );
        colId.setPrefWidth(80);

        TableColumn<Team, String> colNavn =
                new TableColumn<>("Navn");
        colNavn.setCellValueFactory(
                new PropertyValueFactory<>("navn")
        );
        colNavn.setPrefWidth(150);

        TableColumn<Team, Integer> colAntal =
                new TableColumn<>("Antal medarbejdere");
        colAntal.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cellData.getValue()
                                .getMedarbejdere()
                                .size()
                )
        );
        colAntal.setPrefWidth(150);

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
        colProjekter.setPrefWidth(250);
        colProjekter.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Team team = (Team) getTableRow().getItem();

                    VBox projekterBox = new VBox(2);
                    projekterBox.setPadding(new Insets(4, 0, 4, 0));

                    try {
                        List<Projekt> projekter = controller.getAlleAllokeringer().stream()
                                .filter(a -> a.getMedarbejdere().stream()
                                        .anyMatch(m -> team.getMedarbejdere()
                                                .contains(m)))
                                .map(Allokering::getProjekt)
                                .filter(Objects::nonNull).distinct().toList();

                        if (projekter.isEmpty()) {
                            projekterBox.getChildren().add(new Label("Ingen projekter"));
                        } else {
                            for (Projekt p : projekter) {
                                projekterBox.getChildren().add(new Label(p.getNavn()));
                            }
                        }
                    } catch (SQLException e) {
                        projekterBox.getChildren().add(new Label("Fejl ved hentning"));
                    }

                    setGraphic(projekterBox);
                }
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
        btnOpdater = new Button("Opdater");
        btnTilføjTeam = new Button("Tilføj Team");
        btnOpdaterTeam = new Button("Opdater Team");
        btnSletTeam = new Button("Slet");

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        bottomBox.getChildren().addAll(btnTilføjMedarbejder, btnTilføjTeam,btnOpdaterTeam, btnSletTeam, btnOpdater);

        this.setBottom(bottomBox);
    }

    private void initActions() {

        btnTilføjMedarbejder.setOnAction(e -> {

            Team valgtTeam =
                    tvwTeams.getSelectionModel().getSelectedItem();

            if (valgtTeam != null) {
                openTilføjMedarbejderWindow(valgtTeam);
            } else {
                showAlert("Vælg et team først.");
            }
        });

        btnOpdater.setOnAction(e -> loadTeams());

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

    private void loadTeams() {
        try {
            List<Team> teams = controller.getAlleTeams();
            tvwTeams.getItems().setAll(teams);
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af teams:\n" + e.getMessage());
        }
    }

    public void setTeams(List<Team> teams) {
        tvwTeams.getItems().setAll(teams);
    }

    private void openTilføjMedarbejderWindow(Team team) {

        Stage stage = new Stage();
        stage.setTitle("Tilføj medarbejder til " + team.getNavn());

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        TextField txfMedId = new TextField();
        TextField txfNavn = new TextField();
        TextField txfInitialer = new TextField();
        TextField txfStilling = new TextField();

        // Dropdowns
        ComboBox<MedarbejderType> cmbType = new ComboBox<>();
        ComboBox<Afdeling> cmbAfdeling = new ComboBox<>();
        ComboBox<Organisation> cmbOrganisation = new ComboBox<>();
        CheckBox chkFratrådt = new CheckBox("Fratrådt");

        cmbType.getItems().addAll(MedarbejderType.values());
        cmbType.getSelectionModel().selectFirst();

        try {
            cmbAfdeling.getItems().addAll(controller.getAlleAfdelinger());
            cmbOrganisation.getItems().addAll(controller.getAlleOrganisationer());
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af afdelinger/organisationer:\n" + e.getMessage());
        }

        cmbAfdeling.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Afdeling a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getLeder());
            }
        });

        cmbAfdeling.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Afdeling a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getLeder());
            }
        });

        cmbOrganisation.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Organisation o, boolean empty) {
                super.updateItem(o, empty);
                setText(empty || o == null ? null : o.getNavn());
            }
        });

        cmbOrganisation.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Organisation o, boolean empty) {
                super.updateItem(o, empty);
                setText(empty || o == null ? null : o.getNavn());
            }
        });

        // --- Layout ---
        pane.add(new Label("Med ID:"), 0, 0);
        pane.add(txfMedId, 1, 0);

        pane.add(new Label("Navn:"), 0, 1);
        pane.add(txfNavn, 1, 1);

        pane.add(new Label("Initialer:"), 0, 2);
        pane.add(txfInitialer, 1, 2);

        pane.add(new Label("Stilling:"), 0, 3);
        pane.add(txfStilling, 1, 3);

        pane.add(new Label("Type:"), 0, 4);
        pane.add(cmbType, 1, 4);

        pane.add(new Label("Afdeling:"), 0, 5);
        pane.add(cmbAfdeling, 1, 5);

        pane.add(new Label("Organisation:"), 0, 6);
        pane.add(cmbOrganisation, 1, 6);

        Button btnGem = new Button("Gem");
        Button btnLuk = new Button("Luk");
        HBox btnBox = new HBox(10, btnGem, btnLuk);
        pane.add(btnBox, 1, 8);

        btnGem.setOnAction(e -> {
            String navn = txfNavn.getText().trim();
            String initialer = txfInitialer.getText().trim();
            String stilling = txfStilling.getText().trim();
            String medIdTekst = txfMedId.getText().trim();

            if (navn.isEmpty() || initialer.isEmpty() || stilling.isEmpty() || medIdTekst.isEmpty()) {
                showAlert("Alle felter skal udfyldes");
                return;
            }

            if (cmbAfdeling.getValue() == null) {
                showAlert("Vælg en afdeling.");
                return;
            }

            if (cmbOrganisation.getValue() == null) {
                showAlert("Vælg en organisation.");
                return;
            }

            int medId;

            try {
                medId = Integer.parseInt(medIdTekst);
            } catch (NumberFormatException ex) {
                showAlert("Med ID skal være et heltal.");
                return;
            }

            try {
                Medarbejder ny = controller.updateMedarbejder(
                        medId,
                        initialer,
                        navn,
                        cmbType.getValue(),
                        stilling,
                        chkFratrådt.isSelected(),
                        cmbAfdeling.getValue().getAfdId(),
                        cmbOrganisation.getValue().getOrgId(),
                        team.getTeamId()
                );

                if (ny != null) {
                    team.addMedarbejder(ny);
                    tvwTeams.refresh();
                    stage.close();
                } else {
                    showAlert("Medarbejder kunne ikke opdateres - tjek om Med ID allerede findes.");
                }
            } catch (SQLException ex) {
                showAlert("Fejl ved opdatering:\n" + ex.getMessage());
            }
        });

        btnLuk.setOnAction(e -> stage.close());

        Scene scene = new Scene(pane, 380, 340);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void openTilføjTeamWindow() {
        Stage stage = new Stage();
        stage.setTitle("Tilføj et nyt team");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        TextField txfTeamId = new TextField();
        TextField txfTeamNavn = new TextField();

        pane.add(new Label("Team ID"), 0, 0);
        pane.add(txfTeamId, 1, 0);

        pane.add(new Label("Team Navn"), 0, 1);
        pane.add(txfTeamNavn, 1, 1);

        Button btnGem = new Button("Gem");
        Button btnLuk = new Button("Luk");

        btnGem.setOnAction(e -> {
            String teamIdTekst = txfTeamId.getText().trim();
            String teamNavn = txfTeamNavn.getText().trim();

            if (teamNavn.isEmpty()) {
                showAlert("Team Navn skal udfyldes.");
                return;
            }

            int teamId;

            try {
                teamId = Integer.parseInt(teamIdTekst);
            } catch (NumberFormatException ex) {
                showAlert("Team ID skal være et heltal.");
                return;
            }

            try {
                Team team = controller.createTeam(teamId, teamNavn);

                if (team != null) {
                    tvwTeams.refresh();
                    stage.close();
                }
            } catch (SQLException ex) {
                showAlert("Fejl ved oprettelse af Team: " + ex.getMessage());
            }
        });

        HBox btnBox = new HBox(10, btnGem, btnLuk);
        pane.add(btnBox, 1, 2);

        btnLuk.setOnAction(e -> stage.close());

        Scene scene = new Scene(pane, 250, 150);
        stage.setScene(scene);
        stage.showAndWait();
    }

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
        stage.setTitle("Tilføj et nyt team");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        TextField txfTeamId = new TextField();
        TextField txfTeamNavn = new TextField();

        pane.add(new Label("Team ID"), 0, 0);
        pane.add(txfTeamId, 1, 0);

        pane.add(new Label("Team Navn"), 0, 1);
        pane.add(txfTeamNavn, 1, 1);

        Button btnGem = new Button("Gem");
        Button btnLuk = new Button("Luk");

        btnGem.setOnAction(e -> {
            String teamIdTekst = txfTeamId.getText().trim();
            String teamNavn = txfTeamNavn.getText().trim();

            if (teamNavn.isEmpty()) {
                showAlert("Team Navn skal udfyldes.");
                return;
            }

            int teamId;

            try {
                teamId = Integer.parseInt(teamIdTekst);
            } catch (NumberFormatException ex) {
                showAlert("Team ID skal være et heltal.");
                return;
            }

            try {
                Team teamUpdate = controller.updateTeam(teamId, teamNavn);

                if (teamUpdate != null) {
                    tvwTeams.refresh();
                    stage.close();
                }
            } catch (SQLException ex) {
                showAlert("Fejl ved oprettelse af Team: " + ex.getMessage());
            }
        });

        HBox btnBox = new HBox(10, btnGem, btnLuk);
        pane.add(btnBox, 1, 2);

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