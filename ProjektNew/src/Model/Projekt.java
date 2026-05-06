package Model;

import Model.Enum.ØkonomiType;

import java.util.ArrayList;

public class Projekt {
    private int projektId;
    private String navn;
    private ØkonomiType økonomiType;
    // Associations
    private ArrayList<Fase> faser = new ArrayList<>();
    private ArrayList<RessourceBehov> ressourceBehov = new ArrayList<>();

    public Projekt(int projektId, String navn, ØkonomiType økonomiType) {
        this.projektId = projektId;
        this.navn = navn;
        this.økonomiType = økonomiType;
    }

    // Getters
    public int getProjektId() {
        return projektId;
    }

    public String getNavn() {
        return navn;
    }

    public ØkonomiType getØkonomiType() {
        return økonomiType;
    }

    public ArrayList<Fase> getFaser() {
        return faser;
    }

    public ArrayList<RessourceBehov> getRessourceBehov() {
        return ressourceBehov;
    }

    // Setters
    public void setProjektId(int projektId) {
        this.projektId = projektId;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setØkonomiType(ØkonomiType økonomiType) {
        this.økonomiType = økonomiType;
    }

    public void setFaser(ArrayList<Fase> faser) {
        this.faser = faser;
    }

    public void setRessourceBehov(ArrayList<RessourceBehov> ressourceBehov) {
        this.ressourceBehov = ressourceBehov;
    }

    // Hjælpemetoder
    public void addFase(Fase f) {
        if (!faser.contains(f)) faser.add(f);
    }

    public void addRessourceBehov(RessourceBehov r) {
        if (!ressourceBehov.contains(r)) ressourceBehov.add(r);
    }

    @Override
    public String toString() {
        return "Projekt: " + projektId + ", " + navn + ", " + økonomiType;
    }
}
