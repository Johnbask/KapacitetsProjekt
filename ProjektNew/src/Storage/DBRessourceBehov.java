package Storage;

import Model.Enum.ØkonomiType;
import Model.RessourceBehov;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;

public class DBRessourceBehov extends DBCRUD<RessourceBehov> {

    @Override
    public void insert(RessourceBehov rb) throws SQLException {
        String query = "INSERT INTO RessourceBehov (behovId, rolle, periode, andel, timePris, økonomiType, projektId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, rb.getBehovId());
            pstmt.setString(2, rb.getRolle());
            pstmt.setString(3, rb.getPeriode().toString());
            pstmt.setDouble(4, rb.getAndel());
            pstmt.setDouble(5, rb.getTimePris());
            pstmt.setString(6, rb.getØkonomiType().name());
            pstmt.setInt(7, rb.getProjekt().getProjektId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("RessourceBehov indsat korrekt!");
            } else {
                System.out.println("Noget gik galt - Ingen data indsat.");
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<RessourceBehov> readAll() throws SQLException {
        String query = "SELECT " +
                "behovId, rolle, periode, andel, timePris, økonomiType, projektId " +
                "FROM RessourceBehov";

        ArrayList<RessourceBehov> liste = new ArrayList<>();

        try  (Connection minConnection = getConnection();
              PreparedStatement pstmt = minConnection.prepareStatement(query);
              ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                liste.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return liste;
    }

    @Override
    public RessourceBehov readById(int id) throws SQLException {
        String query = "SELECT " +
                "behovId, rolle, periode, andel, timePris, økonomiType, projektId " +
                "FROM RessourceBehov " +
                "WHERE behovId = ?";

        RessourceBehov rb = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)){

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    rb = helperMethod(rs);
                } else {
                    System.out.println("Ingen Ressource Behov fundet med id: " + id);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
        return rb;
    }

    @Override
    public void update(RessourceBehov rb) throws SQLException {
        String query = "UPDATE RessourceBehov " +
                "SET rolle = ?, periode = ?, andel = ?, timePris = ?, økonomiType = ?, projektId = ? " +
                "WHERE behovId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, rb.getRolle());
            pstmt.setString(2, rb.getPeriode().toString());
            pstmt.setDouble(3, rb.getAndel());
            pstmt.setDouble(4, rb.getTimePris());
            pstmt.setString(5, rb.getØkonomiType().name());
            pstmt.setInt(6, rb.getProjekt().getProjektId());
            pstmt.setInt(7, rb.getBehovId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("RessourceBehov opdateret korrekt!");
            } else {
                System.out.println("Noget gik galt - Ingen data indsat.");
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM RessourceBehov WHERE behovId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("RessourceBehov deleted korrekt!");
            } else {
                System.out.println("Noget gik galt - Ingen data deleted.");
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void handleSQLException(SQLException e) {
        System.out.println("Fejl: " + e.getMessage());
        System.out.println("Fejlkode: " + e.getErrorCode());

        String besked = switch (e.getErrorCode()) {
            case 2627 -> "behovId findes allerede (duplikat-fejl)";
            case 547 -> "projektId findes ikke i Projekt-tabellen (FK-fejl)";
            case 8152 -> "Værdien er for lang til kolonnen (f.eks. rolle eller økonomiType)";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };

        System.out.println("Fejl: " + besked);
    }

    private RessourceBehov helperMethod(ResultSet rs) throws SQLException {
        return new RessourceBehov(
                rs.getInt("behovId"),
                rs.getString("rolle"),
                YearMonth.parse(rs.getString("periode")),
                rs.getDouble("andel"),
                rs.getDouble("timePris"),
                ØkonomiType.valueOf(rs.getString("økonomiType"))
        );
    }
}
