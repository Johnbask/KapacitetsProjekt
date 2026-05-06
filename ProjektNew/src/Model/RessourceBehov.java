package Model;

import java.time.LocalDate;

public class RessourceBehov {

    private int behovId;
    private String stilling;
    private String medarbejderNavn;
    private LocalDate år;
    private LocalDate måned;
    private double andel;
    private double timePris;

    public RessourceBehov(int behovId, String stilling, String medarbejderNavn,
                          LocalDate år, LocalDate måned, double andel, double timePris) {
        this.behovId = behovId;
        this.stilling = stilling;
        this.medarbejderNavn = medarbejderNavn;
        this.år = år;
        this.måned = måned;
        this.andel = andel;
        this.timePris = timePris;
    }

    // Getters
    public int getBehovId() {
        return behovId;
    }

    public String getStilling() {
        return stilling;
    }

    public String getMedarbejderNavn() {
        return medarbejderNavn;
    }

    public LocalDate getÅr() {
        return år;
    }

    public LocalDate getMåned() {
        return måned;
    }

    public double getAndel() {
        return andel;
    }

    public double getTimePris() {
        return timePris;
    }

    // Setters

    public void setBehovId(int behovId) {
        this.behovId = behovId;
    }

    public void setStilling(String stilling) {
        this.stilling = stilling;
    }

    public void setMedarbejderNavn(String medarbejderNavn) {
        this.medarbejderNavn = medarbejderNavn;
    }

    public void setÅr(LocalDate år) {
        this.år = år;
    }

    public void setMåned(LocalDate måned) {
        this.måned = måned;
    }

    public void setAndel(double andel) {
        this.andel = andel;
    }

    public void setTimePris(double timePris) {
        this.timePris = timePris;
    }

    @Override
    public String toString() {
        return "RessourceBehov: " + behovId + ", " + stilling + ", " + andel + ", " + timePris + "kr.";
    }
}
