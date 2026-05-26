package GUI;

import Controller.Controller;
import Model.*;
import Model.Enum.MedarbejderType;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

            Button btnAfdelinger = new Button("Afdelinger");
            Button btnOrganisationer = new Button("Organisationer");
            btnAfdelinger.setOnAction(e -> {
                try {
                    afdelingWindow();
                } catch (SQLException ex) {
                    showAlert("Fejl: " + ex.getMessage());
                }
            });
            btnOrganisationer.setOnAction(e -> {
                try {
                    organisationWindow();
                } catch (SQLException ex) {
                    showAlert("Fejl: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            /*
            =================================
            |       ROW 2 - Search bar      |
            =================================
            */
            HBox searchBox = new HBox(10, new Label("Søg:"), txfSøg, btnSøg, btnReset);
            searchBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(searchBox, Priority.ALWAYS);

            HBox afdOrgBtns = new HBox(10, btnAfdelinger, btnOrganisationer);
            afdOrgBtns.setAlignment(Pos.CENTER_RIGHT);

            HBox row2 = new HBox(20, searchBox, afdOrgBtns);
            row2.setAlignment(Pos.CENTER_LEFT);
            this.add(row2, 0, 2, 3, 1);

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
        Button btnSøgLedighed = new Button("Søg Ledighed");

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

        btnSøgLedighed.setOnAction(e -> {
            try {
                openLedighedWindow();
            } catch (SQLException ex) {
                showAlert("Fejl ved button søgLedighed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().addAll(btnCreate, btnUpdate, btnDelete, btnTimeLine, btnSøgLedighed);
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
    |       AFDELING WINDOW     |
    =============================
     */
    private void afdelingWindow() throws SQLException {
        Stage stage = new Stage();
        stage.setTitle("Afdelinger");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        TableView<Afdeling> tvwAfdeling = new TableView<>();
        tvwAfdeling.setPrefSize(500, 280);

        TableColumn<Afdeling, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("afdId"));
        colId.setPrefWidth(60);

        TableColumn<Afdeling, String> colNavn = new TableColumn<>("Navn");
        colNavn.setCellValueFactory(new PropertyValueFactory<>("navn"));
        colNavn.setPrefWidth(200);

        TableColumn<Afdeling, String> colLeder = new TableColumn<>("Leder");
        colLeder.setCellValueFactory(new PropertyValueFactory<>("leder"));
        colLeder.setPrefWidth(200);

        tvwAfdeling.getColumns().addAll(colId, colNavn, colLeder);
        tvwAfdeling.getItems().setAll(controller.getAlleAfdelinger());
        root.setCenter(tvwAfdeling);

        GridPane form = new GridPane();
        form.setPadding(new Insets(10, 0, 6, 0));
        form.setHgap(10);
        form.setVgap(8);

        TextField txfId = new TextField(); txfId.setPromptText("ID");
        TextField txfNavn = new TextField(); txfNavn.setPromptText("Navn");
        TextField txfLeder = new TextField(); txfLeder.setPromptText("Leder");

        form.add(new Label("ID: "), 0, 0); form.add(txfId, 1, 0);
        form.add(new Label("Navn:"), 0, 1); form.add(txfNavn, 1, 1);
        form.add(new Label("Leder:"), 0, 2); form.add(txfLeder, 1, 2);

        tvwAfdeling.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txfId.setText(String.valueOf(newValue.getAfdId()));
                txfNavn.setText(newValue.getNavn());
                txfLeder.setText(newValue.getLeder());
            }
        });

        Button btnCreate = new Button("Opret Afdeling");
        Button btnEdit = new Button("Rediger Afdeling");
        Button btnDelete = new Button("Slet Afdeling");

        btnCreate.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txfId.getText().trim());
                Afdeling ny = controller.createAfdeling(id, txfNavn.getText().trim(), txfLeder.getText().trim());
                tvwAfdeling.getItems().add(ny);
                txfId.clear(); txfNavn.clear(); txfLeder.clear();
            } catch (NumberFormatException ex) {
                showAlert("ID skal være et heltal.");
            } catch (SQLException ex) {
                showAlert("Fejl: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnEdit.setOnAction(e -> {
            Afdeling valgt = tvwAfdeling.getSelectionModel().getSelectedItem();
            if (valgt == null) {
                showAlert("Vælg en afdeling først.");
                return;
            }
            try {
                int id = Integer.parseInt(txfId.getText().trim());
                Afdeling ny = controller.updateAfdeling(id, txfNavn.getText().trim(), txfLeder.getText().trim());
                tvwAfdeling.getItems().add(ny);
                txfId.clear(); txfNavn.clear(); txfLeder.clear();
            } catch (NumberFormatException ex) {
                showAlert("ID skal være et heltal.");
            } catch (SQLException ex) {
                showAlert("Fejl: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnDelete.setOnAction(e -> {
            Afdeling valgt = tvwAfdeling.getSelectionModel().getSelectedItem();
            if (valgt == null) {
                showAlert("Vælg en afdeling først.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Slet '" + valgt.getNavn() + "'?", ButtonType.OK, ButtonType.CANCEL);
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.OK) {
                    try {
                        controller.deleteAfdeling(valgt.getAfdId());
                    } catch (SQLException ex) {
                        showAlert("Fejl: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    tvwAfdeling.getItems().remove(valgt);
                    txfId.clear(); txfNavn.clear(); txfLeder.clear();
                }
            });
        });

        HBox hBox = new HBox(10, btnCreate, btnEdit, btnDelete);
        hBox.setPadding(new Insets(6, 0, 0, 0));

        VBox bottom = new VBox(4, form, hBox);
        root.setBottom(bottom);

        stage.setScene(new Scene(root, 540, 460));
        stage.showAndWait();
    }

    /*
    =================================
    |       ORGANISATION WINDOW     |
    =================================
     */
    private void organisationWindow() throws SQLException {
        Stage stage = new Stage();
        stage.setTitle("Organisationer");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        TableView<Organisation> tvwOrganisation = new TableView<>();
        tvwOrganisation.setPrefSize(500, 280);

        TableColumn<Organisation, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("orgId"));
        colId.setPrefWidth(60);

        TableColumn<Organisation, String> colNavn = new TableColumn<>("Navn");
        colNavn.setCellValueFactory(new PropertyValueFactory<>("navn"));
        colNavn.setPrefWidth(400);

        tvwOrganisation.getColumns().addAll(colId, colNavn);
        tvwOrganisation.getItems().setAll(controller.getAlleOrganisationer());
        root.setCenter(tvwOrganisation);

        GridPane form = new GridPane();
        form.setPadding(new Insets(10, 0, 6, 0));
        form.setHgap(10); form.setVgap(8);

        TextField txfId = new TextField(); txfId.setPromptText("ID");
        TextField txfNavn = new TextField(); txfNavn.setPromptText("Navn");

        form.add(new Label("orgId:"), 0, 0); form.add(txfId, 1, 0);
        form.add(new Label("Navn:"), 0, 1); form.add(txfNavn, 1, 1);

        tvwOrganisation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txfId.setText(String.valueOf(newValue.getOrgId()));
                txfNavn.setText(newValue.getNavn());
            }
        });

        Button btnCreate = new Button("Opret Organisation");
        Button btnEdit = new Button("Rediger Organisation");
        Button btnDelete = new Button("Slet Organisation");

        btnCreate.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txfId.getText().trim());
                Organisation ny = controller.createOrganisation(id, txfNavn.getText().trim());
                tvwOrganisation.getItems().add(ny);
                txfId.clear(); txfNavn.clear();
            } catch (NumberFormatException ex) {
                showAlert("ID skal være et heltal.");
            } catch (SQLException ex) {
                showAlert("Fejl: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnEdit.setOnAction(e -> {
            Organisation valgt = tvwOrganisation.getSelectionModel().getSelectedItem();
            if (valgt == null) {
                showAlert("Vælg en organisation først.");
                return;
            }

            try {
                int id = Integer.parseInt(txfId.getText().trim());
                Organisation ny = controller.updateOrganisation(id, txfNavn.getText().trim());
                tvwOrganisation.getItems().add(ny);
                txfId.clear(); txfNavn.clear();
            } catch (NumberFormatException ex) {
                showAlert("ID skal være et heltal.");
            } catch (SQLException ex) {
                showAlert("Fejl: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnDelete.setOnAction(e -> {
            Organisation valgt = tvwOrganisation.getSelectionModel().getSelectedItem();
            if (valgt == null) {
                showAlert("Vælg en organisation først.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Slet '" + valgt.getNavn() + "'?", ButtonType.OK, ButtonType.CANCEL);
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.OK) {
                    try {
                        controller.deleteOrganisation(valgt.getOrgId());
                    } catch (SQLException ex) {
                        showAlert("Fejl: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    tvwOrganisation.getItems().remove(valgt);
                    txfId.clear(); txfNavn.clear();
                }
            });


        });

        HBox btnBox = new HBox(10, btnCreate, btnEdit, btnDelete);
        btnBox.setPadding(new Insets(6, 0, 0, 0));

        VBox bottom = new VBox(4, form, btnBox);
        root.setBottom(bottom);

        stage.setScene(new Scene(root, 520, 420));
        stage.showAndWait();
    }

        /*
    =================================
    |       SØG LEDIGHED WINDOW     |
    =================================
     */

    private void openLedighedWindow() throws SQLException {
        Stage stage = new Stage();
        stage.setTitle("Søg Ledighed");
        stage.setWidth(900);
        stage.setHeight(700);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        /*
        =================================
        |       TOP: SEARCH SECTION     |
        =================================
         */

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(0, 0, 10, 0));

        HBox searchBox = new HBox(10);
        TextField txfSøg = new TextField();
        txfSøg.setPromptText("Søg efter medarbejder navn...");
        txfSøg.setPrefWidth(300);
        Button btnSøg = new Button("Søg");
        Button btnVisAlle = new Button("Vis alle");
        searchBox.getChildren().addAll(new Label("Søg:"), txfSøg, btnSøg, btnVisAlle);

        topBox.getChildren().add(searchBox);
        root.setTop(topBox);

        /*
        =========================================================
        |       CENTER: TABLEVIEW FOR LEDIGE MEDARBEJDERE       |
        =========================================================
         */

        TableView<LedigMedarbejderRow> tvwLedige = new TableView<>();
        tvwLedige.setPrefHeight(400);

        TableColumn<LedigMedarbejderRow, String> colNavn = new TableColumn<>("Medarbejder");
        colNavn.setCellValueFactory(new PropertyValueFactory<>("navn"));
        colNavn.setPrefWidth(200);

        TableColumn<LedigMedarbejderRow, String> colStilling = new TableColumn<>("Stilling");
        colStilling.setCellValueFactory(new PropertyValueFactory<>("stilling"));
        colStilling.setPrefWidth(200);

        TableColumn<LedigMedarbejderRow, Double> colLedighed = new TableColumn<>("Ledighed");
        colLedighed.setCellValueFactory(new PropertyValueFactory<>("ledighed"));
        colLedighed.setPrefWidth(150);
        colLedighed.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item > 0.0) {
                    setText(String.format("%.2f", item));
                } else {
                    setText(String.format("Ingen ledighed"));
                }
            }
        });

        tvwLedige.getColumns().addAll(colNavn, colStilling, colLedighed);
        root.setCenter(tvwLedige);

        /*
        =====================================================
        |       BUTTON: FORM FOR CREATING ALLOCATION        |
        =====================================================
         */
        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(10, 0, 0, 0));

        // Projekt comboBox
        ComboBox<Projekt> cbProjekt = new ComboBox<>();
        cbProjekt.getItems().addAll(controller.getAlleProjekter());
        cbProjekt.setPromptText("Vælg projekt");
        cbProjekt.setPrefWidth(200);

        // RessourceBehov ComboBox
        ComboBox<RessourceBehov> cbBehov = new ComboBox<>();
        cbBehov.setPromptText("Vælg ressource behov");
        cbBehov.setPrefWidth(200);
        cbBehov.setDisable(true);

        // Update ressource behov når projekt er valgt
        cbProjekt.setOnAction(e -> {
            cbBehov.getItems().clear();
            Projekt valgtProjekt = cbProjekt.getValue();
            if (valgtProjekt != null) {
                List<RessourceBehov> filtreret = null;
                try {
                    filtreret = controller.getAlleRessourceBehov().stream()
                            .filter(rb -> rb.getProjekt() != null &&
                                    rb.getProjekt().getProjektId() == valgtProjekt.getProjektId())
                            .toList();
                } catch (SQLException ex) {
                    showAlert("Fejl ved valg af projekt: " + ex.getMessage());
                    ex.printStackTrace();
                    return;
                }

                cbBehov.getItems().addAll(filtreret);
                cbBehov.setDisable(filtreret.isEmpty());
                cbBehov.setPromptText(filtreret.isEmpty() ? "Ingen ressource behov" : "Vælg ressource behov");
            }
        });

        // Start periode
        TextField txfStart = new TextField();
        txfStart.setPromptText("YYYY-MM");
        txfStart.setPrefWidth(120);

        // Slut periode
        TextField txfSlut = new TextField();
        txfSlut.setPromptText("YYYY-MM");
        txfSlut.setPrefWidth(120);

        // Andel
        TextField txfAndel = new TextField();
        txfAndel.setPrefWidth(100);

        form.add(new Label("Projekt:"), 0, 0); form.add(cbProjekt, 1, 0);
        form.add(new Label("Ressource Behov:"), 2, 0); form.add(cbBehov, 3, 0);
        form.add(new Label("Start periode:"), 0, 1); form.add(txfStart, 1, 1);
        form.add(new Label("Slut periode"), 2, 1); form.add(txfSlut, 3, 1);

        form.add(new Label("Andel"), 0, 2); form.add(txfAndel, 1, 2);

        // Buttons
        Button btnTildel = new Button("Tildel Allokering");
        Button btnLuk = new Button("Luk");
        HBox buttonBox = new HBox(10, btnTildel, btnLuk);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        form.add(buttonBox, 0, 3, 4, 1);

        root.setBottom(form);

        /*
        =====================================
        |       SEARCH FUNCTIONALITY        |
        =====================================
         */

        Runnable søgLedigeMedarbejdere = () -> {
            String søgeord = txfSøg.getText().trim();
            YearMonth startPeriode = null;
            YearMonth slutPeriode = null;

            try {
                if (!txfStart.getText().trim().isEmpty()) {
                    startPeriode = YearMonth.parse(txfStart.getText().trim());
                }
                if (!txfSlut.getText().trim().isEmpty()) {
                    slutPeriode = YearMonth.parse(txfSlut.getText().trim());
                }
            } catch (Exception ex) {
                return;
            }

            List<Medarbejder> medarbejdere;
            if (søgeord.isEmpty()) {
                try {
                    medarbejdere = controller.getAlleMedarbejdere();
                } catch (SQLException e) {
                    showAlert("Fejl ved hentning af medarbejder: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            } else {
                try {
                    medarbejdere = controller.søgMedarbejderNavn(søgeord);
                } catch (SQLException e) {
                    showAlert("Fejl ved søgning af medarbejdere: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                if (medarbejdere.isEmpty()) {
                    showAlert("Ingen medarbejdere fundet med navn: " + søgeord);
                    return;
                }
            }

            List<LedigMedarbejderRow> ledigeRows = new ArrayList<>();

            for (Medarbejder m : medarbejdere) {
                double ledighed = 1.0;

                if (startPeriode != null && slutPeriode != null) {
                    double totalLedighed = 0;
                    int måneder = 0;
                    YearMonth current = startPeriode;
                    while (!current.isAfter(slutPeriode)) {
                        double månedLedighed = 0;
                        try {
                            månedLedighed = controller.getLedigAndel(m.getMedId(), current);
                        } catch (SQLException e) {
                            showAlert("Fejl ved visning af ledighed: " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }

                        totalLedighed += månedLedighed;
                        måneder++;
                        current = current.plusMonths(1);
                    }
                    ledighed = måneder > 0 ? totalLedighed / måneder : 1.0;
                } else if (startPeriode != null) {
                    try {
                        ledighed = controller.getLedigAndel(m.getMedId(), startPeriode);
                    } catch (SQLException e) {
                        showAlert("Fejl ved getLedigAndel startPeriode: " + e.getMessage());
                    }
                } else if (slutPeriode != null) {
                    try {
                        ledighed = controller.getLedigAndel(m.getMedId(), slutPeriode);
                    } catch (SQLException e) {
                        showAlert("Fejl ved getLedigAndel slutPeriode");
                    }
                }

                if (ledighed > 0) {
                    ledigeRows.add(new LedigMedarbejderRow(m.getNavn(), m.getStilling(), ledighed, m));
                }
            }

            ledigeRows.sort((a, b) -> Double.compare(b.ledighed, a.ledighed));
            tvwLedige.getItems().setAll(ledigeRows);
        };

        btnSøg.setOnAction(e -> søgLedigeMedarbejdere.run());
        btnVisAlle.setOnAction(e -> {
            txfSøg.clear();
            søgLedigeMedarbejdere.run();
        });

        txfStart.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || newValue.matches("\\d{4}-\\d{2}")) {
                søgLedigeMedarbejdere.run();
            }
        });
        txfSlut.textProperty().addListener((observable, oldValue, newValue) ->  {
            if (newValue.isEmpty() || newValue.matches("\\d{4}-\\d{2}")) {
                søgLedigeMedarbejdere.run();
            }
        });

        /*
        =============================================
        |       TILDEL ALLOKERING BUTTON ACTION     |
        =============================================
         */

        btnTildel.setOnAction(e -> {
            LedigMedarbejderRow selected = tvwLedige.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Vælg en medarbejder fra listen først.");
                return;
            }

            Projekt valgtProjekt = cbProjekt.getValue();
            if (valgtProjekt == null) {
                showAlert("Vælg et projekt.");
                return;
            }

            try {
                YearMonth start = YearMonth.parse(txfStart.getText().trim());
                YearMonth slut = YearMonth.parse(txfSlut.getText().trim());
                double andel = Double.parseDouble(txfAndel.getText().trim());

                if (slut.isBefore(start)) {
                    showAlert("Slutperiode må ikke være før startperiode.");
                    return;
                }

                if (andel <= 0 || andel > 1) {
                    showAlert("Andel skal være mellem 0 og 1");
                    return;
                }

                double ledighed = controller.getLedigAndel(selected.medarbejder.getMedId(), start);
                if (ledighed < andel) {
                    showAlert(String.format("%s har kun %.of%% ledighed i %s. Kan ikke allokere %.0f%%.", selected.navn, ledighed * 100, stage, andel * 100));
                    return;
                }

                int næsteId = næsteAllokeringsId();
                int behovId = cbBehov.getValue() != null ? cbBehov.getValue().getBehovId() : -1;

                controller.createAllokering(
                        næsteId,
                        start,
                        slut,
                        andel,
                        selected.medarbejder.getMedId(),
                        valgtProjekt.getProjektId(),
                        behovId
                );

                loadData();

                søgLedigeMedarbejdere.run();

                showAlert("Allokering oprettet for " + selected.navn);

                txfStart.clear();
                txfSlut.clear();
                txfAndel.clear();
                cbProjekt.setValue(null);
                cbBehov.getItems().clear();
                cbBehov.setDisable(true);

            } catch (DateTimeParseException ex) {
                showAlert("Ugyldig datoformat. Burg YYYY-MM");
            } catch (NumberFormatException ex) {
                showAlert("Andel skal være et tal, fx 0.5");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnLuk.setOnAction(e -> stage.close());

        søgLedigeMedarbejdere.run();

        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    public static class LedigMedarbejderRow {
        private final String navn;
        private final String stilling;
        private final Double ledighed;
        private final Medarbejder medarbejder;

        public LedigMedarbejderRow(String navn, String stilling, Double ledighed, Medarbejder medarbejder) {
            this.navn = navn;
            this.stilling = stilling;
            this.ledighed = ledighed;
            this.medarbejder = medarbejder;
        }

        public String getNavn() {
            return navn;
        }

        public String getStilling() {
            return stilling;
        }

        public Double getLedighed() {
            return ledighed;
        }

        public Medarbejder getMedarbejder() {
            return medarbejder;
        }
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
