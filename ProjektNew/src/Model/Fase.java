package Model;

import java.time.LocalDate;

public class Fase {
    private LocalDate år;
    private LocalDate startMåned;
    private LocalDate slutMåned;

    public Fase(LocalDate år, LocalDate startMåned, LocalDate slutMåned) {
        this.år = år;
        this.startMåned = startMåned;
        this.slutMåned = slutMåned;
    }

    public LocalDate getÅr() {
        return år;
    }

    public LocalDate getStartMåned() {
        return startMåned;
    }

    public LocalDate getSlutMåned() {
        return slutMåned;
    }
}
