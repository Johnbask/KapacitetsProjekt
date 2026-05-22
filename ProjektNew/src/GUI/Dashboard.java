package GUI;

import Controller.Controller;
import Model.Allokering;
import Model.Enum.MedarbejderType;
import Model.Medarbejder;
import Model.Projekt;
import Model.RessourceBehov;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Dashboard extends GridPane {
    private final Controller controller = Controller.getInstance();

    private TableView<MedarbejderRow> tvwOversigt;

    public Dashboard() {
        initContent();
        loadData();
    }

    public void initContent() {
        setPadding(new Insets(20));
        setHgap(15);
        setVgap(15);

        /*
        =========================
        |       TABLEVIEW       |
        =========================
         */
        tvwOversigt = new TableView<>();
        tvwOversigt.setPrefHeight(300);
        tvwOversigt.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<MedarbejderRow, String> colNavn = new TableColumn<>("Medarbejder");
        colNavn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().navn));
        colNavn.setPrefWidth(150);

        TableColumn<MedarbejderRow, String> colTeam = new TableColumn<>("Team");
        colTeam.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().team));
        colTeam.setPrefWidth(100);

        TableColumn<MedarbejderRow, String> colProjekt = new TableColumn<>("Projekter");
        colProjekt.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().projekter));
        colProjekt.setPrefWidth(150);

        TableColumn<MedarbejderRow, String> colAllokering = new TableColumn<>("Allokeringer");
        colAllokering.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().allokeringer));
        colAllokering.setPrefWidth(300);

        tvwOversigt.getColumns().addAll(colNavn, colTeam, colProjekt, colAllokering);


    }

    /*
    =========================
    |       LOAD DATA       |
    =========================
    */
    public void loadData() {
        getChildren().clear();
        getColumnConstraints().clear();

        try {
            ArrayList<Medarbejder> medarbejdere = controller.getAlleMedarbejdere();
            ArrayList<Allokering> allokeringer = controller.getAlleAllokeringer();
            ArrayList<RessourceBehov> behov = controller.getAlleRessourceBehov();
            int antalProjekter = controller.getAlleProjekter().size();

            // Intern / ekstern
            long intern = medarbejdere.stream()
                    .filter(m -> m.getType() == MedarbejderType.INTERN).count();
            long ekstern = medarbejdere.stream()
                    .filter(m -> m.getType() == MedarbejderType.EKSTERN).count();

            // Allokerede medarbejdere
            Set<Integer> allokeredeIDs = new HashSet<>();
            for (Allokering a : allokeringer) {
                for (Medarbejder m : a.getMedarbejdere()) {
                    allokeredeIDs.add(m.getMedId());
                }
            }

            int allokerede = allokeredeIDs.size();
            int ikkeAllokerede = medarbejdere.size() - allokerede;

            // Ressourcebehov uden allokering
            Set<Integer> behovMedAllokering = new HashSet<>();
            for (Allokering a : allokeringer) {
                if (a.getRessourceBehov() != null) {
                    behovMedAllokering.add(a.getRessourceBehov().getBehovId());
                }
            }

            long udækkedeBehov = behov.stream()
                    .filter(rb -> !behovMedAllokering.contains(rb.getBehovId()))
                    .count();

            // Row 0 - stats korte
            this.add(statKorte("Medarbejdere", String.valueOf(medarbejdere.size()),
                    intern + " interne  .  " + ekstern + " eksterne", "#2196F3"), 0, 0);
            this.add(statKorte("Projekter", String.valueOf(antalProjekter),
                    "Aktive projekter", "#4CAF50"), 1, 0);
            this.add(statKorte("Allokerede", String.valueOf(allokerede),
                    ikkeAllokerede + " ikke allokerede", "#FF9800"), 2, 0);

            // Row 1 - alert eller ok
            if (udækkedeBehov > 0) {
                this.add(alertKort(udækkedeBehov), 0, 1, 3, 1);
            } else {
                Label ok = new Label("✅ alle ressourcebehov har allokering");
                ok.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 13px;");
                this.add(ok, 0, 1, 3, 1);
            }

            /*
            =================================
            |       ROW 2 - TABLEVIEW       |
            =================================
            */
            tvwOversigt.getItems().clear();

            for (Medarbejder m : medarbejdere) {
                List<Allokering> mine = allokeringer.stream()
                        .filter(a -> a.getMedarbejdere().stream()
                                .anyMatch(am -> am.getMedId() == m.getMedId()))
                        .sorted(Comparator.comparing(Allokering::getStartPeriode))
                        .toList();

                String team = m.getTeam() != null ? m.getTeam().getNavn() : "-";

                String projekter = mine.stream()
                        .map(Allokering::getProjekt)
                        .filter(Objects::nonNull)
                        .map(Projekt::getNavn)
                        .distinct()
                        .collect(Collectors.joining(", "));
                if (projekter.isEmpty()) projekter = "-";

                String allokeringTekst = mine.stream()
                        .map(a ->
                                a.getStartPeriode() +
                                " → " +
                                a.getSlutPeriode() +
                                " (" + a.getAndel() + ")"
                        )
                        .collect(Collectors.joining("  |  "));
                if (allokeringTekst.isEmpty()) allokeringTekst = "ikke allokeret";

                tvwOversigt.getItems().add(new MedarbejderRow(
                        m.getNavn(), team, projekter, allokeringTekst
                ));
            }

            this.add(tvwOversigt, 0, 2, 3, 1);


            for (int i = 0; i < 3; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setHgrow(Priority.ALWAYS);
                cc.setPercentWidth(33.3);
                getColumnConstraints().add(cc);
            }

        } catch (SQLException e) {
            showAlert("Fejl ved indlæsning af dashboard: " + e.getMessage());
        }
    }

    private VBox statKorte(String title, String value, String subtitle, String color) {
        VBox kort = new VBox(4);
        kort.setPadding(new Insets(15));
        kort.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 0 0 0 4px;" +
                        "-fx-border-radius: 4px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label lblSubtitle = new Label(subtitle);
        lblSubtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaaaaa;");

        kort.getChildren().addAll(lblTitle, lblValue, lblSubtitle);
        return kort;
    }

    private HBox alertKort(long antal) {
        HBox kort = new HBox(12);
        kort.setPadding(new Insets(12, 15, 12, 15));
        kort.setAlignment(Pos.CENTER_LEFT);
        kort.setStyle(
                "-fx-background-color: #FFF3E0;" +
                        "-fx-border-color: #FF9800;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 4px;" +
                        "-fx-background-radius: 4px;"
        );

        Label icon = new Label("⚠");
        icon.setStyle("-fx-font-size: 18px; -fx-text-fill: #FF9800;");

        Label msg = new Label(antal + " ressourcebehov mangler allokering");
        msg.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E65100;");

        kort.getChildren().addAll(icon, msg);
        return kort;
    }

    /*
    =============================
    |       HJÆLPEKLASSE        |
    =============================
     */

    private static class MedarbejderRow {
        final String navn;
        final String team;
        final String projekter;
        final String allokeringer;

        MedarbejderRow(String navn, String team, String projekter, String allokeringer) {
            this.navn = navn;
            this.team = team;
            this.projekter = projekter;
            this.allokeringer = allokeringer;
        }
    }

    /*
    =============================
    |       HJÆLPEMETODER       |
    =============================
     */
    private void showAlert(String besked) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel");
        alert.setHeaderText(null);
        alert.setContentText(besked);
        alert.showAndWait();
    }

}
