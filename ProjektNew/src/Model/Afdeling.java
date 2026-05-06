package Model;

import Model.Enum.ØkonomiType;

import java.util.ArrayList;

public class Afdeling {
    private int afdId;
    private String navn;
    private String leder;
    // Association
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();

    public Afdeling(int afdId, String navn, String leder) {
        this.afdId = afdId;
        this.navn = navn;
        this.leder = leder;
    }

    // Getters & Setters
    public int getAfdId() {
        return afdId;
    }
    public void setAfdId(int afdId) {
        this.afdId = afdId;
    }

    public String getNavn() {
        return navn;
    }
    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getLeder() {
        return leder;
    }
    public void setLeder(String leder) {
        this.leder = leder;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }
    public void setMedarbejdere(ArrayList<Medarbejder> medarbejdere) {
        this.medarbejdere = medarbejdere;
    }

    // Hjælpemetoder
    public void addMedarbejder(Medarbejder m) { if (!medarbejdere.contains(m)) medarbejdere.add(m); }
    public void deleteMedarbejder(Medarbejder m) { medarbejdere.remove(m); }

    @Override
    public String toString() {
        return "Afdeling: " + afdId + ", " + navn + ", " + leder;
    }
}
