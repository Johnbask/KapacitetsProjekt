package Model;

import Model.Enum.ØkonomiType;

import java.time.LocalDate;
import java.util.ArrayList;

public class RessourceBehov {

    private int behovId;
    private String rolle;
    private LocalDate år;
    private LocalDate måned;
    private double andel;
    private double timePris;
    private ØkonomiType økonomiType;
    // Associationer
    private ArrayList<Allokering> allokeringer = new ArrayList<>();
    private Projekt projekt;

    public RessourceBehov(int behovId, String rolle, LocalDate år, LocalDate måned,
                          double andel, double timePris, ØkonomiType økonomiType) {
        this.behovId = behovId;
        this.rolle = rolle;
        this.år = år;
        this.måned = måned;
        this.andel = andel;
        this.timePris = timePris;
        this.økonomiType = økonomiType;
    }

    // Getters & Setters
    public int getBehovId() {
        return behovId;
    }
    public void setBehovId(int behovId) {
        this.behovId = behovId;
    }

    public String getRolle() {
        return rolle;
    }
    public void setRolle(String rolle) {
        this.rolle = rolle;
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

    public double getTimePris() {
        return timePris;
    }
    public void setTimePris(double timePris) {
        this.timePris = timePris;
    }

    public ØkonomiType getØkonomiType() {
        return økonomiType;
    }
    public void setØkonomiType(ØkonomiType økonomiType) {
        this.økonomiType = økonomiType;
    }

    public ArrayList<Allokering> getAllokeringer() {
        return allokeringer;
    }
    public void setAllokeringer(ArrayList<Allokering> allokeringer) {
        this.allokeringer = allokeringer;
    }

    public Projekt getProjekt() {
        return projekt;
    }
    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

    @Override
    public String toString() {
        return "RessourceBehov: " + behovId + ", " + rolle + ", " + andel + ", " + timePris + "kr.";
    }
}
