package Model;

import Controller.Controller;
import Model.Enum.MedarbejderType;
import Storage.DBAfdeling;
import Storage.DBMedarbejder;
import Storage.DBOrganisation;
import Storage.DBTeam;

import javax.sound.midi.Soundbank;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        DBMedarbejder dbMedarbejder = new DBMedarbejder();
        DBAfdeling dbAfdeling = new DBAfdeling();
        DBOrganisation dbOrganisation = new DBOrganisation();
        DBTeam dbTeam = new DBTeam();

        Controller controller = Controller.getInstance();
        /*
        Afdeling afdeling = controller.createAfdeling(2, "Datahub", "Hanne");
        Organisation organisation = controller.createOrganisation(2, "Erhversakademi Aarhus");
        Team team = controller.createTeam(2, "25-T");

        Medarbejder medarbejder = controller.createMedarbejder(2, "IJ", "Ib Jensen", MedarbejderType.INTERN, "UDV", false, 2, 2, 2);
         */

        System.out.println(dbMedarbejder.readAll());
        System.out.println();
        System.out.println(dbMedarbejder.readById(2));
        System.out.println(dbAfdeling.readAll());
        System.out.println(dbOrganisation.readAll());
        System.out.println(dbTeam.readAll());
    }
}
