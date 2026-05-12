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

        teamPane.setTeams(List.of(t1, t2, t3));
        tabTeams.setContent(teamPane);

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
        List<Allokering> allokeringer = new ArrayList<>();

        Allokering a1 = new Allokering(1, YearMonth.of(2026, 11), 0.8);
        a1.addMedarbejder(m1);

        Allokering a1_2 = new Allokering(1, YearMonth.of(2027, 11), 0.8);
        a1.addMedarbejder(m1);

        Allokering a2 = new Allokering(2, YearMonth.of(2026, 12), 0.6);
        a2.addMedarbejder(m1);

        Allokering a3 = new Allokering(3, YearMonth.of(2027, 1), 0.9);
        a3.addMedarbejder(m1);

        Allokering a4 = new Allokering(4, YearMonth.of(2027, 2), 0.7);
        a4.addMedarbejder(m1);

        Allokering a5 = new Allokering(5, YearMonth.of(2026, 12), 0.5);
        a5.addMedarbejder(m2);

        Allokering a6 = new Allokering(6, YearMonth.of(2027, 1), 0.6);
        a6.addMedarbejder(m2);

        Allokering a7 = new Allokering(7, YearMonth.of(2027, 3), 0.4);
        a7.addMedarbejder(m2);

        Allokering a8 = new Allokering(8, YearMonth.of(2026, 9), 1.0);
        a8.addMedarbejder(m3);

        Allokering a9 = new Allokering(9, YearMonth.of(2026, 10), 1.0);
        a9.addMedarbejder(m3);

        Allokering a10 = new Allokering(10, YearMonth.of(2026, 11), 0.8);
        a10.addMedarbejder(m3);

        Allokering a11 = new Allokering(11, YearMonth.of(2027, 1), 0.6);
        a11.addMedarbejder(m3);

        List<Allokering> allokeringList = List.of(
                a1, a1_2,a2,a3,a4,
                a5,a6,a7,
                a8,a9,a10,a11
        );

        // =====================================================
        // PROJEKTER
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

        a1.setProjekt(p1);
        a2.setProjekt(p1);
        a3.setProjekt(p1);
        a4.setProjekt(p1);

        a5.setProjekt(p2);
        a6.setProjekt(p2);
        a7.setProjekt(p2);

        a8.setProjekt(p4);
        a9.setProjekt(p4);
        a10.setProjekt(p4);
        a11.setProjekt(p4);

        a10.setProjekt(p3);
        a11.setProjekt(p4);

        List<Projekt> projekter = List.of(p1, p2, p3, p4);
        projektPane.buildTimeline(projekter);

        // =====================================================
        // MEDARBEJDER VIEW (IMPORTANT FIX)
        // =====================================================
        MedarbejderOversigt medarbejderPane = new MedarbejderOversigt();

        medarbejderPane.setMedarbejdere(List.of(m1, m2, m3));
        medarbejderPane.setAllokeringer(allokeringList);


        Tab tabMedarbejder = new Tab("Medarbejder oversigt");
        tabMedarbejder.setContent(medarbejderPane);

        // =====================================================
        // BEHOV
        // =====================================================
        Tab tabBehov = new Tab("Behov oversigt");

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

        Scene scene = new Scene(pane, 1100, 600);
        stage.setScene(scene);
        stage.show();
    }
}