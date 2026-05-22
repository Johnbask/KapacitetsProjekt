package Model;

import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;

public class Allokering {
    private int allokeringsId;
    private YearMonth startPeriode;
    private YearMonth slutPeriode;
    private double andel;

    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();
    private Projekt projekt;
    private RessourceBehov ressourceBehov;

    public Allokering(int allokeringsId, YearMonth startPeriode, YearMonth slutPeriode, double andel) {
        this.allokeringsId = allokeringsId;
        this.startPeriode = startPeriode;
        this.slutPeriode = slutPeriode;
        this.andel = andel;
    }

    public int getAllokeringsId() {
        return allokeringsId;
    }

    public void setAllokeringsId(int allokeringsId) {
        this.allokeringsId = allokeringsId;
    }

    public YearMonth getStartPeriode() {
        return startPeriode;
    }

    public void setStartPeriode(YearMonth startPeriode) {
        this.startPeriode = startPeriode;
    }

    public YearMonth getSlutPeriode() {
        return slutPeriode;
    }

    public void setSlutPeriode(YearMonth slutPeriode) {
        this.slutPeriode = slutPeriode;
    }

    public double getAndel() {
        return andel;
    }

    public void setAndel(double andel) {
        this.andel = andel;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }
    public void setMedarbejdere(ArrayList<Medarbejder> medarbejdere) {
        this.medarbejdere = medarbejdere;
    }

    public Projekt getProjekt() {
        return projekt;
    }

    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

    public RessourceBehov getRessourceBehov() {
        return ressourceBehov;
    }

    public void setRessourceBehov(RessourceBehov ressourceBehov) {
        this.ressourceBehov = ressourceBehov;
    }

    public void addMedarbejder(Medarbejder m) {
        if (!medarbejdere.contains(m)) {
            medarbejdere.add(m);
        }
    }

    public void deleteMedarbejder(Medarbejder m) {
        medarbejdere.remove(m);
    }

    @Override
    public String toString() {
        return "Allokering: " + startPeriode + ", " + slutPeriode + ", " + andel;
    }
}