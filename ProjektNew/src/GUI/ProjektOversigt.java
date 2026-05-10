package GUI;


import Model.Fase;
import Model.Projekt;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class ProjektOversigt extends GridPane {

    private  javafx.scene.control.TableView<Projekt> tvwProjekt;


    public ProjektOversigt(){
        this.initcontent();

    }

    private void initcontent(){
    tvwProjekt = new TableView<>();
    this.add(tvwProjekt,0,0);

        TableColumn<Projekt, String> colNavn = new TableColumn<>("Projekt navn");
        colNavn.setCellValueFactory(new PropertyValueFactory<>("Projekt navn"));

        TableColumn<Projekt, String> colNavn2 = new TableColumn<>("Team");
        colNavn2.setCellValueFactory(new PropertyValueFactory<>("Team"));

        TableColumn<Projekt, String> colNavn3 = new TableColumn<>("Tidsperiode");
        colNavn3.setCellValueFactory(new PropertyValueFactory<>("Tidsperiode"));


        tvwProjekt.getColumns().addAll(
                colNavn,
                colNavn2,
                colNavn3
        );


        TableView<Fase> table  = new TableView<>();

// Q1
        TableColumn<Fase, Number> janCol = new TableColumn<>("Jan");
        janCol.setCellValueFactory(new PropertyValueFactory<>("jan"));

        TableColumn<Fase, Number> febCol = new TableColumn<>("Feb");
        febCol.setCellValueFactory(new PropertyValueFactory<>("feb"));

        TableColumn<Fase, Number> marCol = new TableColumn<>("Mar");
        marCol.setCellValueFactory(new PropertyValueFactory<>("mar"));

        TableColumn<Fase, Number> q1Col = new TableColumn<>("Q1");
        q1Col.getColumns().addAll(janCol, febCol, marCol);

    // Q2
        TableColumn<Fase, Number> aprCol = new TableColumn<>("Apr");
        aprCol.setCellValueFactory(new PropertyValueFactory<>("apr"));

        TableColumn<Fase, Number> majCol = new TableColumn<>("Maj");
        majCol.setCellValueFactory(new PropertyValueFactory<>("maj"));

        TableColumn<Fase, Number> junCol = new TableColumn<>("Jun");
        junCol.setCellValueFactory(new PropertyValueFactory<>("jun"));

        TableColumn<Fase, Number> q2Col = new TableColumn<>("Q2");
        q2Col.getColumns().addAll(aprCol, majCol, junCol);


        // Q3

        TableColumn<Fase, Number> JulCol = new TableColumn<>("Jul");
        aprCol.setCellValueFactory(new PropertyValueFactory<>("Jul"));

        TableColumn<Fase, Number> AugCol = new TableColumn<>("Aug");
        majCol.setCellValueFactory(new PropertyValueFactory<>("Aug"));

        TableColumn<Fase, Number> sepCol = new TableColumn<>("Sep");
        junCol.setCellValueFactory(new PropertyValueFactory<>("Sep"));

        TableColumn<Fase, Number> q3Col = new TableColumn<>("Q3");
        q2Col.getColumns().addAll(JulCol, AugCol, sepCol);


        //Q3


        TableColumn<Fase, Number> OckCol = new TableColumn<>("Apr");
        aprCol.setCellValueFactory(new PropertyValueFactory<>("apr"));

        TableColumn<Fase, Number> NovCol = new TableColumn<>("Maj");
        majCol.setCellValueFactory(new PropertyValueFactory<>("maj"));

        TableColumn<Fase, Number> DecCol = new TableColumn<>("Jun");
        junCol.setCellValueFactory(new PropertyValueFactory<>("jun"));

        TableColumn<Fase, Number> q4Col = new TableColumn<>("Q4");
        q2Col.getColumns().addAll(OckCol, NovCol, DecCol);


// tilføj til tabel

        table.getColumns().addAll(

                q1Col,
                janCol,febCol,marCol,

                q2Col,
                aprCol,majCol,junCol,

                q3Col,
                JulCol, AugCol, sepCol,

                q4Col,
                OckCol,NovCol,DecCol

        );






    }


}

