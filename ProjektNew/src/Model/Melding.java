package Model;

import Model.Enum.MeldingType;
import java.time.LocalDate;

public class Melding {
    private int meldingsId;
    private MeldingType type;
    private LocalDate startDato;
    private LocalDate slutDato;
    private String noter;
    // Association
    private Medarbejder medarbejder;

    public Melding(int meldingsId, MeldingType type, LocalDate startDato, LocalDate slutDato, String noter, Medarbejder medarbejder) {
        this.meldingsId = meldingsId;
        this.type = type;
        this.startDato = startDato;
        this.slutDato = slutDato;
        this.noter = noter;
        this.medarbejder = medarbejder;
    }

    public int getMeldingsId() {
        return meldingsId;
    }
    public void setMeldingsId(int meldingsId) {
        this.meldingsId = meldingsId;
    }

    public MeldingType getType() {
        return type;
    }
    public void setType(MeldingType type) {
        this.type = type;
    }

    public LocalDate getStartDato() {
        return startDato;
    }
    public void setStartDato(LocalDate startDato) {
        this.startDato = startDato;
    }

    public LocalDate getSlutDato() {
        return slutDato;
    }
    public void setSlutDato(LocalDate slutDato) {
        this.slutDato = slutDato;
    }

    public String getNoter() {
        return noter;
    }
    public void setNoter(String noter) {
        this.noter = noter;
    }

    public Medarbejder getMedarbejder() {
        return medarbejder;
    }
    public void setMedarbejder(Medarbejder medarbejder) {
        this.medarbejder = medarbejder;
    }

    // TODO: FIX DATO
    // F.eks. indsætter disse: startDato = 2026, 5, 15 | slutDato = 2026, 5, 20
    // Men får disse som output: startDato = 2026, 5, 13 | slutDato = 2026, 5, 18
    // datoerne er altid omkring 2 dage bagud end hvad man indsat

    @Override
    public String toString() {
        return "Melding: [" + meldingsId + "] " + type + " | " + medarbejder.getNavn() +
                " (" + medarbejder.getInitialer() + ") " + " | " + startDato + " -> " +
                slutDato + (noter != null && !noter.isEmpty() ? " | Note: " + noter : "");
    }
}
