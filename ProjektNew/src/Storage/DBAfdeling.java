package Storage;

import Model.Afdeling;

import java.sql.*;
import java.util.ArrayList;

public class DBAfdeling extends DBCRUD<Afdeling> {
    private static final String URLJohn = "";
    private static final String URLLasse = "";

    @Override
    public void insert(Afdeling afdeling) throws SQLException {
        String felter = "(afdId, navn, leder)";
        String values = "VALUES (?, ?, ?)";
        String query = "INSERT INTO Afdeling " + felter + values;

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, afdeling.getAfdId());
            pstmt.setString(2, afdeling.getNavn());
            pstmt.setString(3, afdeling.getLeder());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Afdeling indsat korrekt!");
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
    public ArrayList<Afdeling> readAll() throws SQLException {
        String query = "SELECT afdId, navn, leder FROM Afdeling";

        ArrayList<Afdeling> afdelinger = new ArrayList<>();

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                afdelinger.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }

        return afdelinger;
    }

    @Override
    public Afdeling readById(int id) throws SQLException {
        String query = "SELECT afdId, navn, leder FROM Afdeling WHERE afdId = ?";

        Afdeling afdeling = null;

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    afdeling = helperMethod(rs);
                } else {
                    System.out.println("Ingen afdeling fundet med id: " + id);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }

        return afdeling;
    }

    @Override
    public void update(Afdeling afdeling) throws SQLException {
        String query = "UPDATE Afdeling SET afdId = ?, navn = ?, leder = ? WHERE afdId = ?";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, afdeling.getAfdId());
            pstmt.setString(2, afdeling.getNavn());
            pstmt.setString(3, afdeling.getLeder());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Afdeling opdateret korrekt!");
            } else {
                System.out.println("Ingen afdelinger fundet med id: " + afdeling.getAfdId());
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
        String query = "DELETE FROM Afdeling WHERE afdId = ?";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Afdeling deleted korrekt!");
            } else {
                System.out.println("Noget gik galt - ingen data deleted.");
            }

        } catch (SQLException e) {
            handleSQLException(e);
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        }
    }

    @Override
    protected void handleSQLException(SQLException e) {
        System.out.println("Fejl: " + e.getMessage());
        System.out.println("Fejlkode: " + e.getErrorCode());

        String besked = switch (e.getErrorCode()) {
            case 2627 -> "afdId findes allerede (duplikat-fejl)";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };

        System.out.println("Fejl: " + besked);
    }

    private Afdeling helperMethod(ResultSet rs) throws SQLException {
        return new Afdeling(
                rs.getInt("afdId"),
                rs.getString("navn"),
                rs.getString("leder")
        );
    }
}
