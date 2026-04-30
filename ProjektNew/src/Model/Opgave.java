package Model;

import Model.Enum.OpgaveType;

public class Opgave {
    private int opgaveId;
    private String navn;
    private OpgaveType type;

    public Opgave(int opgaveId, String navn, OpgaveType type) {
        this.opgaveId = opgaveId;
        this.navn = navn;
        this.type = type;
    }

    public int getOpgaveId() {
        return opgaveId;
    }

    public String getNavn() {
        return navn;
    }

    public OpgaveType getType() {
        return type;
    }
}
