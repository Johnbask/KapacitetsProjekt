package GUI;



import Model.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.List;

public class TeamOversigt extends GridPane {

    private TableView<Team> tvwTeams;

    public TeamOversigt() {
        initContent();
    }

    private void initContent() {

        tvwTeams = new TableView<>();
        tvwTeams.setPrefSize(500, 400);

        // =====================================================
        // COLUMNS
        // =====================================================

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

        this.add(tvwTeams, 0, 0);
    }
    public void setTeams(List<Team> teams) {
        tvwTeams.getItems().setAll(teams);
    }
}
