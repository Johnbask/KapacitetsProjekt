package Model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Allokering {
    private int allokeringsId;
    private LocalDate år;
    private LocalDate måned;
    private double andel;

    // Association
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();
    private Projekt projekt;
    private RessourceBehov ressourceBehov;

    public Allokering(int allokeringsId, LocalDate år, LocalDate måned, double allokeringAndel) {
        this.allokeringsId = allokeringsId;
        this.år = år;
        this.måned = måned;
        this.andel = allokeringAndel;
    }

    // Getters & Setters

    public int getAllokeringsId() {
        return allokeringsId;
    }
    public void setAllokeringsId(int allokeringsId) {
        this.allokeringsId = allokeringsId;
    }

    public LocalDate getÅr() {
        return år;
    }
    public void setÅr(LocalDate år) {
        this.år = år;
    }

    public LocalDate getMåned() {
        return måned;
    }
    public void setMåned(LocalDate måned) {
        this.måned = måned;
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

    // Hjælpemetoder
    public void addMedarbejder(Medarbejder m) {
        if (!medarbejdere.contains(m)) medarbejdere.add(m);
    }
    public void deleteMedarbejder(Medarbejder m) { medarbejdere.remove(m); }

    @Override
    public String toString() {
        return "Allokering: " + måned + ", " + andel + ", " + medarbejdere.size();
    }
}
