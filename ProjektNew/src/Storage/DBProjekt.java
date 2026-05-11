package Storage;

import Model.Projekt;

import java.awt.image.RescaleOp;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

public class DBProjekt extends DBCRUD<Projekt> {
    private static final String URLJohn = "";
    private static final String URLLasse = "";

    @Override
    public void insert(Projekt projekt) throws SQLException {
        String query = "INSERT INTO Projekt (projektId, navn) VALUES (?, ?)";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, projekt.getProjektId());
            pstmt.setString(2, projekt.getNavn());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Projekt indsat korret!");
            } else {
                System.out.println("Noget gik galt - ingen data indsat.");
            }

        } catch (SQLException e) {
            handleSQLException(e);

            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Projekt> readAll() throws SQLException {
        String query = "SELECT projektId, navn FROM Projekt";

        ArrayList<Projekt> projekter = new ArrayList<>();

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                projekter.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);

            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }

        return projekter;
    }

    @Override
    public Projekt readById(int id) throws SQLException {
        String query = "SELECT projektId, navn FROM Projekt WHERE projektId = ?";

        Projekt projekt = null;

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    projekt = helperMethod(rs);
                } else {
                    System.out.println("Ingen projekter fundet med id: " + id);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);

            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }

        return projekt;
    }

    @Override
    public void update(Projekt projekt) throws SQLException {
        String query = "";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, projekt.getProjektId());
            pstmt.setString(2, projekt.getNavn());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Projekt opdateret korrekt!");
            } else {
                System.out.println("Ingen projekter fundet med id: " + projekt.getProjektId());
            }
        } catch (SQLException e) {
            handleSQLException(e);

            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Projekt deleted korrekt!");
            } else {
                System.out.println("Noget gik galt - ingen data deleted.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }
    }

    @Override
    protected void handleSQLException(SQLException e) {
        System.out.println("Fejl: " + e.getMessage());
        System.out.println("Fejlkode: " + e.getErrorCode());

        String besked = switch (e.getErrorCode()) {
            case 2627 -> "projektId findes allerede (duplikat-fejl)";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };

        System.out.println("Fejl: " + besked);
    }

    private Projekt helperMethod(ResultSet rs) throws SQLException {
        return new Projekt(
                rs.getInt("projektId"),
                rs.getString("navn")
        );
    }
}
