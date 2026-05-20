package Storage;

import Model.Medarbejder;
import Model.Projekt;

import java.sql.*;
import java.util.ArrayList;

public class DBProjekt extends Storage<Projekt> {

    @Override
    public void insert(Projekt projekt) throws SQLException {
        String query = "INSERT INTO Projekt (projektId, navn) VALUES (?, ?)";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

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
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Projekt> readAll() throws SQLException {
        String query = "SELECT projektId, navn FROM Projekt";

        ArrayList<Projekt> projekter = new ArrayList<>();

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery();) {

            while (rs.next()) {
                projekter.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return projekter;
    }

    @Override
    public Projekt readById(int id) throws SQLException {
        String query = "SELECT projektId, navn FROM Projekt WHERE projektId = ?";

        Projekt projekt = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

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
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return projekt;
    }

    @Override
    public void update(Projekt projekt) throws SQLException {
        String query = "UPDATE Projekt SET navn = ? WHERE projektId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, projekt.getNavn());
            pstmt.setInt(2, projekt.getProjektId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Projekt opdateret korrekt!");
            } else {
                System.out.println("Ingen projekter fundet med id: " + projekt.getProjektId());
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Projekt delete(int id) throws SQLException {
        String query = "DELETE FROM Projekt WHERE projektId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Projekt deleted korrekt!");
            } else {
                System.out.println("Noget gik galt - ingen data deleted.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void handleSQLException(SQLException e) {
        System.out.println("Fejl: " + e.getMessage());
        System.out.println("Fejlkode: " + e.getErrorCode());

        String besked = switch (e.getErrorCode()) {
            case 2627 -> "projektId findes allerede (duplikat-fejl)";
            case 547 -> "Kan ikke slette projekt - tilknyttet ressourceBehov (FK-fejl)";
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
