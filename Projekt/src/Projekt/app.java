package Projekt;

import java.time.YearMonth;

public class app {
    public static void main(String[] args) {
        Projekt projekt = new Projekt("Q1", "Energideling", YearMonth.now());

        Opgave op1 = new Opgave("Opgave 1");
        Opgave op2 = new Opgave("Opgave 2");

        projekt.addOpgave(op1);
        projekt.addOpgave(op2);

        System.out.println(projekt);

        Medarbejder m1 = new Medarbejder(false, "JBS", 1001, "John", "Udvikler", "Team Mandalorian");

        projekt.addMedarbejder(m1);

        System.out.println(projekt);
    }
}
