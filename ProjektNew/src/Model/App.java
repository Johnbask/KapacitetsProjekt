package Model;

import Model.Enum.MedarbejderType;
import Storage.DBMedarbejder;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        DBMedarbejder dbMedarbejder = new DBMedarbejder();

        Afdeling afdeling = new Afdeling(1, "Datahub", "Jens");
        Organisation organisation = new Organisation(1, "Sidemen");
        Team team = new Team(1, "Twitch");

        Medarbejder m1 = new Medarbejder(2, "LAM", "Lionel Andre Messi", MedarbejderType.INTERN, "UDV", false, afdeling, organisation, team);

        dbMedarbejder.insert(m1);

        System.out.println(dbMedarbejder.readAll());
        System.out.println();
        System.out.println(dbMedarbejder.readById(1));
    }
}
