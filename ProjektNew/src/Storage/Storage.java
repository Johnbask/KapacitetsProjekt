package Storage;

import Model.*;

import java.util.ArrayList;

public class Storage {
    private static ArrayList<Medarbejder> medarbejdere = new ArrayList<>();
    private static ArrayList<Projekt> projekter = new ArrayList<>();
    private static ArrayList<Opgave> opgaver = new ArrayList<>();
    private static ArrayList<Organisation> organisationer = new ArrayList<>();
    private static ArrayList<Team> teams = new ArrayList<>();
    private static ArrayList<Afdeling> afdelinger = new ArrayList<>();
    private static ArrayList<RessourceBehov> ressourceBehovs = new ArrayList<>();

    public static void addMedarbejder (Medarbejder m) { medarbejdere.add(m); }
    public static void deleteMedarbejder(Medarbejder m) { medarbejdere.remove(m); }

    public static void addProjekt(Projekt p) { projekter.add(p); }
    public static void deleteProjekt(Projekt p) { projekter.remove(p); }

    public static void addOpgave(Opgave o) { opgaver.add(o); }
    public static void deleteOpgave(Opgave o) { opgaver.remove(o); }

    public static void addOrganisation(Organisation o) { organisationer.add(o); }
    public static void deleteOrganisation(Organisation o) { organisationer.remove(o); }

    public static void addTeam(Team t) { teams.add(t); }
    public static void deleteTeam(Team t) { teams.remove(t); }

    public static void addAfdeling(Afdeling a) { afdelinger.add(a); }
    public static void deleteAfdeling(Afdeling a) { afdelinger.remove(a); }

    public static void addRessourceBehov(RessourceBehov r) { ressourceBehovs.add(r); }
    public static void deleteRessourceBehov(RessourceBehov r) { ressourceBehovs.remove(r); }

}
