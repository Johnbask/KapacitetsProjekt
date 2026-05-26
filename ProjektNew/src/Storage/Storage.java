package Storage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public abstract class Storage<T> {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = Storage.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new RuntimeException("config.properties ikke fundet!");
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke indlæse config.properties: " + e.getMessage());
        }
    }

    protected Connection getConnection() throws SQLException {
        String[][] configs = {
                {"db.john.url", "db.john.user", "db.john.password"},
                {"db.lasse.url", "db.lasse.user", "db.lasse.password"}
        };

        for (String[] config : configs) {
            String url = props.getProperty(config[0]);
            String user = props.getProperty(config[1]);
            String password = props.getProperty(config[2]);

            if (url == null || url.isEmpty()) continue;

            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connected via " + config[0]);
                return conn;
            } catch (SQLException e) {
                System.out.println("Fejl ved " + config[0] + ": " + e.getMessage());
            }
        }

        throw new SQLException("Kunne ikke oprette forbindelse til nogen database.");
    }

    public abstract void insert (T t) throws SQLException;

    public abstract ArrayList<T> readAll() throws SQLException;

    public abstract T readById(int id) throws SQLException;

    public abstract void update(T t) throws SQLException;

    public abstract T delete(int id) throws SQLException;

    protected void handleSQLException(SQLException e) {};
}
