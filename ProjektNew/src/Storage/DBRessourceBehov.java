package Storage;

import Model.Enum.ØkonomiType;
import Model.Medarbejder;
import Model.Projekt;
import Model.RessourceBehov;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;

public class DBRessourceBehov extends Storage<RessourceBehov> {
    private final DBProjekt dbProjekt = new DBProjekt();

    @Override
    public void insert(RessourceBehov rb) throws SQLException {

        String query = "INSERT INTO RessourceBehov " +
                "(behovId, rolle, startPeriode, slutPeriode, andel, timePris, økonomiType, projektId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, rb.getBehovId());
            pstmt.setString(2, rb.getRolle());
            pstmt.setString(3, rb.getStartPeriode().toString());
            pstmt.setString(4, rb.getSlutPeriode().toString());
            pstmt.setDouble(5, rb.getAndel());
            pstmt.setDouble(6, rb.getTimePris());
            pstmt.setString(7, rb.getØkonomiType().name());
            pstmt.setInt(8, rb.getProjekt().getProjektId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public ArrayList<RessourceBehov> readAll() throws SQLException {

        String query = "SELECT behovId, rolle, startPeriode, slutPeriode, andel, timePris, økonomiType, projektId " +
                "FROM RessourceBehov";

        ArrayList<RessourceBehov> liste = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                liste.add(helperMethod(rs));
            }

        }

        return liste;
    }

    @Override
    public RessourceBehov readById(int id) throws SQLException {

        String query = "SELECT behovId, rolle, startPeriode, slutPeriode, andel, timePris, økonomiType, projektId " +
                "FROM RessourceBehov WHERE behovId = ?";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return helperMethod(rs);
                }
            }
        }

        return null;
    }

    @Override
    public void update(RessourceBehov rb) throws SQLException {

        String query = "UPDATE RessourceBehov SET " +
                "rolle = ?, startPeriode = ?, slutPeriode = ?, andel = ?, timePris = ?, økonomiType = ?, projektId = ? " +
                "WHERE behovId = ?";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, rb.getRolle());
            pstmt.setString(2, rb.getStartPeriode().toString());
            pstmt.setString(3, rb.getSlutPeriode().toString());
            pstmt.setDouble(4, rb.getAndel());
            pstmt.setDouble(5, rb.getTimePris());
            pstmt.setString(6, rb.getØkonomiType().name());
            pstmt.setInt(7, rb.getProjekt().getProjektId());
            pstmt.setInt(8, rb.getBehovId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public RessourceBehov delete(int id) throws SQLException {

        String query = "DELETE FROM RessourceBehov WHERE behovId = ?";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }

        return null;
    }

    @Override
    protected void handleSQLException(SQLException e) {

        System.out.println("Fejl: " + e.getMessage());

        String besked = switch (e.getErrorCode()) {
            case 2627 -> "Duplikat behovId";
            case 547 -> "FK fejl (projekt findes ikke)";
            default -> "Ukendt SQL fejl: " + e.getErrorCode();
        };

        System.out.println(besked);
    }

    private RessourceBehov helperMethod(ResultSet rs) throws SQLException {

        RessourceBehov rb = new RessourceBehov(
                rs.getInt("behovId"),
                rs.getString("rolle"),
                YearMonth.parse(rs.getString("startPeriode")),
                YearMonth.parse(rs.getString("slutPeriode")),
                rs.getDouble("andel"),
                rs.getDouble("timePris"),
                ØkonomiType.valueOf(rs.getString("økonomiType"))
        );

        int projektId = rs.getInt("projektId");
        Projekt projekt = dbProjekt.readById(projektId);
        rb.setProjekt(projekt);

        return rb;
    }
}