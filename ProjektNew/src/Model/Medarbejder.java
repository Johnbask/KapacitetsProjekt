package Model;

import Model.Enum.MedarbejderType;

public class Medarbejder {
    private String initialer;
    private String navn;
    private MedarbejderType type;
    private String stilling;
    private boolean fratrådt;
    private Afdeling afdeling;
    private Organisation organisation;
    private Team team;

    public Medarbejder(String initialer, String navn, MedarbejderType type, String stilling,
                       boolean fratrådt, Afdeling afdeling, Organisation organisation, Team team) {
        this.initialer = initialer;
        this.navn = navn;
        this.type = type;
        this.stilling = stilling;
        this.fratrådt = fratrådt;
        this.afdeling = afdeling;
        this.organisation = organisation;
        this.team = team;
    }

    public String getInitialer() {
        return initialer;
    }
    public void setInitialer(String initialer) {
        this.initialer = initialer;
    }

    public String getNavn() {
        return navn;
    }
    public void setNavn(String navn) {
        this.navn = navn;
    }

    public MedarbejderType getType() {
        return type;
    }
    public void setType(MedarbejderType type) {
        this.type = type;
    }

    public String getStilling() {
        return stilling;
    }
    public void setStilling(String stilling) {
        this.stilling = stilling;
    }

    public boolean isFratrådt() {
        return fratrådt;
    }
    public void setFratrådt(boolean fratrådt) {
        this.fratrådt = fratrådt;
    }

    public Afdeling getAfdeling() {
        return afdeling;
    }
    public void setAfdeling(Afdeling afdeling) {
        this.afdeling = afdeling;
    }

    public Organisation getOrganisation() {
        return organisation;
    }
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }


    // TODO: SØGNING INTERN x EKSTERN

    @Override
    public String toString() {
        return "Medarbejder: " + initialer + ", " + navn + ", " + type + ", " + stilling;
    }
}
