package Model;

import Storage.DBMedarbejder;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        DBMedarbejder dbMedarbejder = new DBMedarbejder();

        System.out.println(dbMedarbejder.readAll());
        System.out.println();
        System.out.println(dbMedarbejder.readById(1));
    }
}
