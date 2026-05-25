package GUI;

import Controller.Controller;
import Model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HovdeVindue extends Application {

    private final Controller controller = Controller.getInstance();

    @Override
    public void start(Stage stage) {

        stage.setTitle("Kapacitets Project");

        BorderPane pane = new BorderPane();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // =====================================================
        // HENT DATA FRA DB
        // =====================================================
        List<Projekt> projekter = new ArrayList<>();

        try {
            // Hent alle projekter
            projekter = new ArrayList<>(controller.getAlleProjekter());

            // Hent alle ressourcebehov og kobl dem til de rigtige projekter
            ArrayList<RessourceBehov> alleBehov = controller.getAlleRessourceBehov();

            for (RessourceBehov rb : alleBehov) {
                if (rb.getProjekt() == null) continue;
                int rbProjektId = rb.getProjekt().getProjektId();

                for (Projekt p : projekter) {
                    if (p.getProjektId() == rbProjektId) {
                        p.addRessourceBehov(rb);
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            showAlert("Fejl ved hentning af data:\n" + e.getMessage());
        }

        // =====================================================
        // VIEWS
        // =====================================================
        Dashboard dashboardPane = new Dashboard();
        ProjektOversigt projektPane = new ProjektOversigt();
        BehovOversigt behovPane = new BehovOversigt();
        MedarbejderOversigt medarbejderPane = new MedarbejderOversigt();
        TeamOversigt teamPane = new TeamOversigt();

        // =====================================================
        // INDLÆS DATA I VIEWS
        // =====================================================
        projektPane.buildTimeline(projekter);
        behovPane.setProjekter(projekter);

        // =====================================================
        // CALLBACKS
        // =====================================================
        List<Projekt> finalProjekter = projekter;
        behovPane.setOnBehovChanged(() ->
                projektPane.buildTimeline(finalProjekter)
        );

        List<Projekt> finalProjekter1 = projekter;
        projektPane.setOnProjektOprettet(nytProjekt -> {
            finalProjekter1.add(nytProjekt);
            projektPane.buildTimeline(finalProjekter1);
            behovPane.setProjekter(finalProjekter1);
        });

        // =====================================================
        // TABS
        // =====================================================
        Tab tabDashboard   = new Tab("Dashboard",            dashboardPane);
        Tab tabBehov       = new Tab("Behov oversigt",       behovPane);
        Tab tabMedarbejder = new Tab("Medarbejder oversigt", medarbejderPane);
        Tab tabProjekt     = new Tab("Projekt oversigt",     projektPane);
        Tab tabTeams       = new Tab("Team oversigt",        teamPane);

        tabPane.getTabs().addAll(tabDashboard, tabBehov, tabMedarbejder, tabProjekt, tabTeams);

        tabDashboard.setOnSelectionChanged(e -> {
            if (tabDashboard.isSelected()) {
                dashboardPane.loadData();
            }
        });

        pane.setCenter(tabPane);

        Scene scene = new Scene(pane, 1200, 650);

        pane.prefWidthProperty().bind(scene.widthProperty());
        pane.prefHeightProperty().bind(scene.heightProperty());
        tabPane.prefWidthProperty().bind(scene.widthProperty());
        tabPane.prefHeightProperty().bind(scene.heightProperty());

        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String besked) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fejl");
        alert.setHeaderText(null);
        alert.setContentText(besked);
        alert.showAndWait();
    }
}
