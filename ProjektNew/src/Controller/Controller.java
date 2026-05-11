package Controller;

import Model.*;
import Model.Enum.Kvartal;
import Model.Enum.MedarbejderType;
import Model.Enum.ØkonomiType;
import Storage.*;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;

public class Controller {
    private final DBMedarbejder dbMedarbejder = new DBMedarbejder();
    private final DBAfdeling dbAfdeling = new DBAfdeling();
    private final DBOrganisation dbOrganisation = new DBOrganisation();
    private final DBTeam dbTeam = new DBTeam();
    private final DBProjekt dbProjekt = new DBProjekt();
    private final DBRessourceBehov dbRessourceBehov = new DBRessourceBehov();
    private final DBFase dbFase = new DBFase();
    private final DBAllokering dbAllokering = new DBAllokering();

    // Singleton
    private static Controller instance;

    private Controller() {}

    public synchronized static Controller getInstance() {
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

    public void createMedarbejder(int medId, String initialer, String navn, MedarbejderType type,
                                  String stilling, boolean fratrådt, int afdId, int orgId, int teamId) throws SQLException {
        Afdeling afdeling = dbAfdeling.readById(afdId);
        Organisation organisation = dbOrganisation.readById(orgId);
        Team team = dbTeam.readById(teamId);

        if (afdeling == null || organisation == null || team == null) {
            System.out.println("Fejl: Afdeling, Organisation eller Team blev ikke fundet - Medarbejder ikke oprettet." );
            return;
        }

        Medarbejder medarbejder = new Medarbejder(medId, initialer, navn, type, stilling, fratrådt, afdeling, organisation, team);
        dbMedarbejder.insert(medarbejder);
    }

    /*
    =========================
    |       Projekt         |
    =========================
     */

    public void createProjekt(int projektId, String navn) throws SQLException {
        Projekt projekt = new Projekt(projektId, navn);
        dbProjekt.insert(projekt);
    }

    /*
    ==========================
    |     RessourceBehov     |
    ==========================
     */

    public void createRessourceBehov(int behovId, String rolle, YearMonth periode, double andel, double timePris, ØkonomiType økonomiType, int projektId) throws SQLException {
        Projekt projekt = dbProjekt.readById(projektId);

        if (projekt == null) {
            System.out.println("Fejl: Projekt ikke fundet - RessourceBehov ikke oprettet");
            return;
        }

        RessourceBehov ressourceBehov = new RessourceBehov(behovId, rolle, periode, andel, timePris, økonomiType);
        ressourceBehov.setProjekt(projekt);

        dbRessourceBehov.insert(ressourceBehov);
    }

    /*
    ===========================
    |           Fase          |
    ===========================
     */

    public void createFase(int faseId, String navn, YearMonth startMåned, YearMonth slutMåned, Kvartal kvartal, double andel, int projektId) throws SQLException {
        Projekt projekt = dbProjekt.readById(projektId);

        if (projekt == null) {
            System.out.println("Fejl: Projekt ikke fundet - Fase ikke oprettet." );
            return;
        }

        Fase fase = new Fase(faseId, navn, startMåned, slutMåned, kvartal, andel);
        fase.setProjekt(projekt);

        dbFase.insert(fase);
    }

    /*
    ==========================
    |       Allokering       |
    ==========================
    */

    public void createAllokering(int allokeringsId, YearMonth periode, double andel, int medId, int projektId, int behovId) throws SQLException {
        Medarbejder medarbejder = dbMedarbejder.readById(medId);
        Projekt projekt = dbProjekt.readById(projektId);
        RessourceBehov ressourceBehov = dbRessourceBehov.readById(behovId);

        if (medarbejder == null || projekt == null || ressourceBehov == null) {
            System.out.println("Fejl: Medarbejder, Projekt eller RessourceBehov ikke fundet - Allokering ikke oprettet.");
            return;
        }

        Allokering allokering = new Allokering(allokeringsId, periode, andel);
        allokering.addMedarbejder(medarbejder);
        allokering.setProjekt(projekt);
        allokering.setRessourceBehov(ressourceBehov);

        dbAllokering.insert(allokering);
    }

    // TODO: CRUD AF ALLE KLASSER

    // TODO: VIS ALLOKERET TID TIL MEDARBEJDER

    /*
    Viser en liste over alle allokeringer for den givne medarbejder
     */

    public ArrayList<Allokering> getAllokeringerForMedarbejder(int medId) throws  SQLException {
        ArrayList<Allokering> alleAllokeringer = dbAllokering.readAll();
        ArrayList<Allokering> medarbejderAllokeringer = new ArrayList<>();

        for (Allokering allokering : alleAllokeringer) {
            for (Medarbejder medarbejder : allokering.getMedarbejdere()) {
                if (medarbejder.getMedId() == medId) {
                    medarbejderAllokeringer.add(allokering);
                    break;
                }
            }
        }

        return medarbejderAllokeringer;
    }

    /*
    Viser den samlede allokerede andel for den givne medarbejder
     */

    public double getSamletAllokeretAndelForMedarbejder(int medId) throws SQLException {
        ArrayList<Allokering> allokeringer = getAllokeringerForMedarbejder(medId);

        double samletAndel = 0;

        for (Allokering allokering : allokeringer) {
            samletAndel += allokering.getAndel();
        }

        return samletAndel;
    }

    // TODO: CHECK ER 'LEDIG'

    /*
    Checker hvis en medarbejder er 'ledig'.
    Hvis medarbejders andel er under 1.0
    */

    public boolean erLedig(int medId, YearMonth periode) throws SQLException {
        ArrayList<Allokering> allokeringer = getAllokeringerForMedarbejder(medId);

        double samledeAndel = 0;

        for (Allokering allokering : allokeringer) {
            if (allokering.getPeriode().equals(periode)) {
                samledeAndel += allokering.getAndel();
            }
        }

        return samledeAndel < 1.0;
    }

    /*
    Checker hvor meget ledighed en medarbejder har
    */

    public double getLedigAndel(int medId, YearMonth periode) throws SQLException {
        ArrayList<Allokering> allokeringer = getAllokeringerForMedarbejder(medId);

        double samletAndel = 0;

        for (Allokering allokering : allokeringer) {
            if (allokering.getPeriode().equals(periode)) {
                samletAndel += allokering.getAndel();
            }
        }

        return 1.0 - samletAndel;
    }

    // TODO: CHECK 1..* PROJEKTER

    // TODO: CHECK RB Medarbejders TIDER

    /*

    */

    // TODO: ALLOKERLIGELIGT

}
