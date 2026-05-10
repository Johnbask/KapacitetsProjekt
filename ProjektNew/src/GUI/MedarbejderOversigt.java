package GUI;


import Model.Medarbejder;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class MedarbejderOversigt extends GridPane {

    private TableView<Medarbejder> tvwMedarbejdere;

    public MedarbejderOversigt() {
        this.initContent();
    }

    private void initContent() {
        tvwMedarbejdere = new TableView<>();
        this.add(tvwMedarbejdere, 0, 0);

        // Kolonner
        TableColumn<Medarbejder, String> colNavn = new TableColumn<>("Navn");
        colNavn.setCellValueFactory(new PropertyValueFactory<>("navn"));

        TableColumn<Medarbejder, String> colInitialer = new TableColumn<>("Initialer");
        colInitialer.setCellValueFactory(new PropertyValueFactory<>("initialer"));

        TableColumn<Medarbejder, String> colStilling = new TableColumn<>("Stilling");
        colStilling.setCellValueFactory(new PropertyValueFactory<>("stilling"));

        TableColumn<Medarbejder, Object> colTeam = new TableColumn<>("Team");
        colTeam.setCellValueFactory(new PropertyValueFactory<>("team"));

        TableColumn<Medarbejder, Object> colAfdeling = new TableColumn<>("Afdeling");
        colAfdeling.setCellValueFactory(new PropertyValueFactory<>("afdeling"));

        tvwMedarbejdere.getColumns().addAll(
                colNavn,
                colInitialer,
                colStilling,
                colTeam,
                colAfdeling
        );
    }
}
