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
import java.util.List;

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
        ProjektOversigt projektPane = new ProjektOversigt();
        Tab tabProjekt = new Tab("Projekt oversigt", projektPane);

        // =====================================================
        // TEAM OVERSIGT
        // =====================================================
        TeamOversigt teamPane = new TeamOversigt();
        Tab tabTeams = new Tab("Team oversigt", teamPane);

        // =====================================================
        // MEDARBEJDERE
        // =====================================================
        Medarbejder m1 = new Medarbejder(1, "JH", "Jonas Hansen",
                MedarbejderType.INTERN, "Developer", false, null, null, null);

        Medarbejder m2 = new Medarbejder(2, "MK", "Mette Kristensen",
                MedarbejderType.INTERN, "Tester", false, null, null, null);

        Medarbejder m3 = new Medarbejder(3, "AB", "Anders Berg",
                MedarbejderType.EKSTERN, "Architect", false, null, null, null);

        // =====================================================
        // ALLOKERINGER
        // =====================================================
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

        List<Allokering> allokeringer = List.of(a1, a2, a3, a4, a5, a6, a7);

        // =====================================================
        // PROJEKTER + BEHOV (VIGTIG RETTELSE HER)
        // =====================================================
        Projekt p1 = new Projekt(1, "System A");
        Projekt p2 = new Projekt(2, "System B");

        // SYSTEM A (3 måneder)
        p1.addRessourceBehov(new RessourceBehov(
                1, "Developer",
                YearMonth.of(2026, 9),
                YearMonth.of(2026, 11),
                2, 850, ØkonomiType.CAPEX));

        // SYSTEM B (5 måneder)
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

        projektPane.buildTimeline(List.of(p1, p2));

        // =====================================================
        // MEDARBEJDER VIEW
        // =====================================================
        MedarbejderOversigt medarbejderPane = new MedarbejderOversigt();
        //medarbejderPane.setMedarbejdere(List.of(m1, m2, m3));
        //medarbejderPane.setAllokeringer(allokeringer);

        Tab tabMedarbejder = new Tab("Medarbejder oversigt", medarbejderPane);

        // =====================================================
        // BEHOV VIEW
        // =====================================================
        BehovOversigt behovPane = new BehovOversigt();
        behovPane.setProjekter(List.of(p1, p2));

        Tab tabBehov = new Tab("Behov oversigt", behovPane);

        // =====================================================
        // ADD TABS
        // =====================================================
        tabPane.getTabs().addAll(
                tabBehov,
                tabMedarbejder,
                tabProjekt,
                tabTeams
        );

        pane.setCenter(tabPane);

        Scene scene = new Scene(pane, 1200, 650);
        stage.setScene(scene);
        stage.show();
    }
}