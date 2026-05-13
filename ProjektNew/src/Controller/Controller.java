package Controller;

import Model.*;
import Model.Enum.Kvartal;
import Model.Enum.MedarbejderType;
import Model.Enum.ØkonomiType;
import Storage.*;
import org.junit.jupiter.api.AfterAll;

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

    public Afdeling createAfdeling(int afdId, String navn, String leder) throws SQLException {
        Afdeling afdeling = new Afdeling(afdId, navn, leder);
        dbAfdeling.insert(afdeling);
        return afdeling;
    }

    public ArrayList<Afdeling> getAlleAfdelinger() throws SQLException {
        return dbAfdeling.readAll();
    }

    public Afdeling getAfdelingById(int afdId) throws SQLException {
        return dbAfdeling.readById(afdId);
    }

    public void updateAfdeling(int afdId, String nytNavn, String nyLeder) throws SQLException {
        Afdeling afdeling = new Afdeling(afdId, nytNavn, nyLeder);
        dbAfdeling.update(afdeling);
    }

    public void deleteAfdeling(int afdId) throws SQLException {
        dbAfdeling.delete(afdId);
    }

    /*
    =========================
    |     Organisation      |
    =========================
    */

    public Organisation createOrganisation(int orgId, String navn) throws SQLException {
        Organisation organisation = new Organisation(orgId, navn);
        dbOrganisation.insert(organisation);
        return organisation;
    }

    public ArrayList<Organisation> getAlleOrganisationer() throws SQLException {
        return dbOrganisation.readAll();
    }

    public Organisation getOrganisationById(int orgId) throws SQLException {
        return dbOrganisation.readById(orgId);
    }

    public void updateOrganisation(int orgId, String nyNavn) throws SQLException {
        Organisation organisation = new Organisation(orgId, nyNavn);
        dbOrganisation.update(organisation);
    }

    public void deleteOrganisation(int orgId) throws SQLException {
        dbOrganisation.delete(orgId);
    }

    /*
    =========================
    |         Team          |
    =========================
    */

    public Team createTeam(int teamId, String navn) throws SQLException {
        Team team = new Team(teamId, navn);
        dbTeam.insert(team);
        return team;
    }

    public ArrayList<Team> getAlleTeams() throws SQLException {
        return dbTeam.readAll();
    }

    public Team getTeamById(int teamId) throws SQLException {
        return dbTeam.readById(teamId);
    }

    public void updateTeam(int teamId, String nyNavn) throws SQLException {
        Team team = new Team(teamId, nyNavn);
        dbTeam.update(team);
    }

    public void deleteTeam(int teamId) throws SQLException {
        dbTeam.delete(teamId);
    }

    /*
    =========================
    |      Medarbejder      |
    =========================
    */

    public Medarbejder createMedarbejder(int medId, String initialer, String navn, MedarbejderType type,
                                  String stilling, boolean fratrådt, int afdId, int orgId, int teamId) throws SQLException {
        Afdeling afdeling = dbAfdeling.readById(afdId);
        Organisation organisation = dbOrganisation.readById(orgId);
        Team team = dbTeam.readById(teamId);

        if (afdeling == null || organisation == null || team == null) {
            System.out.println("Fejl: Afdeling, Organisation eller Team blev ikke fundet - Medarbejder ikke oprettet." );
            return null;
        }

        Medarbejder medarbejder = new Medarbejder(medId, initialer, navn, type, stilling, fratrådt, afdeling, organisation, team);
        dbMedarbejder.insert(medarbejder);
        return medarbejder;
    }

    public ArrayList<Medarbejder> getAlleMedarbejdere() throws SQLException {
        return dbMedarbejder.readAll();
    }

    public Medarbejder getMedarbejderById(int medId) throws  SQLException {
        return dbMedarbejder.readById(medId);
    }

    public void updateMedarbejder(int medId, String nyInitialer, String nynNavn,
                                  MedarbejderType type, String nyStilling, boolean fratrådt,
                                  int afdId, int orgId, int teamId) throws  SQLException {
        Afdeling afdeling = dbAfdeling.readById(afdId);
        Organisation organisation = dbOrganisation.readById(orgId);
        Team team = dbTeam.readById(teamId);

        if (afdeling == null || organisation == null || team == null) {
            System.out.println("Fejl: Afdeling, Organisation eller Team blev ikke fundet - Medarbejder ikke opdateret");
        }

        Medarbejder medarbejder = new Medarbejder(medId, nyInitialer, nynNavn, type, nyStilling, fratrådt, afdeling, organisation, team);
        dbMedarbejder.update(medarbejder);
    }

    public void deleteMedarbejder(int medId) throws SQLException {
        dbMedarbejder.delete(medId);
    }

    /*
    =========================
    |       Projekt         |
    =========================
     */

    public Projekt createProjekt(int projektId, String navn) throws SQLException {
        Projekt projekt = new Projekt(projektId, navn);
        dbProjekt.insert(projekt);
        return projekt;
    }

    public ArrayList<Projekt> getAlleProjekter() throws SQLException {
        return dbProjekt.readAll();
    }

    public Projekt getProjektById(int projektId) throws SQLException {
        return dbProjekt.readById(projektId);
    }

    public void updateProjetk(int projektId, String nyNavn) throws SQLException {
        Projekt projekt = new Projekt(projektId, nyNavn);
        dbProjekt.update(projekt);
    }

    public void deleteProjekt(int projektId) throws SQLException {
        dbProjekt.delete(projektId);
    }

    /*
    ==========================
    |     RessourceBehov     |
    ==========================
     */

    public RessourceBehov createRessourceBehov(int behovId, String rolle, YearMonth periode,
                                               double andel, double timePris, ØkonomiType økonomiType,
                                               int projektId) throws SQLException {

        Projekt projekt = dbProjekt.readById(projektId);

        if (projekt == null) {
            System.out.println("Fejl: Projekt ikke fundet - RessourceBehov ikke oprettet");
            return null;
        }

        RessourceBehov ressourceBehov = new RessourceBehov(behovId, rolle, periode, andel, timePris, økonomiType);
        ressourceBehov.setProjekt(projekt);

        dbRessourceBehov.insert(ressourceBehov);

        return ressourceBehov;
    }

    public ArrayList<RessourceBehov> getAlleRessourceBehov() throws SQLException {
        return dbRessourceBehov.readAll();
    }

    public RessourceBehov getRessourceBehovById(int behovId) throws SQLException {
        return dbRessourceBehov.readById(behovId);
    }

    public void updateRessourceBehov(int nyBehovId, String nyRolle, YearMonth nyPeriode,
                                     double nyAndel, double nyTimePris, ØkonomiType nyØkonomiType,
                                     int nyProjektId) throws SQLException {
        Projekt projekt = dbProjekt.readById(nyProjektId);

        if (projekt == null) {
            System.out.println("Fejl: projekt blev ikke fundet - RessourceBehov ikke opdateret");
            return;
        }

        RessourceBehov ressourceBehov = new RessourceBehov(nyBehovId, nyRolle, nyPeriode, nyAndel, nyTimePris, nyØkonomiType);
        ressourceBehov.setProjekt(projekt);
        dbRessourceBehov.update(ressourceBehov);
    }

    public void deleteRessourceBehov(int behovId) throws SQLException {
        dbRessourceBehov.delete(behovId);
    }

    /*
    ===========================
    |           Fase          |
    ===========================
     */

    public Fase createFase(int faseId, String navn, YearMonth startMåned,
                           YearMonth slutMåned, Kvartal kvartal, double andel,
                           int projektId) throws SQLException {
        Projekt projekt = dbProjekt.readById(projektId);

        if (projekt == null) {
            System.out.println("Fejl: Projekt ikke fundet - Fase ikke oprettet." );
            return null;
        }

        Fase fase = new Fase(faseId, navn, startMåned, slutMåned, kvartal, andel);
        fase.setProjekt(projekt);

        dbFase.insert(fase);

        return fase;
    }

    public ArrayList<Fase> getAlleFaser() throws SQLException {
        return dbFase.readAll();
    }

    public Fase getFaseById(int faseId) throws SQLException {
        return dbFase.readById(faseId);
    }

    public void updateFase(int nyFaseId, String nyNavn, YearMonth nyStartMåned,
                           YearMonth nySlutMåned, Kvartal nyKvartal, double nyAndel,
                           int nyProjektId) throws SQLException {
        Projekt projekt = dbProjekt.readById(nyProjektId);

        if (projekt == null) {
            System.out.println("Fejl: projekt blev ikke fundet - Fase blev ikke opdateret");
            return;
        }

        Fase fase = new Fase(nyFaseId, nyNavn, nyStartMåned, nySlutMåned, nyKvartal, nyAndel);
        fase.setProjekt(projekt);
        dbFase.update(fase);
    }

    public void deleteFase(int faseId) throws SQLException {
        dbFase.delete(faseId);
    }

    /*
    ==========================
    |       Allokering       |
    ==========================
    */

    public Allokering createAllokering(int allokeringsId, YearMonth periode, double andel,
                                       int medId, int projektId, int behovId) throws SQLException {
        Medarbejder medarbejder = dbMedarbejder.readById(medId);
        Projekt projekt = dbProjekt.readById(projektId);
        RessourceBehov ressourceBehov = dbRessourceBehov.readById(behovId);

        if (medarbejder == null || projekt == null || ressourceBehov == null) {
            System.out.println("Fejl: Medarbejder, Projekt eller RessourceBehov ikke fundet - Allokering ikke oprettet.");
            return null;
        }

        Allokering allokering = new Allokering(allokeringsId, periode, andel);
        allokering.addMedarbejder(medarbejder);
        allokering.setProjekt(projekt);
        allokering.setRessourceBehov(ressourceBehov);

        dbAllokering.insert(allokering);

        return allokering;
    }

    public ArrayList<Allokering> getAlleAllokeringer() throws SQLException {
        return dbAllokering.readAll();
    }

    public Allokering getAllokeringById(int allokeringsId) throws SQLException {
        return dbAllokering.readById(allokeringsId);
    }

    public void updateAllokering(int nyAllokeringsId, YearMonth nyPeriode, double nyAndel,
                                 int nyMedId, int nyProjektId, int nyBehovId) throws SQLException {
        Medarbejder medarbejder = dbMedarbejder.readById(nyMedId);
        Projekt projekt = dbProjekt.readById(nyProjektId);
        RessourceBehov behov = dbRessourceBehov.readById(nyBehovId);

        if (medarbejder == null || projekt == null || behov == null) {
            System.out.println("Fejl: Medarbejder eller Projekt eller Ressource behov ikke fundet - Allokering blev ikke opdateret");
            return;
        }

        Allokering allokering = new Allokering(nyAllokeringsId, nyPeriode, nyAndel);
        allokering.addMedarbejder(medarbejder);
        allokering.setProjekt(projekt);
        allokering.setRessourceBehov(behov);

        dbAllokering.update(allokering);
    }

    public void deleteAllokering(int allokeringsId) throws SQLException {
        dbAllokering.delete(allokeringsId);
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


     /*
     EFTER INTERVIEW
      */

    // TODO: Søgning af ledighed på en periode

    // TODO: Kapacitets melding

    // TODO: Syg melding / Barsel

    // TODO: Alert at der mangler medarbejder på projekt

    // TODO: simple søgning - navn

}
