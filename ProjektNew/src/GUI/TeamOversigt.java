package GUI;


import Model.Medarbejder;
import Model.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class TeamOversigt extends BorderPane {

    private TableView<Team> tvwTeams;
    private Button btnTilføjMedarbejder;

    public TeamOversigt() {
        initContent();
        initActions();
    }

    private void initContent() {

        this.setPadding(new Insets(10));

        // ==========================================
        // TABLE
        // ==========================================
        tvwTeams = new TableView<>();
        tvwTeams.setPrefSize(500, 400);

        TableColumn<Team, Integer> colId =
                new TableColumn<>("Team ID");
        colId.setCellValueFactory(
                new PropertyValueFactory<>("teamId")
        );

        TableColumn<Team, String> colNavn =
                new TableColumn<>("Navn");
        colNavn.setCellValueFactory(
                new PropertyValueFactory<>("navn")
        );

        TableColumn<Team, Integer> colAntal =
                new TableColumn<>("Antal medarbejdere");
        colAntal.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cellData.getValue()
                                .getMedarbejdere()
                                .size()
                )
        );

        tvwTeams.getColumns().addAll(
                colId,
                colNavn,
                colAntal
        );

        this.setCenter(tvwTeams);

        // ==========================================
        // BUTTON BOTTOM LEFT
        // ==========================================
        btnTilføjMedarbejder = new Button("Tilføj medarbejder");

        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomBox.getChildren().add(btnTilføjMedarbejder);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));

        this.setBottom(bottomBox);
    }

    private void initActions() {

        btnTilføjMedarbejder.setOnAction(e -> {

            Team valgtTeam =
                    tvwTeams.getSelectionModel().getSelectedItem();

            if (valgtTeam != null) {
                openTilføjMedarbejderWindow(valgtTeam);
            }
        });
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

        TextField txfNavn = new TextField();
        TextField txfInitialer = new TextField();
        TextField txfStilling = new TextField();

        pane.add(new Label("Navn:"), 0, 0);
        pane.add(txfNavn, 1, 0);

        pane.add(new Label("Initialer:"), 0, 1);
        pane.add(txfInitialer, 1, 1);

        pane.add(new Label("Stilling:"), 0, 2);
        pane.add(txfStilling, 1, 2);

        Button btnGem = new Button("Gem");
        Button btnLuk = new Button("Luk");

        pane.add(btnGem, 0, 3);
        pane.add(btnLuk, 1, 3);

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
                    team
            );

            team.addMedarbejder(ny);

            tvwTeams.refresh();
            stage.close();
        });

        btnLuk.setOnAction(e -> stage.close());

        Scene scene = new Scene(pane, 350, 220);
        stage.setScene(scene);
        stage.showAndWait();
    }
}