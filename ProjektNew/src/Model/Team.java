package Model;

import java.util.ArrayList;

public class Team {
    private int teamId;
    private String navn;
    // Associations
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();

    public Team(int teamId, String navn) {
        this.teamId = teamId;
        this.navn = navn;
    }

    // Getters & Setters
    public int getTeamId() {
        return teamId;
    }
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getNavn() {
        return navn;
    }
    public void setNavn(String navn) {
        this.navn = navn;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }
    public void setMedarbejdere(ArrayList<Medarbejder> medarbejdere) {
        this.medarbejdere = medarbejdere;
    }

    // Hjælpemetode
    public void addMedarbejder(Medarbejder m) { if (!medarbejdere.contains(m)) medarbejdere.add(m); }
    public void deleteMedarbejder(Medarbejder m) { medarbejdere.remove(m); }

    @Override
    public String toString() {
        return "(" + teamId + "), " + navn;
    }
}

