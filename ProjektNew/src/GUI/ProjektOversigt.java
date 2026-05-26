package GUI;

import Controller.Controller;
import Model.Projekt;
import Model.RessourceBehov;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ProjektOversigt extends GridPane {

    private final Controller controller = Controller.getInstance();

    private GridPane timelineGrid;
    private ScrollPane scrollPane;

    private Button btnOpret;
    private Button btnRediger;
    private Button btnSlet;

    private List<Projekt> projekter;

    // Callback der kaldes når et nyt projekt oprettes
    // Consumer<Projekt> så HovdeVindue får det nye projekt-objekt
    private Consumer<Projekt> onProjektOprettet;

    private final String[] colors = {
            "#8ecae6",
            "#ffb703",
            "#90be6d",
            "#f94144",
            "#577590"
    };

    public ProjektOversigt() {
        initContent();
        initActions();
    }

    // =====================================================
    // CALLBACK REGISTRERING
    // =====================================================
    public void setOnProjektOprettet(Consumer<Projekt> callback) {
        this.onProjektOprettet = callback;
    }

    private void initContent() {

        this.setPadding(new Insets(10));
        this.setVgap(10);

        // ---------------- BUTTONS ----------------
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        btnOpret   = new Button("Opret Projekt");
        btnRediger = new Button("Rediger Projekt");
        btnSlet = new Button("Slet Projekt");

        buttonBox.getChildren().addAll(btnOpret, btnRediger, btnSlet);
        this.add(buttonBox, 0, 2);

        // ---------------- TIMELINE ----------------
        timelineGrid = new GridPane();
        timelineGrid.setHgap(2);
        timelineGrid.setVgap(2);

        scrollPane = new ScrollPane(timelineGrid);
        scrollPane.setPrefViewportHeight(500);
        scrollPane.setPrefWidth(1100);
        scrollPane.setPannable(true);

        this.add(scrollPane, 0, 1);

        scrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                scrollPane.prefWidthProperty().bind(newScene.widthProperty().subtract(20));
                scrollPane.prefViewportHeightProperty().bind(newScene.heightProperty().subtract(80));
            }
        });

    }

    private void initActions() {
        btnOpret.setOnAction(e -> createProjektWindow());
        btnRediger.setOnAction(e -> editProjektWindow());
        btnSlet.setOnAction(e -> {
            try {
                sletProjektAlert();
            } catch (SQLException ex) {
                showAlert("Fejl: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // =====================================================
    // BUILD TIMELINE
    // =====================================================
    public void buildTimeline(List<Projekt> projekter) {
        this.projekter = projekter;

        timelineGrid.getChildren().clear();

        if (projekter == null || projekter.isEmpty()) return;

        YearMonth min = null;
        YearMonth max = null;

        // FIND GLOBAL RANGE
        for (Projekt projekt : projekter) {
            for (RessourceBehov rb : projekt.getRessourceBehov()) {
                if (min == null || rb.getStartPeriode().isBefore(min)) min = rb.getStartPeriode();
                if (max == null || rb.getSlutPeriode().isAfter(max))   max = rb.getSlutPeriode();
            }
        }

        if (min == null || max == null) return;

        // HEADER
        YearMonth current = min;
        int col = 1;
        int currentYear    = -1;
        int currentQuarter = -1;

        while (!current.isAfter(max)) {

            int year    = current.getYear();
            int month   = current.getMonthValue();
            int quarter = ((month - 1) / 3) + 1;

            if (year != currentYear) {
                Label lblYear = new Label(String.valueOf(year));
                lblYear.setMinSize(80, 25);
                lblYear.setAlignment(Pos.CENTER);
                lblYear.setStyle("-fx-font-weight: bold;" +
                        "-fx-border-color: black;" +
                        "-fx-background-color: #dddddd;");
                timelineGrid.add(lblYear, col, 0);
                currentYear    = year;
                currentQuarter = -1;
            }

            if (quarter != currentQuarter) {
                Label lblQuarter = new Label("Q" + quarter);
                lblQuarter.setMinSize(80, 25);
                lblQuarter.setAlignment(Pos.CENTER);
                lblQuarter.setStyle("-fx-font-weight: bold;" +
                        "-fx-border-color: gray;" +
                        "-fx-background-color: #eeeeee;");
                timelineGrid.add(lblQuarter, col, 1);
                currentQuarter = quarter;
            }

            Label lblMonth = new Label(current.getMonth().name().substring(0, 3));
            lblMonth.setMinSize(80, 25);
            lblMonth.setAlignment(Pos.CENTER);
            lblMonth.setStyle("-fx-border-color: lightgray;");
            timelineGrid.add(lblMonth, col, 2);

            current = current.plusMonths(1);
            col++;
        }

        // PROJECT ROWS
        int startRow = 3;

        for (int i = 0; i < projekter.size(); i++) {

            Projekt projekt = projekter.get(i);
            int row    = startRow + i;
            String color = colors[i % colors.length];

            Label navnLabel = new Label(projekt.getNavn());
            navnLabel.setMinSize(120, 30);
            navnLabel.setAlignment(Pos.CENTER_LEFT);
            navnLabel.setStyle("-fx-font-weight: bold; -fx-border-color: black;");
            timelineGrid.add(navnLabel, 0, row);

            current = min;
            col = 1;

            while (!current.isAfter(max)) {

                Label cell = new Label();
                cell.setMinSize(80, 30);
                cell.setAlignment(Pos.CENTER);

                boolean filled = false;

                for (RessourceBehov rb : projekt.getRessourceBehov()) {
                    // Simpelt periode-check i stedet for indre while-løkke
                    if (!current.isBefore(rb.getStartPeriode()) && !current.isAfter(rb.getSlutPeriode())) {
                        cell.setStyle("-fx-background-color: " + color + "; -fx-border-color: black;");
                        filled = true;
                        break;
                    }
                }

                if (!filled) {
                    cell.setStyle("-fx-border-color: lightgray;");
                }

                timelineGrid.add(cell, col, row);

                current = current.plusMonths(1);
                col++;
            }
        }
    }

    // =====================================================
    // CREATE PROJECT WINDOW
    // =====================================================
    private void createProjektWindow() {

        Stage stage = new Stage();
        stage.setTitle("Opret Projekt");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        Label lblNavn  = new Label("Projekt navn:");
        TextField txfNavn = new TextField();

        Label lblId  = new Label("Projekt ID:");
        TextField txfId = new TextField();

        Button btnGem = new Button("Gem");
        Button btnLuk = new Button("Luk");

        pane.add(lblId,    0, 0); pane.add(txfId,   1, 0);
        pane.add(lblNavn,  0, 1); pane.add(txfNavn,  1, 1);
        pane.add(btnGem,   0, 2); pane.add(btnLuk,   1, 2);

        btnGem.setOnAction(e -> {

            String navn = txfNavn.getText().trim();
            String idTekst = txfId.getText().trim();

            if (navn.isEmpty() || idTekst.isEmpty()) {
                showAlert("Udfyld både ID og navn.");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idTekst);
            } catch (NumberFormatException ex) {
                showAlert("Projekt ID skal være et heltal.");
                return;
            }

            Projekt nyt = null;
            try {
                nyt = controller.createProjekt(id, navn);
            } catch (SQLException ex) {
                showAlert("Fejl ved oprettelse af projekt: " + ex.getMessage());
                ex.printStackTrace();
            }

            // Fortæl HovdeVindue om det nye projekt
            // HovdeVindue tilføjer det til den delte liste og kalder buildTimeline + setProjekter
            if (onProjektOprettet != null) {
                onProjektOprettet.accept(nyt);
            }

            stage.close();
        });

        btnLuk.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 300, 175));
        stage.showAndWait();
    }

    // =====================================================
    // EDIT PROJECT WINDOW
    // =====================================================
    private void editProjektWindow() {

        if (projekter == null || projekter.isEmpty()) return;

        Stage stage = new Stage();
        stage.setTitle("Rediger Projekt");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        ComboBox<Projekt> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(projekter);
        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Projekt p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getNavn());
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Projekt p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getNavn());
            }
        });

        TextField txfNavn = new TextField();
        Button btnGem = new Button("Gem");
        Button btnLuk = new Button("Luk");

        pane.add(new Label("Projekt:"),  0, 0); pane.add(comboBox, 1, 0);
        pane.add(new Label("Nyt navn:"), 0, 1); pane.add(txfNavn,  1, 1);
        pane.add(btnGem,                 0, 2); pane.add(btnLuk,   1, 2);

        comboBox.setOnAction(e -> {
            Projekt valgt = comboBox.getValue();
            if (valgt != null) txfNavn.setText(valgt.getNavn());
        });

        btnGem.setOnAction(e -> {
            Projekt valgt = comboBox.getValue();
            if (valgt != null) {
                valgt.setNavn(txfNavn.getText());
                try {
                    controller.updateProjetk(valgt.getProjektId(), txfNavn.getText());
                } catch (SQLException ex) {
                    showAlert("Fejl ved opdatering af projekt: " + ex.getMessage());
                }
                buildTimeline(projekter);
            }
            stage.close();
        });

        btnLuk.setOnAction(e -> stage.close());

        stage.setScene(new Scene(pane, 350, 200));
        stage.showAndWait();
    }

    private void sletProjektAlert() throws SQLException {
        Stage stage = new Stage();
        stage.setTitle("Sletning af projekt:");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        ComboBox<Projekt> cmbProjekt = new ComboBox<>();
        cmbProjekt.getItems().setAll(projekter);
        cmbProjekt.setPromptText("Vælg projekt");
        cmbProjekt.setPrefWidth(200);

        Button btnSletProjekt = new Button("Slet Projekt");

        btnSletProjekt.setOnAction(e -> {
            Projekt valgtProjekt = cmbProjekt.getValue();
            if (valgtProjekt != null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advarsel Sletning af valgt projekt: " + valgtProjekt);
                alert.setHeaderText("Er du sikker på du vil slette valgt Projekt?");
                alert.setContentText("Hvis du trykker 'Ok' sletter du alt ved valgte Projekt");

                Optional<ButtonType> resultat = alert.showAndWait();

                if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                    try {
                        controller.deleteProjekt(valgtProjekt.getProjektId());
                        projekter.remove(valgtProjekt);
                        buildTimeline(projekter);
                        System.out.println("Slettet");
                    } catch (SQLException ex) {
                        showAlert("Fejl ved sletning af projekt: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
            stage.close();
        });

        pane.add(new Label("Projekter:"), 0, 0); pane.add(cmbProjekt, 1, 0);
        pane.add(btnSletProjekt, 1, 1);

        stage.setScene(new Scene(pane, 350, 150));
        stage.showAndWait();
    }

    private void showAlert(String besked) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advarsel");
        alert.setHeaderText(null);
        alert.setContentText(besked);
        alert.showAndWait();
    }
}