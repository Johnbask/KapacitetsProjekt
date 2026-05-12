package GUI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.List;

import Model.Enum.ØkonomiType;
import Model.Projekt;
import Model.RessourceBehov;
import Model.Team;

public class HovdeVindue extends Application {

    @Override
    public void start(Stage stage) {

        stage.setTitle("Kapacitets Project");

        BorderPane pane = new BorderPane();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // =====================================================
        // PROJEKT OVERSIGT
        // =====================================================
        Tab tabProjekt = new Tab("Projekt oversigt");

        ProjektOversigt projektPane = new ProjektOversigt();
        tabProjekt.setContent(projektPane);

        // =====================================================
        // TEAM OVERSIGT
        // =====================================================
        Tab tabTeams = new Tab("Team oversigt");

        TeamOversigt teamPane = new TeamOversigt();

        Team t1 = new Team(1, "Backend");
        Team t2 = new Team(2, "Frontend");
        Team t3 = new Team(3, "DevOps");

        ObservableList<Team> teams = FXCollections.observableArrayList(
                t1, t2, t3
        );

        teamPane.setTeams(teams);
        tabTeams.setContent(teamPane);

        // =====================================================
        // TEST DATA - PROJEKTER
        // =====================================================

        Projekt p1 = new Projekt(1, "System A");
        p1.addRessourceBehov(new RessourceBehov(
                1, "Developer", YearMonth.of(2026, 11),
                0.8, 850, ØkonomiType.CAPEX));
        p1.addRessourceBehov(new RessourceBehov(
                2, "Developer", YearMonth.of(2026, 12),
                0.6, 850, ØkonomiType.CAPEX));
        p1.addRessourceBehov(new RessourceBehov(
                3, "Developer", YearMonth.of(2027, 1),
                0.9, 850, ØkonomiType.CAPEX));

        Projekt p2 = new Projekt(2, "System B");
        p2.addRessourceBehov(new RessourceBehov(
                4, "Tester", YearMonth.of(2026, 12),
                0.5, 700, ØkonomiType.CAPEX));
        p2.addRessourceBehov(new RessourceBehov(
                5, "Tester", YearMonth.of(2027, 1),
                0.7, 700, ØkonomiType.CAPEX));
        p2.addRessourceBehov(new RessourceBehov(
                6, "Tester", YearMonth.of(2027, 2),
                0.6, 700, ØkonomiType.CAPEX));

        Projekt p3 = new Projekt(3, "Quick Fix");
        p3.addRessourceBehov(new RessourceBehov(
                7, "Consultant", YearMonth.of(2027, 1),
                1.0, 1200, ØkonomiType.CAPEX));
        p3.addRessourceBehov(new RessourceBehov(
                8, "Consultant", YearMonth.of(2027, 2),
                0.5, 1200, ØkonomiType.CAPEX));

        Projekt p4 = new Projekt(4, "Enterprise Platform");
        p4.addRessourceBehov(new RessourceBehov(
                10, "Architect", YearMonth.of(2026, 9),
                1.0, 1200, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                11, "Developer", YearMonth.of(2026, 10),
                0.8, 850, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                12, "Developer", YearMonth.of(2026, 11),
                0.8, 850, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                13, "Developer", YearMonth.of(2026, 12),
                0.7, 850, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                14, "Tester", YearMonth.of(2027, 1),
                0.6, 700, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                15, "Tester", YearMonth.of(2027, 2),
                0.6, 700, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                16, "Developer", YearMonth.of(2027, 3),
                0.5, 850, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                17, "Developer", YearMonth.of(2027, 4),
                0.5, 850, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                18, "Support", YearMonth.of(2027, 5),
                0.3, 600, ØkonomiType.CAPEX));
        p4.addRessourceBehov(new RessourceBehov(
                19, "Support", YearMonth.of(2027, 6),
                0.3, 600, ØkonomiType.CAPEX));

        List<Projekt> projekter = List.of(p1, p2, p3, p4);
        projektPane.buildTimeline(projekter);

        // =====================================================
        // ØVRIGE TABS
        // =====================================================
        Tab tabMedarbejder = new Tab("Medarbejder oversigt");
        tabMedarbejder.setContent(new MedarbejderOversigt());

        Tab tabBehov = new Tab("Behov oversigt");

        tabPane.getTabs().addAll(
                tabBehov,
                tabMedarbejder,
                tabProjekt,
                tabTeams
        );

        pane.setCenter(tabPane);

        Scene scene = new Scene(pane, 1100, 600);
        stage.setScene(scene);
        stage.show();
    }
}