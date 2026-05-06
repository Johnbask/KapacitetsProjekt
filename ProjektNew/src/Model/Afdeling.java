package Model;

import Model.Enum.ØkonomiType;

import java.util.ArrayList;

public class Afdeling {
    private int afdId;
    private String navn;
    private String leder;
    private ØkonomiType økonomiType;
    // Association
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();

    public Afdeling(int afdId, String navn, String leder, ØkonomiType økonomiType) {
        this.afdId = afdId;
        this.navn = navn;
        this.leder = leder;
        this.økonomiType = økonomiType;
    }

    // Getters
    public int getAfdId() {
        return afdId;
    }

    public String getNavn() {
        return navn;
    }

    public String getLeder() {
        return leder;
    }

    public ØkonomiType getØkonomiType() {
        return økonomiType;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }

    // Setters
    public void setAfdId(int afdId) {
        this.afdId = afdId;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setLeder(String leder) {
        this.leder = leder;
    }

    public void setØkonomiType(ØkonomiType økonomiType) {
        this.økonomiType = økonomiType;
    }

    public void setMedarbejdere(ArrayList<Medarbejder> medarbejdere) {
        this.medarbejdere = medarbejdere;
    }

    @Override
    public String toString() {
        return "Afdeling: " + afdId + ", " + navn + ", " + leder + ", " + økonomiType;
    }
}
