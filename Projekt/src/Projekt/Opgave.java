package Projekt;

import java.util.ArrayList;

public class Opgave {


    private String navn;
    private enum økonomi{CAPEX, OPEX}
    private Medarbejder medarbejder;
    private ArrayList<Projekt> projekter;

    public Opgave(String navn) {
        this.navn = navn;
    }


    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    // Todo

    public void  AddOpgave(){}

    public void  deleteOpagve(){}




}
