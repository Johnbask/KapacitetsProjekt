package Model;

import Model.Enum.Kvartal;

import java.time.LocalDate;
import java.time.YearMonth;

public class Fase {
    private int faseId;
    private String navn;
    private YearMonth startMåned;
    private YearMonth slutMåned;
    private Kvartal kvartal;
    private double andel; // 0.25, 0.5, 1, 1.25 osv..
    // Associations
    private Projekt projekt;

    public Fase(int faseId, String navn, YearMonth startMåned, YearMonth slutMåned, Kvartal kvartal, double andel) {
        this.faseId = faseId;
        this.navn = navn;
        this.startMåned = startMåned;
        this.slutMåned = slutMåned;
        this.kvartal = kvartal;
        this.andel = andel;
    }

    // Getters & Setters

    public int getFaseId() {
        return faseId;
    }
    public void setFaseId(int faseId) {
        this.faseId = faseId;
    }

    public String getNavn() {
        return navn;
    }
    public void setNavn(String navn) {
        this.navn = navn;
    }

    public YearMonth getStartMåned() {
        return startMåned;
    }
    public void setStartMåned(YearMonth startMåned) {
        this.startMåned = startMåned;
    }

    public YearMonth getSlutMåned() {
        return slutMåned;
    }
    public void setSlutMåned(YearMonth slutMåned) {
        this.slutMåned = slutMåned;
    }

    public Kvartal getKvartal() {
        return kvartal;
    }
    public void setKvartal(Kvartal kvartal) {
        this.kvartal = kvartal;
    }

    public double getAndel() {
        return andel;
    }
    public void setAndel(double andel) {
        this.andel = andel;
    }

    public Projekt getProjekt() {
        return projekt;
    }
    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

    @Override
    public String toString() {
        return "Fase: " + faseId + ", " + navn + ", " + projekt + ", startMåned: " +  startMåned + ", slutMåned: " + slutMåned;
    }
}
