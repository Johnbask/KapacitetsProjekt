package Model;

import Model.Enum.OpgaveType;

public class Opgave {
    private int opgaveId;
    private String navn;
    private OpgaveType type;
    private Allokering allokering;

    public Opgave(int opgaveId, String navn, OpgaveType type) {
        this.opgaveId = opgaveId;
        this.navn = navn;
        this.type = type;
    }

    // Getters
    public int getOpgaveId() {
        return opgaveId;
    }

    public String getNavn() {
        return navn;
    }

    public OpgaveType getType() {
        return type;
    }

    public Allokering getAllokering() {
        return allokering;
    }

    // Setters

    public void setOpgaveId(int opgaveId) {
        this.opgaveId = opgaveId;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setType(OpgaveType type) {
        this.type = type;
    }

    public void setAllokering(Allokering allokering) {
        this.allokering = allokering;
    }

    @Override
    public String toString() {
        return "Opgave: " + opgaveId + "," + navn + ", " + type;
    }
}
