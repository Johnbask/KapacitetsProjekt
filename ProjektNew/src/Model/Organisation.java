package Model;

import java.util.ArrayList;

public class Organisation {
    private int orgId;
    private String navn;
    // Associations
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();
    private ArrayList<Projekt> projekter = new ArrayList<>();

    public Organisation(int orgId, String navn) {
        this.orgId = orgId;
        this.navn = navn;
    }

    public int getOrgId() {
        return orgId;
    }

    public String getNavn() {
        return navn;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }

    public ArrayList<Projekt> getProjekter() {
        return projekter;
    }

    @Override
    public String toString() {
        return "(" + orgId + "), " + navn;
    }
}
