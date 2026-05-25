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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Dashboard extends GridPane {
    private final Controller controller = Controller.getInstance();

    private TableView<MedarbejderRow> tvwOversigt;

    private TextField txfSøg;
    private Button btnSøg;
    private Button btnReset;

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

        /*
        =========================
        |       Search bar      |
        =========================
         */

        txfSøg = new TextField();
        txfSøg.setPromptText("Søg på medarbejder navn...");
        txfSøg.setPrefWidth(250);

        btnSøg = new Button("Søg");
        btnReset = new Button("Vis alle");

        btnSøg.setOnAction(e -> {
            String søgeord = txfSøg.getText().trim();
            if (søgeord.isEmpty()) {
                showAlert("Skriv et navn at søge på.");
                return;
            }
            try {
                ArrayList<Medarbejder> resultater = controller.søgMedarbejderNavn(søgeord);
                if (resultater.isEmpty()) {
                    showAlert("Ingen medarbejder fundet med navn: " + søgeord);
                    txfSøg.clear();
                    return;
                }

                ArrayList<Allokering> allokeringer = controller.getAlleAllokeringer();

                tvwOversigt.getItems().clear();

                for (Medarbejder m : resultater) {
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

                    String allokeringsTekst = mine.stream()
                            .map(a ->
                                    a.getStartPeriode() +
                                    " -> " +
                                    a.getSlutPeriode() +
                                    " (" + a.getAndel() + ")"
                            )
                            .collect(Collectors.joining("  |  "));
                    if (allokeringsTekst.isEmpty()) allokeringsTekst = "ikke allokeret";

                    tvwOversigt.getItems().add(new MedarbejderRow(
                            m.getNavn(), team, projekter, allokeringsTekst
                    ));
                }

            } catch (SQLException ex) {
                showAlert("Fejl ved søgning:\n" + ex.getMessage());
            }
            txfSøg.clear();
        });

        btnReset.setOnAction(e -> {
            try {
                ArrayList<Medarbejder> alle = controller.getAlleMedarbejdere();
                ArrayList<Allokering> allokeringer = controller.getAlleAllokeringer();
                tvwOversigt.getItems().clear();
            } catch (Exception ex) {
                showAlert("Fejl: " + ex.getMessage());
            }
            loadData();
            txfSøg.clear();
        });
    }

    /*
    ===================================
    |       LOAD DATA / VIS DATA      |
    ===================================
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
            |       ROW 2 - Search bar      |
            =================================
            */
            HBox searchBox = new HBox(10, new Label("Søg:"), txfSøg, btnSøg, btnReset);
            searchBox.setAlignment(Pos.CENTER_LEFT);
            this.add(searchBox, 0, 2, 3, 1);

            /*
            =================================
            |       ROW 3 - TABLEVIEW       |
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

            this.add(tvwOversigt, 0, 3, 3, 1);
            this.add(crudAllokering(), 0, 4, 3, 1);

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
    |       CRUD ALLOKERING     |
    =============================
     */

    private HBox crudAllokering() {
        Button btnCreate = new Button("Opret Allokering");
        Button btnUpdate = new Button("Rediger Allokering");
        Button btnDelete = new Button("Slet Allokering");
        Button btnTimeLine = new Button("Åben Allokering");

        btnCreate.setOnAction(e -> createAllokering());
        btnUpdate.setOnAction(e -> updateAllokering());
        btnDelete.setOnAction(e -> deleteAllokering());
        btnTimeLine.setOnAction(e -> {
            MedarbejderRow valgt = tvwOversigt.getSelectionModel().getSelectedItem();
            if (valgt == null) {
                showAlert("Vælg en medarbejder i tabellen først");
                return;
            }
            try {
                Medarbejder medarbejder = controller.getAlleMedarbejdere().stream()
                        .filter(m -> m.getNavn().equals(valgt.navn))
                        .findFirst().orElse(null);
                if (medarbejder == null) {
                    showAlert("Kunne ikke finde medarbejderen.");
                    return;
                }
                showTimeLineWindow(medarbejder);
            } catch (SQLException ex) {
                showAlert("Fejl: " + ex.getMessage());
            }
        });

        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().addAll(btnCreate, btnUpdate, btnDelete, btnTimeLine);
        return hBox;
    }

    private void createAllokering() {
        MedarbejderRow valgt = tvwOversigt.getSelectionModel().getSelectedItem();
        if (valgt == null) {
            showAlert("Vælg en medarbejder i tabellen først");
            return;
        }

        Medarbejder medarbejder;
        try {
            medarbejder = controller.getAlleMedarbejdere().stream()
                    .filter(m -> m.getNavn().equals(valgt.navn))
                    .findFirst()
                    .orElse(null);
            if (medarbejder == null) {
                showAlert("Kunne ikke finde medarbejderen.");
                return;
            }
        } catch (SQLException e) {
            showAlert("fejl: " + e.getMessage());
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Opret Allokering");
        dialog.setHeaderText("Ny allokering for: " + medarbejder.getNavn());

        ButtonType btnOpret = new ButtonType("Opret", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuller = new ButtonType("Annuller", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOpret, btnAnnuller);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        ComboBox<Projekt> cbProjekt = new ComboBox<>();
        try {
            cbProjekt.getItems().addAll(controller.getAlleProjekter());
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af projekter: " + e.getMessage());
            return;
        }
        cbProjekt.setPromptText("Vælg projekt");
        cbProjekt.setMaxWidth(Double.MAX_VALUE);

        ComboBox<RessourceBehov> cbBehov = new ComboBox<>();
        cbBehov.setPromptText("Vælg ressourceBehov");
        cbBehov.setMaxWidth(Double.MAX_VALUE);
        cbBehov.setDisable(true);

        cbProjekt.setOnAction(e -> {
            cbBehov.getItems().clear();
            Projekt valgtProjekt = cbProjekt.getValue();
            if (valgtProjekt != null) {
                try {
                    ArrayList<RessourceBehov> alleBehov = controller.getAlleRessourceBehov();
                    List<RessourceBehov> filtreret = alleBehov.stream()
                            .filter(rb -> rb.getProjekt() != null &&
                                    rb.getProjekt().getProjektId() == valgtProjekt.getProjektId())
                            .toList();

                    cbBehov.getItems().addAll(filtreret);
                    cbBehov.setDisable(filtreret.isEmpty());

                    if (filtreret.isEmpty()) {
                        cbBehov.setPromptText("Ingen ressource behov på dette projekt");
                    } else {
                        cbBehov.setPromptText("Vælg ressource behov");
                    }
                } catch (SQLException ex) {
                    showAlert("Fejl ved hentning af ressource behov: " + ex.getMessage());
                }
            }
        });

        TextField txfStart = new TextField();
        txfStart.setPromptText("YYYY-MM");

        TextField txfSlut = new TextField();
        txfSlut.setPromptText("YYYY-MM");

        TextField txfAndel = new TextField();
        txfAndel.setPromptText("0.25, 0.5...");

        form.add(new Label("Projekt"), 0, 0); form.add(cbProjekt, 1, 0);
        form.add(new Label("Ressource Behov"), 0, 1); form.add(cbBehov, 1, 1);
        form.add(new Label("Start periode"), 0, 2); form.add(txfStart, 1, 2);
        form.add(new Label("Slut periode"), 0, 3); form.add(txfSlut, 1, 3);
        form.add(new Label("Andel:"), 0, 4); form.add(txfAndel, 1, 4);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(420);

        Node opretButton = dialog.getDialogPane().lookupButton(btnOpret);
        opretButton.setDisable(true);

        Runnable validateFields = () -> {
            boolean ok = cbProjekt.getValue() != null
                    && !txfStart.getText().isBlank()
                    && !txfSlut.getText().isBlank()
                    && !txfAndel.getText().isBlank();
            opretButton.setDisable(!ok);
        };

        cbProjekt.valueProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        txfStart.textProperty().addListener((observable, oldValue, newValue) ->  validateFields.run());
        txfSlut.textProperty().addListener((observable, oldValue, newValue) ->  validateFields.run());
        txfAndel.textProperty().addListener((observable, oldValue, newValue) ->  validateFields.run());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != btnOpret) return;

        try {
            YearMonth start = YearMonth.parse(txfStart.getText().trim());
            YearMonth slut = YearMonth.parse(txfSlut.getText().trim());
            double andel = Double.parseDouble(txfAndel.getText().trim());
            Projekt projekt = cbProjekt.getValue();
            RessourceBehov behov = cbBehov.getValue();

            if (slut.isBefore(start)) {
                showAlert("Slutperiode må ikke være før startperiode.");
                return;
            }

            if (andel <= 0 || andel > 1) {
                showAlert("Andel skal være mellem 0 og 1");
                return;
            }

            int behovId = behov != null ? behov.getBehovId() : -1;

            int næsteId = næsteAllokeringsId();

             controller.createAllokering(
                     næsteId,
                     start,
                     slut,
                     andel,
                     medarbejder.getMedId(),
                     projekt.getProjektId(),
                     behovId
             );

             loadData();

        } catch (DateTimeParseException e) {
            showAlert("Ugyldig dataformat. Brug YYYY-MM, fx 2025-01.");
        } catch (NumberFormatException e) {
            showAlert("Andel skal være et tal, fx 0.5");
        } catch (SQLException e) {
            showAlert("Fejl ved oprettelse: " + e.getMessage());
        }
    }

    private void updateAllokering() {
        MedarbejderRow valgt = tvwOversigt.getSelectionModel().getSelectedItem();
        if (valgt == null) {
            showAlert("Vælg en medarbejder i tabellen først");
            return;
        }

        Medarbejder medarbejder;
        Allokering eksisterende;
        ArrayList<Projekt> projekter;

        try {
            medarbejder = controller.getAlleMedarbejdere().stream()
                    .filter(m -> m.getNavn().equals(valgt.navn))
                    .findFirst()
                    .orElse(null);
            if (medarbejder == null) {
                showAlert("Kunne ikke finde medarbejderen.");
                return;
            }

            List<Allokering> mineAllokeringer = controller.getAlleAllokeringer().stream()
                    .filter(a -> a.getMedarbejdere().stream()
                            .anyMatch(m -> m.getMedId() == medarbejder.getMedId()))
                    .toList();

            if (mineAllokeringer.isEmpty()) {
                showAlert(medarbejder.getNavn() + " har ingen allokeringer at redigere.");
                return;
            }

            eksisterende = mineAllokeringer.getFirst();
            projekter = controller.getAlleProjekter();

        } catch (SQLException e) {
            showAlert("fejl: " + e.getMessage());
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Rediger Allokering");
        dialog.setHeaderText("Rediger allokering for: " + medarbejder.getNavn());

        ButtonType btnGem = new ButtonType("Gem", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuller = new ButtonType("Annuller", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGem, btnAnnuller);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        ComboBox<Allokering> cbAllokering = new ComboBox<>();
        try {
            cbAllokering.getItems().addAll(
                    controller.getAlleAllokeringer().stream()
                            .filter(a -> a.getMedarbejdere().stream()
                                    .anyMatch(m -> m.getMedId() == medarbejder.getMedId()))
                            .toList()
            );
        } catch (SQLException e) {
            showAlert("Fejl: " + e.getMessage());
            return;
        }

        cbAllokering.setValue(eksisterende);
        cbAllokering.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Projekt> cbProjekt = new ComboBox<>();
        cbProjekt.getItems().addAll(projekter);
        cbProjekt.setValue(eksisterende.getProjekt());
        cbProjekt.setMaxWidth(Double.MAX_VALUE);

        ComboBox<RessourceBehov> cbBehov = new ComboBox<>();
        cbBehov.setPromptText("Valgfrit");
        cbBehov.setMaxWidth(Double.MAX_VALUE);

        Runnable opdaterBehov = () -> {
            cbBehov.getItems().clear();
            Projekt p = cbProjekt.getValue();
            if (p != null) {
                try {
                    ArrayList<RessourceBehov> alleBehov = controller.getAlleRessourceBehov();
                    List<RessourceBehov> filtreret = alleBehov.stream()
                            .filter(rb -> rb.getProjekt() != null &&
                                    rb.getProjekt().getProjektId() == p.getProjektId())
                            .toList();

                    cbBehov.getItems().addAll(filtreret);

                    Allokering aktuel = cbAllokering.getValue() != null ? cbAllokering.getValue() : eksisterende;
                    RessourceBehov aktivBehov = aktuel.getRessourceBehov();
                    if (aktivBehov != null &&
                    aktivBehov.getProjekt() != null &&
                    aktivBehov.getProjekt().getProjektId() == p.getProjektId()) {
                        cbBehov.setValue(aktivBehov);
                    }

                } catch (SQLException ex) {
                    showAlert("Fejl ved hentning af ressourcebehov: " + ex.getMessage());
                }
            }
        };

        opdaterBehov.run();

        cbProjekt.setOnAction(e -> opdaterBehov.run());

        TextField txfStart = new TextField(eksisterende.getStartPeriode().toString());
        TextField txfSlut = new TextField(eksisterende.getSlutPeriode().toString());
        TextField txfAndel = new TextField(String.valueOf(eksisterende.getAndel()));

        cbAllokering.setOnAction(e -> {
            Allokering valgtAllokering = cbAllokering.getValue();
            if (valgtAllokering == null) return;
            cbProjekt.setValue(valgtAllokering.getProjekt());
            txfStart.setText(valgtAllokering.getStartPeriode().toString());
            txfSlut.setText(valgtAllokering.getSlutPeriode().toString());
            txfAndel.setText(String.valueOf(valgtAllokering.getAndel()));
            opdaterBehov.run();
        });

        form.add(new Label("Allokering:"), 0, 0); form.add(cbAllokering, 1, 0);
        form.add(new Label("Projekt:"), 0, 1); form.add(cbProjekt, 1, 1);
        form.add(new Label("Ressource behov:"), 0, 2); form.add(cbBehov, 1, 2);
        form.add(new Label("Start periode:"), 0, 3); form.add(txfStart, 1, 3);
        form.add(new Label("Slut periode"), 0, 4); form.add(txfSlut, 1, 4);
        form.add(new Label("Andel:"), 0, 5); form.add(txfAndel, 1, 5);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(450);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != btnGem) return;

        try {
            Allokering valgtAllokering = cbAllokering.getValue();
            YearMonth start = YearMonth.parse(txfStart.getText().trim());
            YearMonth slut = YearMonth.parse(txfSlut.getText().trim());
            double andel = Double.parseDouble(txfAndel.getText().trim());
            Projekt projekt = cbProjekt.getValue();
            RessourceBehov behov = cbBehov.getValue();

            if (projekt == null) {
                showAlert("Vælg venligst et projekt.");
                return;
            }

            if (slut.isBefore(start)) {
                showAlert("Slut periode må ikke være før start periode");
                return;
            }

            if (andel <= 0 || andel > 1) {
                showAlert("Andel skal være mellem 0 og 1.");
                return;
            }

            int behovId = behov != null ? behov.getBehovId() : -1;

            controller.updateAllokering(
                    valgtAllokering.getAllokeringsId(),
                    start,
                    slut,
                    andel,
                    medarbejder.getMedId(),
                    projekt.getProjektId(),
                    behovId
            );

            loadData();

        } catch (DateTimeParseException e) {
            showAlert("Ugyldigt datoformat. Brug YYYY-MM, fx 2025-01");
        } catch (NumberFormatException e) {
            showAlert("Andel skal være et tal, fx 0.5");
        } catch (SQLException e) {
            showAlert("Fejl ved opdatering: " + e.getMessage());
        }
    }

    private void deleteAllokering() {
        MedarbejderRow valgt = tvwOversigt.getSelectionModel().getSelectedItem();
        if (valgt == null) {
            showAlert("Vælg en medarbejder i tabellen først.");
            return;
        }

        Medarbejder medarbejder;
        List<Allokering> mineAllokeringer;

        try {
            medarbejder = controller.getAlleMedarbejdere().stream()
                    .filter(m -> m.getNavn().equals(valgt.navn))
                    .findFirst()
                    .orElse(null);

            if (medarbejder == null) {
                showAlert("Kunne ikke finde medarbejderen.");
                return;
            }

            mineAllokeringer = controller.getAlleAllokeringer().stream()
                    .filter(a -> a.getMedarbejdere().stream()
                            .anyMatch(m -> m.getMedId() == medarbejder.getMedId()))
                    .toList();

            if (mineAllokeringer.isEmpty()) {
                showAlert(medarbejder.getNavn() + " har ingen allokeringer at slette.");
                return;
            }

        } catch (SQLException e) {
            showAlert("Fejl: " + e.getMessage());
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Slet Allokering");
        dialog.setHeaderText("Slet allokering for: " + medarbejder.getNavn());

        ButtonType btnSlet = new ButtonType("Slet", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuller = new ButtonType("Annuller", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSlet, btnAnnuller);

        dialog.getDialogPane().lookupButton(btnSlet)
                .setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        ComboBox<Allokering> cbAllokering = new ComboBox<>();
        cbAllokering.getItems().addAll(mineAllokeringer);
        cbAllokering.setValue(mineAllokeringer.getFirst());
        cbAllokering.setMaxWidth(Double.MAX_VALUE);

        Label lblProjekt = new Label();
        Label lblPeriode = new Label();
        Label lblAndel   = new Label();

        Runnable opdaterPreview = () -> {
            Allokering a = cbAllokering.getValue();
            if (a == null) return;
            lblProjekt.setText(a.getProjekt() != null ? a.getProjekt().getNavn() : "-");
            lblPeriode.setText(a.getStartPeriode() + " → " + a.getSlutPeriode());
            lblAndel.setText(String.valueOf(a.getAndel()));
        };

        opdaterPreview.run();
        cbAllokering.setOnAction(e -> opdaterPreview.run());

        form.add(new Label("Allokering:"), 0, 0); form.add(cbAllokering, 1, 0);
        form.add(new Label("Projekt:"),    0, 1); form.add(lblProjekt,   1, 1);
        form.add(new Label("Periode:"),    0, 2); form.add(lblPeriode,   1, 2);
        form.add(new Label("Andel:"),      0, 3); form.add(lblAndel,     1, 3);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(400);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != btnSlet) return;

        try {
            Allokering valgtAllokering = cbAllokering.getValue();

            controller.deleteAllokering(valgtAllokering.getAllokeringsId());

            loadData();

        } catch (SQLException e) {
            showAlert("Fejl ved sletning: " + e.getMessage());
        }
    }

    // ==========================================
    // BUILD TIMELINE
    // ==========================================
    private void showTimeLineWindow(Medarbejder medarbejder) {
        Stage stage = new Stage();
        stage.setTitle("Timeline - " + medarbejder.getNavn());

        GridPane timelineGrid = new GridPane();
        timelineGrid.setHgap(2);
        timelineGrid.setVgap(2);

        ScrollPane scrollPane = new ScrollPane(timelineGrid);
        scrollPane.setPrefHeight(300);
        scrollPane.setPrefWidth(800);

        try {
            buildTimeLine(medarbejder, timelineGrid, controller.getAlleAllokeringer());
        } catch (SQLException e) {
            showAlert("Fejl ved hentning af allokeringer: " + e.getMessage());
            return;
        }

        stage.setScene(new Scene(scrollPane));
        stage.show();
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

    private void buildTimeLine(Medarbejder medarbejder, GridPane timelineGrid, List<Allokering> allokeringer) {
        timelineGrid.getChildren().clear();

        List<Allokering> relevant = allokeringer.stream()
                .filter(a -> a.getMedarbejdere().stream()
                        .anyMatch(m -> m.getMedId() == medarbejder.getMedId()))
                .toList();

        if (relevant.isEmpty()) {
            timelineGrid.add(new Label("Ingen allokeringer fundet."), 0, 0);
            return;
        }

        YearMonth min = null, max = null;
        for (Allokering a : relevant) {
            if (a.getStartPeriode() == null || a.getSlutPeriode() == null) continue;
            if (min == null || a.getStartPeriode().isBefore(min)) min = a.getStartPeriode();
            if (max == null || a.getSlutPeriode().isAfter(max)) max = a.getSlutPeriode();
        }
        if (min == null) return;

        YearMonth current = min;
        int col = 1, lastYear = -1, lastQuarter = -1;
        while (!current.isAfter(max)) {
            int year = current.getYear();
            int quarter = ((current.getMonthValue() - 1) / 3) + 1;

            if (year != lastYear) {
                Label lbl = new Label(String.valueOf(year));
                lbl.setMinSize(80, 25); lbl.setAlignment(Pos.CENTER);
                lbl.setStyle("-fx-font-weight: bold; -fx-border-color: black; -fx-background-color: #dddddd;");
                timelineGrid.add(lbl, col, 0);
                lastYear = year; quarter = -1;
            }

            if (quarter != lastQuarter) {
                Label lbl = new Label("Q" + quarter);
                lbl.setMinSize(80, 25); lbl.setAlignment(Pos.CENTER);
                lbl.setStyle("-fx-font-weight: bold; -fx-background-color: gray; -fx-background-color: #eeeeee");
                timelineGrid.add(lbl, col, 1);
                lastQuarter = quarter;
            }
            Label lbl = new Label(current.getMonth().name().substring(0, 3));
            lbl.setMinSize(80, 25); lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-border-color: lightgray;");
            timelineGrid.add(lbl, col, 2);

            current = current.plusMonths(1);
            col++;
        }

        int row = 3;
        List<Projekt> projekter = relevant.stream()
                .map(Allokering::getProjekt).filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Projekt::getProjektId, p -> p, (a, b) -> a))
                .values().stream().toList();

        for (Projekt projekt : projekter) {
            Label projektLabel = new Label(projekt.getNavn());
            projektLabel.setMinSize(120, 30);
            projektLabel.setStyle("-fx-font-weight: bold; -fx-border-color: black;");
            timelineGrid.add(projektLabel, 0, row);

            List<Allokering> projektAllokeringer = relevant.stream()
                    .filter(a -> a.getProjekt() != null
                    && a.getProjekt().getProjektId() == projekt.getProjektId())
                    .toList();

            current = min; col = 1;
            while (!current.isAfter(max)) {
                final YearMonth cm = current;
                Label cell = new Label();
                cell.setMinSize(80, 30);
                cell.setAlignment(Pos.CENTER);

                projektAllokeringer.stream()
                        .filter(a -> a.getStartPeriode() != null && a.getSlutPeriode() != null
                        && !cm.isBefore(a.getStartPeriode()) && !cm.isAfter(a.getSlutPeriode()))
                        .findFirst()
                        .ifPresentOrElse(
                                a -> { cell.setText(String.valueOf(a.getAndel()));
                                cell.setStyle("-fx-background-color: #dee2e6; -fx-border-color: black;") ;},
                                () -> cell.setStyle("-fx-border-color: lightgray;")
                        );

                timelineGrid.add(cell, col, row);
                current = current.plusMonths(1);
                col++;
            }
            row++;
        }
    }

    private int næsteAllokeringsId() throws SQLException {
        ArrayList<Allokering> alle = controller.getAlleAllokeringer();
        return alle.stream()
                .mapToInt(Allokering::getAllokeringsId)
                .max()
                .orElse(0) + 1;
    }

    private void showAlert(String besked) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel");
        alert.setHeaderText(null);
        alert.setContentText(besked);
        alert.showAndWait();
    }

}
