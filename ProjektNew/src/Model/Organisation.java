package Model;

import java.util.ArrayList;

public class Organisation {
    private int orgId;
    private String navn;
    // Associations
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();

    public Organisation(int orgId, String navn) {
        this.orgId = orgId;
        this.navn = navn;
    }

    // Getters & Setters
    public int getOrgId() {
        return orgId;
    }
    public void setOrgId(int orgId) {
        this.orgId = orgId;
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

    // Hjælpemetoder
    public void addMedarbejder(Medarbejder m) { if (!medarbejdere.contains(m)) medarbejdere.add(m); }
    public void deleteMedarbejder(Medarbejder m) { medarbejdere.remove(m); }

    @Override
    public String toString() {
        return "(" + orgId + "), " + navn;
    }
}
