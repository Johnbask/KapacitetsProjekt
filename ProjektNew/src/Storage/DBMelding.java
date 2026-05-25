package Storage;

import Model.Enum.MedarbejderType;
import Model.Enum.MeldingType;
import Model.Medarbejder;
import Model.Melding;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DBMelding extends Storage<Melding> {

    @Override
    public void insert(Melding melding) throws SQLException {
        String query = "INSERT INTO Melding (meldingsId, typeMelding, startDato, slutDato, noter, medId) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection minConnection = getConnection();
        PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, melding.getMeldingsId());
            pstmt.setString(2, melding.getType().name());
            pstmt.setString(3, melding.getStartDato().toString());
            pstmt.setString(4, melding.getSlutDato().toString());
            pstmt.setString(5, melding.getNoter());
            pstmt.setInt(6, melding.getMedarbejder().getMedId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Melding indsat korrekt!");
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
    public ArrayList<Melding> readAll() throws SQLException {
        String query = "SELECT " +
                "ml.meldingsId, ml.typeMelding, ml.startDato, ml.slutDato, ml.noter, " +
                "m.medId, m.initialer, m.navn, m.medarbejderType, m.stilling, m.fratrådt " +
                "FROM Melding ml " +
                "JOIN Medarbejder m ON ml.medId = m.medId";

        ArrayList<Melding> meldinger = new ArrayList<>();

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                meldinger.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return meldinger;
    }

    @Override
    public Melding readById(int id) throws SQLException {
        String query = "SELECT " +
                "ml.meldingsId, ml.typeMelding, ml.startDato, ml.slutDato, ml.noter, " +
                "m.medId, m.initialer, m.navn, m.medarbejderType, m.stilling, m.fratrådt " +
                "FROM Melding ml " +
                "JOIN Medarbejder m ON ml.medId = m.medId " +
                "WHERE ml.meldingsId = ?";

        Melding melding = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    melding = helperMethod(rs);
                } else {
                    System.out.println("Ingen melding fundet med id: " + id);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return melding;
    }

    public ArrayList<Melding> readByMedarbejder(String søgeord) throws SQLException {
        String query = "SELECT ml.meldingsId, ml.typeMelding, ml.startDato, ml.slutDato, ml.noter, " +
                "m.medId, m.initialer, m.navn, m.medarbejderType, m.stilling, m.fratrådt " +
                "FROM Melding ml " +
                "JOIN Medarbejder m ON ml.medId = m.medId " +
                "WHERE m.navn LIKE ? OR m.initialer LIKE ?";

        ArrayList<Melding> meldinger = new ArrayList<>();

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, "%" + søgeord + "%");
            pstmt.setString(2, "%" + søgeord + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    meldinger.add(helperMethod(rs));
                }
                if (meldinger.isEmpty()) {
                    System.out.println("Ingen meldinger fundet for: " + søgeord);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return meldinger;
    }

    @Override
    public void update(Melding melding) throws SQLException {
        String query = "UPDATE Melding SET typeMelding = ?, startDato = ?, slutDato = ?, noter = ?, medId = ? " +
                "WHERE meldingsId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, melding.getType().name());
            pstmt.setString(2, melding.getStartDato().toString());
            pstmt.setString(3, melding.getSlutDato().toString());
            pstmt.setString(4, melding.getNoter());
            pstmt.setInt(5, melding.getMedarbejder().getMedId());
            pstmt.setInt(6, melding.getMeldingsId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Melding opdateret korrekt!");
            } else {
                System.out.println("Ingen meldinger fundet med id: " + melding.getMeldingsId());
            }
        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Melding delete(int id) throws SQLException {
        String query = "DELETE FROM Melding WHERE meldingsId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Melding deleted korrekt!");
            } else {
                System.out.println("Ingen melding fundet med id: " + id);
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
            case 2627 -> "meldingsId findes allerede (duplikat-fejl)";
            case 547 -> "medId findes ikke i medarbejder (FK-fejl)";
            case 8152 -> "Værdien er for lang til kolonnen (tjek noter eller type)";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };

        System.out.println("Fejl: " + besked);
    }

    private Melding helperMethod(ResultSet rs) throws SQLException {
        Medarbejder medarbejder = new Medarbejder(
                rs.getInt("medId"),
                rs.getString("initialer"),
                rs.getString("navn"),
                MedarbejderType.valueOf(rs.getString("medarbejderType")),
                rs.getString("stilling"),
                rs.getBoolean("fratrådt"),
                null, null, null
        );

        return new Melding(
                rs.getInt("meldingsId"),
                MeldingType.valueOf(rs.getString("typeMelding")),
                rs.getDate("startDato").toLocalDate(),
                rs.getDate("slutDato").toLocalDate(),
                rs.getString("noter"),
                medarbejder
        );
    }
}
