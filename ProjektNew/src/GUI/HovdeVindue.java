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

        tabTeams.setContent(teamPane);

        // =====================================================
        // MEDARBEJDERE
        // =====================================================
        Medarbejder m1 = new Medarbejder(1, "JH", "Jonas Hansen",
                MedarbejderType.INTERN, "Developer", false, null, null, t1);

        Medarbejder m2 = new Medarbejder(2, "MK", "Mette Kristensen",
                MedarbejderType.INTERN, "Tester", false, null, null, t2);

        Medarbejder m3 = new Medarbejder(3, "AB", "Anders Berg",
                MedarbejderType.EKSTERN, "Architect", false, null, null, t3);

        t1.addMedarbejder(m1);
        t2.addMedarbejder(m2);
        t3.addMedarbejder(m3);

        // =====================================================
        // ALLOKERINGER
        // =====================================================
        Allokering a1 = new Allokering(1, YearMonth.of(2026, 11), 0.8);
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

        List<Allokering> allokeringer = List.of(
                a1, a2, a3, a4,
                a5, a6, a7,
                a8, a9, a10, a11
        );

        // =====================================================
        // PROJEKTER
        // =====================================================
        Projekt p1 = new Projekt(1, "System A");
        Projekt p2 = new Projekt(2, "System B");

        // (valgfrit hvis du bruger behov senere)
        p1.addRessourceBehov(new RessourceBehov(1, "Dev", YearMonth.of(2026, 9), 2, 850, ØkonomiType.CAPEX));
        p2.addRessourceBehov(new RessourceBehov(2, "Test", YearMonth.of(2026, 10), 1, 700, ØkonomiType.OPEX));

        projektPane.buildTimeline(List.of(p1, p2));

        // =====================================================
        // MEDARBEJDER VIEW  (VIGTIG FIX HER)
        // =====================================================
        MedarbejderOversigt medarbejderPane = new MedarbejderOversigt();
        medarbejderPane.setMedarbejdere(List.of(m1, m2, m3));
        medarbejderPane.setAllokeringer(allokeringer);

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