package Controller;

public class Controller {

    // TODO: CRUD AF ALLE KLASSER

    // Singleton
    private static Controller instance;

    private Controller() {}

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }



    // TODO: VIS ALLOKERET TID TIL MEDARBEJDER

    // TODO: CHECK ER 'LEDIG'

    // TODO: CHECK 1..* PROJEKTER

    // TODO: CHECK RB Medarbejders TIDER

    // TODO: ALLOKERLIGELIGT

}
