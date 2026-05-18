package Storage;

import Model.Medarbejder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Storage<T> {
    private static final String URLJohn = "jdbc:sqlserver://JOHN_LYSPRO\\SQLEXPRESS;databaseName=KapacitetsProjekt;user=sa;password=Frodo3125;";
    private static final String URLLasse = "";

    protected Connection getConnection() throws SQLException {
        try {
            Connection minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");
            return minConnection;
        } catch (SQLException e) {
            System.out.println("Failed to connect to John - Trying Lasse: " + e.getMessage());
            Connection minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
            return minConnection;
        }
    }

    public abstract void insert (T t) throws SQLException;

    public abstract ArrayList<T> readAll() throws SQLException;

    public abstract T readById(int id) throws SQLException;

    public abstract void update(T t) throws SQLException;

    public abstract Medarbejder delete(int id) throws SQLException;

    protected void handleSQLException(SQLException e) {};
}
