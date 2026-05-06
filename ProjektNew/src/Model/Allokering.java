package Model;

import org.junit.platform.commons.util.ToStringBuilder;

import java.lang.reflect.AnnotatedArrayType;
import java.time.LocalDate;
import java.util.ArrayList;

public class Allokering {
    private double allokeringAndel;
    private LocalDate måned;
    private LocalDate år;

    // Association
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();
    private ArrayList<Opgave> opgaver = new ArrayList<>();

    public Allokering(double allokeringAndel, LocalDate måned, LocalDate år) {
        this.allokeringAndel = allokeringAndel;
        this.måned = måned;
        this.år = år;
    }

    // Getters
    public double getAllokeringAndel() {
        return allokeringAndel;
    }

    public LocalDate getMåned() {
        return måned;
    }

    public LocalDate getÅr() {
        return år;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }

    public ArrayList<Opgave> getOpgaver() {
        return opgaver;
    }

    // Setters
    public void setAllokeringAndel(double allokeringAndel) {
        this.allokeringAndel = allokeringAndel;
    }

    public void setMåned(LocalDate måned) {
        this.måned = måned;
    }

    public void setÅr(LocalDate år) {
        this.år = år;
    }

    public void setMedarbejdere(ArrayList<Medarbejder> medarbejdere) {
        this.medarbejdere = medarbejdere;
    }

    public void setOpgaver(ArrayList<Opgave> opgaver) {
        this.opgaver = opgaver;
    }

    // Hjælpemetoder
    public void addMedarbejder(Medarbejder m) {
        if (!medarbejdere.contains(m)) medarbejdere.add(m);
    }

    public void addOpgave(Opgave o) {
        if (!opgaver.contains(o)) opgaver.add(o);
    }

    @Override
    public String toString() {
        return "Allokering: " + måned + ", " + allokeringAndel + ", " + medarbejdere.size() + ", " + opgaver.size();
    }
}
