package Projekt;

import java.util.ArrayList;

public class Afdeling {
    private String leder;
    private String organisation;
    private String AfdelingsNavn;
    private ArrayList<Medarbejder> medarbejder = new ArrayList<>();


    public Afdeling(String afdelingsNavn, String leder, String organisation) {
        AfdelingsNavn = afdelingsNavn;
        this.leder = leder;
        this.organisation = organisation;
    }

    public String getAfdelingsNavn() {
        return AfdelingsNavn;
    }

    public void setAfdelingsNavn(String afdelingsNavn) {
        AfdelingsNavn = afdelingsNavn;
    }

    public String getLeder() {
        return leder;
    }

    public void setLeder(String leder) {
        this.leder = leder;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    // Todo

    public void addAfdeling(){}

    public void deleteAfdeling(){}



}
