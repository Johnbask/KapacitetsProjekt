package Model;

import Model.Enum.ØkonomiType;

import java.util.ArrayList;

public class Projekt {
    private int projektId;
    private String navn;
    // Associations
    private ArrayList<Fase> faser = new ArrayList<>();
    private ArrayList<RessourceBehov> ressourceBehov = new ArrayList<>();
    private ArrayList<Allokering> allokeringer = new ArrayList<>();

    public Projekt(int projektId, String navn, ØkonomiType økonomiType) {
        this.projektId = projektId;
        this.navn = navn;
    }

    // Getters
    public int getProjektId() {
        return projektId;
    }

    public String getNavn() {
        return navn;
    }

    public ArrayList<Fase> getFaser() {
        return faser;
    }

    public ArrayList<RessourceBehov> getRessourceBehov() {
        return ressourceBehov;
    }

    public ArrayList<Allokering> getAllokeringer() {
        return allokeringer;
    }

    // Setters
    public void setProjektId(int projektId) {
        this.projektId = projektId;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setFaser(ArrayList<Fase> faser) {
        this.faser = faser;
    }

    public void setRessourceBehov(ArrayList<RessourceBehov> ressourceBehov) {
        this.ressourceBehov = ressourceBehov;
    }

    public void setAllokeringer(ArrayList<Allokering> allokeringer) {
        this.allokeringer = allokeringer;
    }

    // Hjælpemetoder
    public void addFase(Fase f) {
        if (!faser.contains(f)) faser.add(f);
    }
    public void deleteFase(Fase f) { faser.remove(f); }

    public void addRessourceBehov(RessourceBehov r) {
        if (!ressourceBehov.contains(r)) ressourceBehov.add(r);
    }
    public void deleteRessourceBehov(RessourceBehov r) { ressourceBehov.remove(r); }

    public void addAllokering(Allokering a) { if (!allokeringer.contains(a)) allokeringer.add(a); }
    public void deleteAllokering(Allokering a) { allokeringer.remove(a); }

    @Override
    public String toString() {
        return "Projekt: " + projektId + ", " + navn;
    }
}
