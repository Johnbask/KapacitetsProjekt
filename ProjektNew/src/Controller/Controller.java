package Controller;

import Model.Afdeling;
import Model.Enum.MedarbejderType;
import Model.Medarbejder;
import Model.Organisation;
import Model.Team;
import Storage.DBAfdeling;
import Storage.DBMedarbejder;
import Storage.DBOrganisation;
import Storage.DBTeam;

import java.sql.SQLException;

public class Controller {
    private final DBMedarbejder dbMedarbejder = new DBMedarbejder();
    private final DBAfdeling dbAfdeling = new DBAfdeling();
    private final DBOrganisation dbOrganisation = new DBOrganisation();
    private final DBTeam dbTeam = new DBTeam();

    // Singleton
    private static Controller instance;

    private Controller() {}

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    /*
    =========================
    |       AFDELING        |
    =========================
    */

    public void createAfdeling(int afdId, String navn, String leder) throws SQLException {
        Afdeling afdeling = new Afdeling(afdId, navn, leder);
        dbAfdeling.insert(afdeling);
    }

    /*
    =========================
    |     Organisation      |
    =========================
    */

    public void createOrganisation(int orgId, String navn) throws SQLException {
        Organisation organisation = new Organisation(orgId, navn);
        dbOrganisation.insert(organisation);
    }

    /*
    =========================
    |         Team          |
    =========================
    */

    public void createTeam(int teamId, String navn) throws SQLException {
        Team team = new Team(teamId, navn);
        dbTeam.insert(team);
    }

    /*
    =========================
    |      Medarbejder      |
    =========================
    */

    public void createMedarbejder(int medId, String initialer, String navn, MedarbejderType type, String stilling, boolean fratrådt, int afdId, int orgId, int teamId) throws SQLException {
        Afdeling afdeling = dbAfdeling.readById(afdId);
        Organisation organisation = dbOrganisation.readById(orgId);
        Team team = dbTeam.readById(teamId);

        if (afdeling == null || organisation == null || team == null) {
            System.out.println("Fejl: Afdeling, Organisation eller Team blev ikke fundet - Medarbejder ikke oprettet." );
        }

        Medarbejder medarbejder = new Medarbejder(medId, initialer, navn, type, stilling, fratrådt, afdeling, organisation, team);
        dbMedarbejder.insert(medarbejder);
    }


    // TODO: CRUD AF ALLE KLASSER

    // TODO: VIS ALLOKERET TID TIL MEDARBEJDER

    // TODO: CHECK ER 'LEDIG'

    // TODO: CHECK 1..* PROJEKTER

    // TODO: CHECK RB Medarbejders TIDER

    // TODO: ALLOKERLIGELIGT

}
