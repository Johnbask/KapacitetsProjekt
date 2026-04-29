package Projekt;

import java.lang.reflect.Array;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;

public class Projekt {

    private String navn;
    private String kvartal;
    private YearMonth årMåneder;
    // Association
    private ArrayList<Opgave> opgaver;

    public Projekt(String kvartal, String navn,YearMonth årMåneder) {
        this.kvartal = kvartal;
        this.navn = navn;
        this.årMåneder = årMåneder;
    }

    public String getKvartal() {
        return kvartal;
    }

    public void setKvartal(String kvartal) {
        this.kvartal = kvartal;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public ArrayList<Opgave> getOpgaver() {
        return opgaver;
    }

    public void setOpgaver(ArrayList<Opgave> opgaver) {
        this.opgaver = opgaver;
    }

    public YearMonth getÅrMåneder() {
        return årMåneder;
    }

    public void setÅrMåneder(YearMonth årMåneder) {
        this.årMåneder = årMåneder;
    }

    public void addOpgave (Opgave opgave) {
        if (!opgaver.contains(opgave)) {
            opgaver.add(opgave);
        }
    }

    public void deleteOpgavek(Opgave opgave) {
        opgaver.remove(opgave);
    }

    /*

    */
    @Override
    public String toString() {
        return "navn: " + navn + ", kvartal: " + kvartal + ", årMåneder: " + årMåneder + ", Opgaver: " + opgaver.toString();
    }
}
