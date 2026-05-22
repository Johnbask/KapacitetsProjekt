package GUI;

import Model.*;
import Model.Enum.MedarbejderType;
import Model.Enum.ØkonomiType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class HovdeVindue extends Application {

    @Override
    public void start(Stage stage) {

        stage.setTitle("Kapacitets Project");

        BorderPane pane = new BorderPane();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // =====================================================
        // MEDARBEJDERE (testdata)
        // =====================================================
        Medarbejder m1 = new Medarbejder(1, "JH", "Jonas Hansen",
                MedarbejderType.INTERN, "Developer", false, null, null, null);

        Medarbejder m2 = new Medarbejder(2, "MK", "Mette Kristensen",
                MedarbejderType.INTERN, "Tester", false, null, null, null);

        Medarbejder m3 = new Medarbejder(3, "AB", "Anders Berg",
                MedarbejderType.EKSTERN, "Architect", false, null, null, null);

        // =====================================================
        // ALLOKERINGER (testdata)
        // =====================================================
        /*
        Allokering a1 = new Allokering(1, YearMonth.of(2026, 9), 0.8);
        Allokering a2 = new Allokering(2, YearMonth.of(2026, 10), 0.6);
        Allokering a3 = new Allokering(3, YearMonth.of(2026, 11), 0.9);
        Allokering a4 = new Allokering(4, YearMonth.of(2026, 12), 0.7);
        Allokering a5 = new Allokering(5, YearMonth.of(2027, 1), 0.5);
        Allokering a6 = new Allokering(6, YearMonth.of(2027, 2), 0.6);
        Allokering a7 = new Allokering(7, YearMonth.of(2027, 3), 0.4);


        a1.addMedarbejder(m1);
        a2.addMedarbejder(m1);
        a3.addMedarbejder(m1);
        a4.addMedarbejder(m2);
        a5.addMedarbejder(m2);
        a6.addMedarbejder(m3);
        a7.addMedarbejder(m3);
        */


        // =====================================================
        // PROJEKTER — delt muterbar liste
        // =====================================================
        Projekt p1 = new Projekt(1, "System A");
        Projekt p2 = new Projekt(2, "System B");

        p1.addRessourceBehov(new RessourceBehov(
                1, "Developer",
                YearMonth.of(2026, 9),
                YearMonth.of(2026, 11),
                2, 850, ØkonomiType.CAPEX));

        p2.addRessourceBehov(new RessourceBehov(
                2, "Tester",
                YearMonth.of(2026, 10),
                YearMonth.of(2027, 2),
                1, 700, ØkonomiType.OPEX));

        p2.addRessourceBehov(new RessourceBehov(
                3, "UX",
                YearMonth.of(2026, 11),
                YearMonth.of(2027, 3),
                1.5, 900, ØkonomiType.CAPEX));

        // VIGTIGT: ArrayList så listen kan muteres og deles mellem views
        List<Projekt> projekter = new ArrayList<>(List.of(p1, p2));

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
        // CALLBACKS — kobler de to views sammen
        //
        // Når et behov oprettes/redigeres/slettes i BehovOversigt:
        //   → genbyg tidslinjen i ProjektOversigt
        //
        // Når et projekt oprettes i ProjektOversigt:
        //   → opdater projektlisten i BehovOversigt
        // =====================================================
        behovPane.setOnBehovChanged(() ->
                projektPane.buildTimeline(projekter)
        );

        projektPane.setOnProjektOprettet(nytProjekt -> {
            projekter.add(nytProjekt);
            projektPane.buildTimeline(projekter);
            behovPane.setProjekter(projekter);
        });


        // =====================================================
        // TABS
        // =====================================================
        Tab tabDashboard = new Tab("Dashboard", dashboardPane);
        Tab tabBehov      = new Tab("Behov oversigt",      behovPane);
        Tab tabMedarbejder = new Tab("Medarbejder oversigt", medarbejderPane);
        Tab tabProjekt    = new Tab("Projekt oversigt",     projektPane);
        Tab tabTeams      = new Tab("Team oversigt",        teamPane);

        tabPane.getTabs().addAll(tabDashboard, tabBehov, tabMedarbejder, tabProjekt, tabTeams);

        pane.setCenter(tabPane);

        Scene scene = new Scene(pane, 1200, 650);

        // Dashboard
        tabDashboard.setOnSelectionChanged(e -> {
            if (tabDashboard.isSelected()) {
                dashboardPane.initContent();
            }
        });

        // Dynamisk størrelse — alt indeni følger med automatisk
        pane.prefWidthProperty().bind(scene.widthProperty());
        pane.prefHeightProperty().bind(scene.heightProperty());
        tabPane.prefWidthProperty().bind(scene.widthProperty());
        tabPane.prefHeightProperty().bind(scene.heightProperty());

        stage.setScene(scene);
        stage.show();
    }
}