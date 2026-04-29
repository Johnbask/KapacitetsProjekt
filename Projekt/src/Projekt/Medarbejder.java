package Projekt;

import java.util.ArrayList;

public class Medarbejder {

    private String navn;
    private String initialer;
    private String stilling;
    private boolean fratrådt;
    private String team;
    private int medarbejderId;
    private enum type {intern,extern}
    private ArrayList<Opgave> opgaver = new ArrayList<>();

    public Medarbejder(boolean fratrådt, String initialer, int medarbejderId,
                       String navn, String stilling, String team) {
        this.fratrådt = fratrådt;
        this.initialer = initialer;
        this.medarbejderId = medarbejderId;
        this.navn = navn;
        this.stilling = stilling;
        this.team = team;
    }

    public boolean isFratrådt() {
        return fratrådt;
    }

    public void setFratrådt(boolean fratrådt) {
        this.fratrådt = fratrådt;
    }

    public String getInitialer() {
        return initialer;
    }

    public void setInitialer(String initialer) {
        this.initialer = initialer;
    }

    public int getMedarbejderId() {
        return medarbejderId;
    }

    public void setMedarbejderId(int medarbejderId) {
        this.medarbejderId = medarbejderId;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getStilling() {
        return stilling;
    }

    public void setStilling(String stilling) {
        this.stilling = stilling;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
