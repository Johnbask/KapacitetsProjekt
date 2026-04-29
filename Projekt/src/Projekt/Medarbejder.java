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

    public void deleteMedarbejder(Medarbejder medarbejder){
        medarbejder.deleteMedarbejder(medarbejder);
    }

    public void AddMedarbejder(Medarbejder medarbejder){
        medarbejder.AddMedarbejder(medarbejder);
    }

    //  Todo

    /* Metode til at visse den allokeret tid på medarbejderen ex: jun:1 jul:1 aug:1 osv...
    vi vil gerne kunne visse det med alle projekter medarbejdren er på */
    public void  visAllokeretTid(){}


    /*Metode til at "updatere" den tid der allokeres til medarbejderen  */
    public void allokeretTid(){}

    /*I tilfælde af at den allokerede ikke er korekt for medarbejderen (de kunne have en ordning hvor de kun er på halvtid)
    vil vi gerne kunne redigere deres tid til fx 0,5 */
    public void redigereAlokeretTid(){}


    /*Metode der sætter medarbejder til ledig hvis de ikke har allokeret tid (nok mere en hjølpe metode)*/
    public void Ledig(){}



}
