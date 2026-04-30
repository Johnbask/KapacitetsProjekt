import java.util.ArrayList;

public class Afdeling {
    private int afdId;
    private String navn;
    private String leder;
    private ØkonomiType økonomiType;
    // Association
    private ArrayList<Medarbejder> medarbejdere = new ArrayList<>();

    public Afdeling(int afdId, String navn, String leder, ØkonomiType økonomiType) {
        this.afdId = afdId;
        this.navn = navn;
        this.leder = leder;
        this.økonomiType = økonomiType;
    }

    public int getAfdId() {
        return afdId;
    }

    public String getNavn() {
        return navn;
    }

    public String getLeder() {
        return leder;
    }

    public ØkonomiType getØkonomiType() {
        return økonomiType;
    }

    public ArrayList<Medarbejder> getMedarbejdere() {
        return medarbejdere;
    }
}
