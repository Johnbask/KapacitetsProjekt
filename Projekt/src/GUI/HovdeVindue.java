package GUI;



import Projekt.Projekt;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;

public class HovdeVindue extends Application {

private HovdeVindue hovdeVindue;


    @Override
    public void start(Stage Stage) throws Exception {
        Stage.setTitle("Kapacitets Project");
        BorderPane pane = new BorderPane();
        this.initContent(pane);

        Scene scene = new Scene(pane);
        Stage.setScene(scene);
        Stage.setHeight(500);
        Stage.setWidth(800);
        Stage.show();

    }

    public void initContent(BorderPane pane){
        TabPane tabPane = new TabPane();
        this.initTabPane(tabPane);
        pane.setCenter(tabPane);

    }

    public void initTabPane(TabPane tabPane){
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);


        //skabelse af tabs
        Tab tabMedarbejderOversigt = new Tab("Medarbejder oversigt.");
        Tab tabProjektOversigt = new Tab("Projekt oversigt.");
        Tab tabBehovOversigt = new Tab("Behov oversigt.");
        Tab tabTeamOversigt = new Tab("Team oversigt.");

        // forbindelse til tabs




        // Tilføjelse af tabs
        tabPane.getTabs().add(tabBehovOversigt);
        tabPane.getTabs().add(tabMedarbejderOversigt);
        tabPane.getTabs().add(tabProjektOversigt);
        tabPane.getTabs().add(tabTeamOversigt);





    }

}



