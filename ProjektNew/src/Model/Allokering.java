package Model;

import org.junit.platform.commons.util.ToStringBuilder;

import java.lang.reflect.AnnotatedArrayType;
import java.time.LocalDate;
import java.util.ArrayList;

public class Allokering {
    private double andel;
    private LocalDate måned;
    private LocalDate år;

    // Association
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();

    public Allokering(double allokeringAndel, LocalDate måned, LocalDate år) {
        this.andel = allokeringAndel;
        this.måned = måned;
        this.år = år;
    }

    // Getters & Setters
    public double getAndel() {
        return andel;
    }
    public void setAndel(double andel) {
        this.andel = andel;
    }

    public LocalDate getMåned() {
        return måned;
    }
    public void setMåned(LocalDate måned) {
        this.måned = måned;
    }

    public LocalDate getÅr() {
        return år;
    }
    public void setÅr(LocalDate år) {
        this.år = år;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }
    public void setMedarbejdere(ArrayList<Medarbejder> medarbejdere) {
        this.medarbejdere = medarbejdere;
    }

    // Hjælpemetoder
    public void addMedarbejder(Medarbejder m) {
        if (!medarbejdere.contains(m)) medarbejdere.add(m);
    }
    public void deleteMedarbejder(Medarbejder m) { medarbejdere.remove(m); }

    @Override
    public String toString() {
        return "Allokering: " + måned + ", " + andel + ", " + medarbejdere.size();
    }
}
