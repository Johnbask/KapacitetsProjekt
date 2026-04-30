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
}
