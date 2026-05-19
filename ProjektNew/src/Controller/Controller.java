package Controller;

import Model.*;
import Model.Enum.Kvartal;
import Model.Enum.MedarbejderType;
import Model.Enum.MeldingType;
import Model.Enum.ØkonomiType;
import Storage.*;


import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final DBMedarbejder dbMedarbejder = new DBMedarbejder();
    private final DBAfdeling dbAfdeling = new DBAfdeling();
    private final DBOrganisation dbOrganisation = new DBOrganisation();
    private final DBTeam dbTeam = new DBTeam();
    private final DBProjekt dbProjekt = new DBProjekt();
    private final DBRessourceBehov dbRessourceBehov = new DBRessourceBehov();
    private final DBFase dbFase = new DBFase();
    private final DBAllokering dbAllokering = new DBAllokering();
    private final DBMelding dbMelding = new DBMelding();

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

    public Afdeling getAfdelingByName(String navn) throws SQLException {
        return dbAfdeling.readByName(navn);
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

    public Organisation getOrganisationByName(String navn) throws SQLException{
        return dbOrganisation.readByName(navn);
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

    public Team getTeamByName(String navn) throws SQLException {
        return dbTeam.readByName(navn);
    }

    public Team updateTeam(int teamId, String nyNavn) throws SQLException {
        Team team = new Team(teamId, nyNavn);
        dbTeam.update(team);
        return team;
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
                                  String stilling, boolean fratrådt, String afdNavn, String orgNavn, String teamNavn) throws SQLException {
        Afdeling afdeling = dbAfdeling.readByName(afdNavn);
        Organisation organisation = dbOrganisation.readByName(orgNavn);
        Team team = dbTeam.readByName(teamNavn);

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

    public Medarbejder updateMedarbejder(int medId, String nyInitialer, String nytNavn,
                                         MedarbejderType type, String nyStilling, boolean fratrådt,
                                         String afdNavn, String orgNavn, String teamNavn) throws  SQLException {
        Afdeling afdeling = null;
        if (afdNavn != null && !afdNavn.isEmpty()) {
            afdeling = dbAfdeling.readByName(afdNavn);
            if (afdeling == null) {
                afdeling = dbAfdeling.readByLeder(afdNavn);
            }
        }

        Organisation organisation = null;
        if (orgNavn != null && !orgNavn.isEmpty()) {
            organisation = dbOrganisation.readByName(orgNavn);
        }

        Team team = null;
        if (teamNavn != null && !teamNavn.isEmpty()) {
            team = dbTeam.readByName(teamNavn);
        }

        if (afdeling == null || organisation == null || team == null) {
            System.out.println("Fejl: Afdeling, Organisation eller Team blev ikke fundet - Medarbejder ikke opdateret");
            return null;
        }

        Medarbejder medarbejder = new Medarbejder(medId, nyInitialer, nytNavn, type, nyStilling, fratrådt, afdeling, organisation, team);
        dbMedarbejder.update(medarbejder);
        return medarbejder;
    }

    public Medarbejder deleteMedarbejder(int medId) throws SQLException {
        return dbMedarbejder.delete(medId);
    }

    public void fjernMedarbejderFraTeam(int medId) throws SQLException {
        dbMedarbejder.removeFromTeam(medId);
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

    public RessourceBehov createRessourceBehov(int behovId, String rolle, YearMonth startPeriode, YearMonth slutPeriode,
                                               double andel, double timePris, ØkonomiType økonomiType,
                                               int projektId) throws SQLException {

        Projekt projekt = dbProjekt.readById(projektId);

        if (projekt == null) {
            System.out.println("Fejl: Projekt ikke fundet - RessourceBehov ikke oprettet");
            return null;
        }

        RessourceBehov ressourceBehov = new RessourceBehov(behovId, rolle, startPeriode,slutPeriode, andel, timePris, økonomiType);
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

    public void updateRessourceBehov(int nyBehovId, String nyRolle, YearMonth nyStartPeriode, YearMonth nySlutPeriode,
                                     double nyAndel, double nyTimePris, ØkonomiType nyØkonomiType,
                                     int nyProjektId) throws SQLException {
        Projekt projekt = dbProjekt.readById(nyProjektId);

        if (projekt == null) {
            System.out.println("Fejl: projekt blev ikke fundet - RessourceBehov ikke opdateret");
            return;
        }

        RessourceBehov ressourceBehov = new RessourceBehov(nyBehovId, nyRolle,nyStartPeriode, nySlutPeriode, nyAndel, nyTimePris, nyØkonomiType);
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

    // TODO: Syg melding / Barsel

    /*
    =====================
    |      Melding      |
    =====================
    */

    public Melding createMelding(int meldingsId, MeldingType type, LocalDate startDato, LocalDate slutDato, String noter, String medarbejderNavn) throws SQLException {
        Medarbejder medarbejder = dbMedarbejder.readByName(medarbejderNavn);

        if (medarbejder == null) {
            System.out.println("Fejl: Medarbejder ikke fundet - melding ikke oprettet.");
            return null;
        }

        if (startDato.isAfter(slutDato)) {
            System.out.println("Fejl: startDato må ikke være efter slutDato");
        }

        Melding melding = new Melding(meldingsId, type, startDato, slutDato, noter, medarbejder);
        dbMelding.insert(melding);
        return melding;
    }

    public ArrayList<Melding> getAlleMeldinger() throws SQLException {
        return dbMelding.readAll();
    }

    public Melding getMeldingById(int meldingsId) throws SQLException {
        return dbMelding.readById(meldingsId);
    }

    public ArrayList<Melding> getMeldingerForMedarbejder(String søgeord) throws SQLException {
        ArrayList<Melding> meldinger = dbMelding.readByMedarbejder(søgeord);

        System.out.println("Meldinger for '" + søgeord + "':");
        if (meldinger.isEmpty()) {
            System.out.println("    Ingen meldinger registreret.");
        } else {
            for (Melding m : meldinger) {
                System.out.println("    " + m);
            }
        }

        return meldinger;
    }

    public ArrayList<Melding> getMeldingerAfType(MeldingType type) throws SQLException {
        ArrayList<Melding> alle = getAlleMeldinger();
        ArrayList<Melding> resultater = new ArrayList<>();

        for (Melding m : alle) {
            if (m.getType() == type) {
                resultater.add(m);
            }
        }

        System.out.println("Meldinger af type " + type + ":");
        if (resultater.isEmpty()) {
            System.out.println("    Ingen meldinger af denne type.");
        } else {
            for (Melding m : resultater) {
                System.out.println("    " + m);
            }
        }

        return resultater;
    }

    public boolean harAktivMelding(String navn, LocalDate dato) throws SQLException {
        ArrayList<Medarbejder> matches = søgMedarbejderNavn(navn);

        if (matches.isEmpty()) {
            System.out.println("Fejl: Ingen medarbejder fundet med navn.");
            return false;
        }

        Medarbejder medarbejder = matches.getFirst();

        if (matches.size() > 1) {
            System.out.println("Advarsel: " + matches.size() + " medarbejdere fundet med navnet '"
            + navn + "' - tjekker for " + medarbejder.getNavn() +
                    " (" + medarbejder.getInitialer() + ")");
        }

        ArrayList<Melding> meldinger = dbMelding.readByMedarbejder(medarbejder.getNavn());

        for (Melding m : meldinger) {
            if (!dato.isBefore(m.getStartDato()) && !dato.isAfter(m.getSlutDato())) {
                System.out.println(medarbejder.getNavn() + " har en aktiv " +
                        m.getType() + "-melding den " + dato +
                        " (" + m.getStartDato() + " -> " + m.getSlutDato() + ")");
                return true;
            }
        }

        System.out.println(medarbejder.getNavn() + " har ingen aktiv melding den " + dato);
        return false;
    }

    public Melding updateMelding(int meldingsId, MeldingType type, LocalDate startDato, LocalDate slutDato, String noter, String medarbejderNavn) throws SQLException {
        Medarbejder medarbejder = dbMedarbejder.readByName(medarbejderNavn);

        if (medarbejder == null) {
            System.out.println("Fejl: Medarbejder ikke fundet - melding ikke opdateret.");
        }

        if (slutDato.isAfter(startDato)) {
            System.out.println("Fejl: slutDato må ikke være efter startDato.");
        }

        Melding melding = new Melding(meldingsId, type, startDato, slutDato, noter, medarbejder);
        dbMelding.update(melding);
        return melding;
    }

    public void deleteMelding(int meldingsId) throws SQLException {
        dbMelding.delete(meldingsId);
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
     EFTER INTERVIEW/MØDE
     */

    // TODO: Søgning af ledighed på en periode

    /*
     Viser en medarbejders ledighed på alle perioder.

     Givet et navn af medarbejderen, vil der returneres et Map hvor:
        - Er YearMonth (periode)
        - Er værdien for resten af ledigheden

     Eksempel output: { 2026-06 -> 0.75, 2026-08 -> 0.25 osv.. }
    */

    public Map<YearMonth, Double> getLedighedForMedarbejder(String navn) throws SQLException {
        ArrayList<Medarbejder> matches = søgMedarbejderNavn(navn);

        if (matches.isEmpty()) {
            System.out.println("Fejl: Ingen medarbejder fundet med navn: " + navn);
            return new HashMap<>();
        }

        Medarbejder medarbejder = matches.getFirst();

        if (matches.size() > 1.0) {
            System.out.println("Advarsel: " + matches.size() + " medarbejder fundet med navnet '" + navn +
                    "' - viser ledighed for " + medarbejder.getNavn() + " (" + medarbejder.getInitialer() +
                    ", id: " + medarbejder.getMedId() + ")");
        }

        ArrayList<Allokering> allokeringer = getAllokeringerForMedarbejder(medarbejder.getMedId());

        Map<YearMonth, Double> ledighedMap = new HashMap<>();

        for (Allokering a : allokeringer) {
            YearMonth periode = a.getPeriode();
            ledighedMap.put(periode, ledighedMap.getOrDefault(periode, 1.0) - a.getAndel());
        }

        System.out.println("Ledighed for " + medarbejder.getNavn() + " (" + medarbejder.getInitialer() + ")");

        if (ledighedMap.isEmpty()) {
            System.out.println("    Ingen allokeringer fundet - medarbejder er ledig i alle perioder");
        } else {
            ledighedMap.entrySet().stream().sorted(
                    Map.Entry.comparingByKey()).forEach(entry
                    -> System.out.println("    " + entry.getKey() + " -> ledighed: " + entry.getValue()));
        }

        return ledighedMap;
    }

    /*
    Finder alle ledige medarbejdere i en givet periode

    Returnerer et Map hvor
        - Er medarbejderen
        - Er værdien for resten af ledigheden
    */
    public Map<Medarbejder, Double> getLedigeMedarbejdereIPeriode(YearMonth periode) throws SQLException {
        Map<Medarbejder, Double> ledighedMap = beregnLedighedIPeriode(periode);

        System.out.println("Ledige medarbejdere i " + periode);

        if (ledighedMap.isEmpty()) {
            System.out.println("    Ingen ledige medarbejdere fundet i denne periode");
        } else {
            ledighedMap.entrySet().stream().sorted((a, b)
                    -> Double.compare(b.getValue(), a.getValue())).forEach(entry
                    -> System.out.println("  " + entry.getKey().getNavn() + " (" + entry.getKey().getInitialer() + ")"
                    + " -> ledighed: " + entry.getValue()));
        }

        return ledighedMap;
    }

    /*
    Kigger efter medarbejdere med nok ledighed til at dække et specifikt behov.
    Via et givet periode og en påkrævet andel returnere kun medarbejdere
    Som har mindst den ønskede ledighed i den periode

    Eksempel: getLedigeMedarbejdereMedAndel(YearMonth.of(2026, 7), 0.5)
    -> finder alle der har mindst 0.5 ledig i juli 2026
    */

    public Map<Medarbejder, Double> getLedigMedarbejdereMedAndel(YearMonth periode, double påkrævetAndel) throws SQLException {
        Map<Medarbejder, Double> alleLedige = beregnLedighedIPeriode(periode);
        Map<Medarbejder, Double> resultater = new HashMap<>();

        for (Map.Entry<Medarbejder, Double> entry : alleLedige.entrySet()) {
            if (entry.getValue() >= påkrævetAndel) {
                resultater.put(entry.getKey(), entry.getValue());
            }
        }

        System.out.println("Medarbejdere med mindst " + påkrævetAndel + " ledig i " + periode);

        if (resultater.isEmpty()) {
            System.out.println("Ingen medarbejdere har tilstrækkelig ledighed");
        } else {
            resultater.entrySet().stream().sorted((a, b)
                    -> Double.compare(b.getValue(), a.getValue())).forEach(entry
                    -> System.out.println(" " + entry.getKey().getNavn() + " (" + entry.getKey().getInitialer() + ") "
                    + " -> ledighed: " + entry.getValue()));
        }

        return resultater;
    }

    /*
    Hjælpemetode til at beregne ledighed i en periode
    */

    private Map<Medarbejder, Double> beregnLedighedIPeriode(YearMonth periode) throws SQLException {
        ArrayList<Medarbejder> alleMedarbejdere = getAlleMedarbejdere();
        ArrayList<Allokering> alleAllokeringer = getAlleAllokeringer();
        Map<Medarbejder, Double> ledighedMap = new HashMap<>();

        for (Medarbejder m : alleMedarbejdere) {
            double total = 0;

            for (Allokering a : alleAllokeringer) {
                if (a.getPeriode().equals(periode)) {
                    for (Medarbejder am : a.getMedarbejdere()) {
                        if (am.getMedId() == m.getMedId()) {
                            total += a.getAndel();
                        }
                    }
                }
            }
            if (total < 1.0) {
                double ledighed = total > 0 ? 1.0 - total : 0.0;
                ledighedMap.put(m, ledighed);
            }
        }

        return ledighedMap;
    }

    // TODO: Kapacitets melding

    // TODO: Alert at der mangler medarbejder på projekt

    // TODO: simple søgning - navn

    // Søg medarbejder på navn eller initialer
    public ArrayList<Medarbejder> søgMedarbejderNavn(String søgeord) throws SQLException {
        ArrayList<Medarbejder> alle = getAlleMedarbejdere();
        ArrayList<Medarbejder> resultater = new ArrayList<>();
        String lowerCase = søgeord.toLowerCase();

        for (Medarbejder m : alle) {
            if (m.getNavn().toLowerCase().contains(lowerCase) ||
            m.getInitialer().toLowerCase().contains(lowerCase)) {
                resultater.add(m);
            }
        }

        return resultater;
    }

    // Søg Afdeling på navn
    public ArrayList<Afdeling> søgAfdelingNavn(String søgeord) throws SQLException {
        ArrayList<Afdeling> alle = getAlleAfdelinger();
        ArrayList<Afdeling> resultater = new ArrayList<>();
        String lowerCaes = søgeord.toLowerCase();

        for (Afdeling a : alle) {
            if (a.getNavn().toLowerCase().contains(lowerCaes)) {
                resultater.add(a);
            }
        }

        return resultater;
    }

    // Søg Organisation på navn
    public ArrayList<Organisation> søgOrganisationNavn(String søgeord) throws SQLException {
        ArrayList<Organisation> alle = getAlleOrganisationer();
        ArrayList<Organisation> resultater = new ArrayList<>();
        String lowerCase = søgeord.toLowerCase();

        for (Organisation o : alle) {
            if (o.getNavn().toLowerCase().contains(lowerCase)) {
                resultater.add(o);
            }
        }

        return resultater;
    }

    // Søg Team på navn
    public ArrayList<Team> søgTeamNavn(String søgeord) throws SQLException {
        ArrayList<Team> alle = getAlleTeams();
        ArrayList<Team> resultater = new ArrayList<>();
        String lowerCase = søgeord.toLowerCase();

        for (Team t : alle) {
            if (t.getNavn().toLowerCase().contains(lowerCase)) {
                resultater.add(t);
            }
        }

        return resultater;
    }

    // Søg Projekt på navn
    public ArrayList<Projekt> søgProjektNavn(String søgeord) throws SQLException {
        ArrayList<Projekt> alle = getAlleProjekter();
        ArrayList<Projekt> resultater = new ArrayList<>();
        String lowerCase = søgeord.toLowerCase();

        for (Projekt p : alle) {
            if (p.getNavn().toLowerCase().contains(lowerCase)) {
                resultater.add(p);
            }
        }

        return resultater;
    }

    // Søg Fase på navn
    public ArrayList<Fase> søgFaseNavn(String søgeord) throws SQLException {
        ArrayList<Fase> alle = getAlleFaser();
        ArrayList<Fase> resultater = new ArrayList<>();
        String lowerCase = søgeord.toLowerCase();

        for (Fase f : alle) {
            if (f.getNavn().toLowerCase().contains(lowerCase)) {
                resultater.add(f);
            }
        }

        return resultater;
    }

    // Søg RessourceBehov på rolle
    public ArrayList<RessourceBehov> søgRessourceBehovRolle(String søgeord) throws SQLException {
        ArrayList<RessourceBehov> alle = getAlleRessourceBehov();
        ArrayList<RessourceBehov> resultater = new ArrayList<>();
        String lowerCase = søgeord.toLowerCase();

        for (RessourceBehov rb : alle) {
            if (rb.getRolle().toLowerCase().contains(lowerCase)) {
                resultater.add(rb);
            }
        }

        return resultater;
    }

    // Tæller antal medarbejder pr. Team
    public int antalMedarbejderPrTeam(Team team) throws SQLException {
        ArrayList<Medarbejder> medarbejdere = getAlleMedarbejdere();

        int total = 0;

        for (Medarbejder medarbejder : medarbejdere) {
            if (medarbejder.getTeam().equals(team)) {
                total++;
            }
        }

        return total;
    }

}
