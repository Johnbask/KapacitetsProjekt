package Model;

import Model.Enum.ØkonomiType;

import java.time.YearMonth;
import java.util.ArrayList;

public class RessourceBehov {

    private int behovId;
    private String rolle;
    private YearMonth periode;
    private double andel;
    private double timePris;
    private ØkonomiType økonomiType;
    private ArrayList<Allokering> allokeringer = new ArrayList<>();
    private Projekt projekt;

    public RessourceBehov(int behovId,
                          String rolle,
                          YearMonth periode,
                          double andel,
                          double timePris,
                          ØkonomiType økonomiType) {

        this.behovId = behovId;
        this.rolle = rolle;
        this.periode = periode;
        this.andel = andel;
        this.timePris = timePris;
        this.økonomiType = økonomiType;
    }


    public int getBehovId() {
        return behovId;
    }

    public String getRolle() {
        return rolle;
    }

    public YearMonth getPeriode() {
        return periode;
    }

    public double getAndel() {
        return andel;
    }

    public double getTimePris() {
        return timePris;
    }

    public ØkonomiType getØkonomiType() {
        return økonomiType;
    }

    public ArrayList<Allokering> getAllokeringer() {
        return allokeringer;
    }

    public Projekt getProjekt() {
        return projekt;
    }

    // Setters
    public void setBehovId(int behovId) {
        this.behovId = behovId;
    }

    public void setRolle(String rolle) {
        this.rolle = rolle;
    }

    public void setPeriode(YearMonth periode) {
        this.periode = periode;
    }

    public void setAndel(double andel) {
        this.andel = andel;
    }

    public void setTimePris(double timePris) {
        this.timePris = timePris;
    }

    public void setØkonomiType(ØkonomiType økonomiType) {
        this.økonomiType = økonomiType;
    }

    public void setAllokeringer(ArrayList<Allokering> allokeringer) {
        this.allokeringer = allokeringer;
    }

    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

    // Hjælpemetoder
    public void addAllokering(Allokering a) {
        if (!allokeringer.contains(a)) {
            allokeringer.add(a);
        }
    }

    public void deleteAllokering(Allokering a) {
        allokeringer.remove(a);
    }

    @Override
    public String toString() {
        return "RessourceBehov{" +
                "behovId=" + behovId +
                ", rolle='" + rolle + '\'' +
                ", periode=" + periode +
                ", andel=" + andel +
                ", timePris=" + timePris +
                '}';
    }
}