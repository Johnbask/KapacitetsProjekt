package Model;

import Controller.Controller;
import Model.Enum.MedarbejderType;
import Model.Enum.MeldingType;
import Storage.DBAfdeling;
import Storage.DBMedarbejder;
import Storage.DBOrganisation;
import Storage.DBTeam;

import javax.sound.midi.Soundbank;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;

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
        /*
        System.out.println(dbMedarbejder.readById(2));
        System.out.println(dbAfdeling.readAll());
        System.out.println(dbOrganisation.readAll());
        System.out.println(dbTeam.readAll());
        System.out.println("\n=== getLedighedForMedarbejder('Aksel') ===");
        controller.getLedighedForMedarbejder("Aksel");
        System.out.println("\n=== getLedigeMedarbejdereIPeriode(YearMonth(2026, 9)) ===");
        controller.getLedigeMedarbejdereIPeriode(YearMonth.of(2026, 9));
        System.out.println("\n=== getLedigeMedarbejdereIPeriode(YearMonth(2026, 8)) ===");
        controller.getLedigeMedarbejdereIPeriode(YearMonth.of(2026, 8));
        System.out.println("\n=== getLedigMedarbejderMedAndel(YearMonth.of(2026, 8), 0.75) ===");
        controller.getLedigMedarbejdereMedAndel(YearMonth.of(2026, 8), 0.75);
        System.out.println();
         */

        //controller.deleteMelding(1);
        //controller.createMelding(1, MeldingType.SYG, LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 19), "Influenza", 7);

        //controller.getMeldingerForMedarbejder("AKF");

        //controller.getMeldingerAfType(MeldingType.SYG);
        /*
        boolean aktiv = controller.harAktivMelding("Aksel", LocalDate.of(2026, 5, 16));
        System.out.println(aktiv);

         */
    }
}
