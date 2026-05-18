package Model;

import Model.Enum.ØkonomiType;

import java.time.YearMonth;
import java.util.ArrayList;

public class RessourceBehov {

    private int behovId;
    private String rolle;

    private YearMonth startPeriode;
    private YearMonth slutPeriode;

    private double andel;
    private double timePris;
    private ØkonomiType økonomiType;

    private ArrayList<Allokering> allokeringer = new ArrayList<>();
    private Projekt projekt;

    public RessourceBehov(int behovId,
                          String rolle,
                          YearMonth startPeriode,
                          YearMonth slutPeriode,
                          double andel,
                          double timePris,
                          ØkonomiType økonomiType) {

        this.behovId = behovId;
        this.rolle = rolle;
        this.startPeriode = startPeriode;
        this.slutPeriode = slutPeriode;
        this.andel = andel;
        this.timePris = timePris;
        this.økonomiType = økonomiType;
    }

    public int getBehovId() { return behovId; }
    public String getRolle() { return rolle; }

    public YearMonth getStartPeriode() { return startPeriode; }
    public YearMonth getSlutPeriode() { return slutPeriode; }

    public double getAndel() { return andel; }
    public double getTimePris() { return timePris; }
    public ØkonomiType getØkonomiType() { return økonomiType; }

    public ArrayList<Allokering> getAllokeringer() { return allokeringer; }
    public Projekt getProjekt() { return projekt; }

    public void setProjekt(Projekt projekt) { this.projekt = projekt; }
}