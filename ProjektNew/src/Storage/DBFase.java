package Storage;

import Model.Enum.Kvartal;
import Model.Fase;
import Model.Medarbejder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;

public class DBFase extends Storage<Fase> {
    @Override
    public void insert(Fase fase) throws SQLException {
        String query = "INSERT INTO Fase (faseId, navn, startMåned, slutMåned, kvartal, andel, projektId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, fase.getFaseId());
            pstmt.setString(2, fase.getNavn());
            pstmt.setString(3, fase.getStartMåned().toString());
            pstmt.setString(4, fase.getSlutMåned().toString());
            pstmt.setString(5, fase.getKvartal().name());
            pstmt.setDouble(6, fase.getAndel());
            pstmt.setInt(7, fase.getProjekt().getProjektId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Fase indsat korrekt!");
            } else {
                System.out.println("Noget gik galt - ingen data indsat");
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Fase> readAll() throws SQLException {
        String query = "SELECT faseId, navn, startMåned, slutMåned, kvartal, andel, projektId FROM Fase";

        ArrayList<Fase> faser = new ArrayList<>();

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                faser.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return faser;
    }

    @Override
    public Fase readById(int id) throws SQLException {
        String query = "SELECT faseId, navn, startMåned, slutMåned, kvartal, andel, projektId FROM Fase WHERE faseId = ?";

        Fase fase = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    fase = helperMethod(rs);
                } else {
                    System.out.println("Ingen fase fundet med id: " + id);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return fase;
    }

    @Override
    public void update(Fase fase) throws SQLException {
        String query = "UPDATE Fase " +
                "SET navn = ?, startMåned = ?, slutMåned = ?, kvartal = ?, andel = ?, projektId = ? " +
                "WHERE faseId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, fase.getNavn());
            pstmt.setString(2, fase.getStartMåned().toString());
            pstmt.setString(3, fase.getSlutMåned().toString());
            pstmt.setString(4, fase.getKvartal().name());
            pstmt.setDouble(5, fase.getAndel());
            pstmt.setInt(6, fase.getProjekt().getProjektId());
            pstmt.setInt(7, fase.getFaseId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Fase opdateret korrekt!");
            } else {
                System.out.println("Ingen fase fundet med id: " + fase.getFaseId());
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Medarbejder delete(int id) throws SQLException {
        String query = "DELETE FROM Fase WHERE faseId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Fase slettet korrekt!");
            } else {
                System.out.println("Noget gik galt - ingen data slettet.");
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
            case 2627 -> "faseId findes allerede (duplikat-fejl)";
            case 547 -> "projektId findes ikke i Projekt-tabellen (FK-fejl)";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };

        System.out.println("Fejl: " + besked);
    }

    private Fase helperMethod(ResultSet rs) throws SQLException {
        return new Fase(
                rs.getInt("faseId"),
                rs.getString("navn"),
                YearMonth.parse(rs.getString("startMåned")),
                YearMonth.parse(rs.getString("slutMåned")),
                Kvartal.valueOf(rs.getString("kvartal")),
                rs.getDouble("andel")
        );
    }
}
