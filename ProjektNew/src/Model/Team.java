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

    public int getTeamId() {
        return teamId;
    }

    public String getNavn() {
        return navn;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }

    @Override
    public String toString() {
        return "(" + teamId + "), " + navn;
    }
}

