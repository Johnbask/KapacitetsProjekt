package Model;

public class Team {
    private int teamId;
    private String navn;

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
}

