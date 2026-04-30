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

    public String getNavn() {
        return navn;
    }

    public MedarbejderType getType() {
        return type;
    }

    public String getStilling() {
        return stilling;
    }

    public boolean isFratrådt() {
        return fratrådt;
    }

    public Afdeling getAfdeling() {
        return afdeling;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return navn;
    }
}
