package Storage;

import Model.Allokering;
import Model.Medarbejder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;

public class DBAllokering extends Storage<Allokering> {
    @Override
    public void insert(Allokering allokering) throws SQLException {
        String query = "INSERT INTO Allokering (allokeringsId, periode, andel, medId, projektId, behovId) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, allokering.getAllokeringsId());
            pstmt.setString(2, allokering.getPeriode().toString());
            pstmt.setDouble(3, allokering.getAndel());
            pstmt.setInt(4, allokering.getMedarbejdere().get(0).getMedId());
            pstmt.setInt(5, allokering.getProjekt().getProjektId());
            pstmt.setInt(6, allokering.getRessourceBehov().getBehovId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Allokering indsat korrekt!");
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
    public ArrayList<Allokering> readAll() throws SQLException {
        String query = "SELECT " +
                "a.allokeringsId, a.periode, a.andel, m.medId, p.projektId, rb.behovId " +
                "FROM Allokering a " +
                "LEFT JOIN Medarbejder m ON a.medId = m.medId " +
                "LEFT JOIN Projekt p ON a.projektId = p.projektId " +
                "LEFT JOIN RessourceBehov rb ON a.behovId = rb.behovId";

        ArrayList<Allokering> allokeringer = new ArrayList<>();

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                allokeringer.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return allokeringer;
    }

    @Override
    public Allokering readById(int id) throws SQLException {
        String query = "SELECT allokeringsId, periode, andel, medId, projektId, behovId " +
                "FROM Allokering WHERE allokeringsId = ?";

        Allokering allokering = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    allokering = helperMethod(rs);
                } else {
                    System.out.println("Ingen allokering fundet med id: " + id);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return allokering;
    }

    @Override
    public void update(Allokering allokering) throws SQLException {
        String query = "UPDATE Allokering " +
                "SET periode = ?, andel = ?, medId = ?, projektId = ?, behovId = ? " +
                "WHERE allokeringsId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, allokering.getPeriode().toString());
            pstmt.setDouble(2, allokering.getAndel());
            pstmt.setInt(3, allokering.getMedarbejdere().get(0).getMedId());
            pstmt.setInt(4, allokering.getProjekt().getProjektId());
            pstmt.setInt(5, allokering.getRessourceBehov().getBehovId());
            pstmt.setInt(6, allokering.getAllokeringsId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Allokering opdateret korrekt!");
            } else {
                System.out.println("Ingen allokering fundet med id: " + allokering.getAllokeringsId());
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
        String query = "DELETE FROM Allokering WHERE allokeringsId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Allokering slettet korrekt!");
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
            case 2627 -> "allokeringsId findes allerede (duplikat-fejl)";
            case 547 -> "medId, projektId eller behovId findes ikke i den tilknyttede tabel (FK-fejl)";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };

        System.out.println("Fejl: " + besked);
    }

    private Allokering helperMethod(ResultSet rs) throws SQLException {
        Allokering allokering = new Allokering(
                rs.getInt("allokeringsId"),
                YearMonth.parse(rs.getString("periode")),
                rs.getDouble("andel")
        );

        Medarbejder medarbejder = new Medarbejder(
                rs.getInt("medId"),
                null, null, null, null, false, null, null, null
        );

        allokering.addMedarbejder(medarbejder);

        return allokering;
    }
}
