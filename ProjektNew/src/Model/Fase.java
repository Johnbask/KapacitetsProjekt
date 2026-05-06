package Model;

import Model.Enum.Kvartal;

import java.time.LocalDate;

public class Fase {
    private int faseId;
    private String navn;
    private LocalDate år;
    private LocalDate startMåned;
    private LocalDate slutMåned;
    private Kvartal kvartal;
    private double andel; // 0.25, 0.5, 1, 1.25 osv..
    // Associations
    private Team team;
    private Projekt projekt;

    public Fase(int faseId, String navn, LocalDate år, LocalDate startMåned, LocalDate slutMåned, Kvartal kvartal, double andel) {
        this.faseId = faseId;
        this.navn = navn;
        this.år = år;
        this.startMåned = startMåned;
        this.slutMåned = slutMåned;
        this.kvartal = kvartal;
        this.andel = andel;
    }

    // Getters
    public int getFaseId() {
        return faseId;
    }

    public String getNavn() {
        return navn;
    }

    public LocalDate getÅr() {
        return år;
    }

    public LocalDate getStartMåned() {
        return startMåned;
    }

    public LocalDate getSlutMåned() {
        return slutMåned;
    }

    public Kvartal getKvartal() {
        return kvartal;
    }

    public double getAndel() {
        return andel;
    }

    public Team getTeam() {
        return team;
    }

    public Projekt getProjekt() {
        return projekt;
    }
    // Setters

    public void setFaseId(int faseId) {
        this.faseId = faseId;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setÅr(LocalDate år) {
        this.år = år;
    }

    public void setStartMåned(LocalDate startMåned) {
        this.startMåned = startMåned;
    }

    public void setSlutMåned(LocalDate slutMåned) {
        this.slutMåned = slutMåned;
    }

    public void setKvartal(Kvartal kvartal) {
        this.kvartal = kvartal;
    }

    public void setAndel(double andel) {
        this.andel = andel;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

    @Override
    public String toString() {
        return "Fase: " + faseId + ", " + navn + ", " + team + ", " + projekt + ", startMåned: " +  startMåned + ", slutMåned: " + slutMåned;
    }
}
