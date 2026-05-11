package Storage;

import Model.Afdeling;
import Model.Enum.MedarbejderType;
import Model.Medarbejder;
import Model.Organisation;
import Model.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBMedarbejder extends DBCRUD<Medarbejder> {
    @Override
    public void insert(Medarbejder m) throws SQLException {
        String query = "INSERT INTO Medarbejder (medId, initialer, navn, medarbejderType, stilling, fratrådt, afdId, orgId, teamId) " +
                "VALUES (?, ?, ?, ?, ?, ? ,? ,?, ?)";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, m.getMedId());
            pstmt.setString(2, m.getInitialer());
            pstmt.setString(3, m.getNavn());
            pstmt.setString(4, m.getType().name());
            pstmt.setString(5, m.getStilling());
            pstmt.setBoolean(6, m.isFratrådt());
            pstmt.setInt(7, m.getAfdeling().getAfdId());
            pstmt.setInt(8, m.getOrganisation().getOrgId());
            pstmt.setInt(9, m.getTeam().getTeamId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Medarbejder indsat korrekt!");
            } else {
                System.out.println("Noget gik galt - Ingen data indsat");
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Medarbejder> readAll() throws SQLException {
        String query = "SELECT m.medId, m.initialer, m.navn, m.medarbejderType, m.stilling, m.fratrådt, " +
                "m.afdId, a.navn AS afdNavn, a.leder, " +
                "m.orgId, o.navn AS orgNavn, " +
                "m.teamId, t.navn AS teamNavn " +
                "FROM Medarbejder m " +
                "LEFT JOIN Afdeling a ON m.afdId = a.afdId " +
                "LEFT JOIN Organisation o ON m.orgId = o.orgId " +
                "LEFT JOIN Team t ON m.teamId = t.teamId";

        ArrayList<Medarbejder> liste = new ArrayList<>();

        try (Connection minConnection = getConnection();
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
    public Medarbejder readById(int id) throws SQLException {
        String query = "SELECT m.medId, m.initialer, m.navn, m.medarbejderType, m.stilling, m.fratrådt, " +
                "m.afdId, a.navn AS afdNavn, a.leder, " +
                "m.orgId, o.navn AS orgNavn, " +
                "m.teamId, t.navn AS teamNavn " +
                "FROM Medarbejder m " +
                "LEFT JOIN Afdeling a ON m.afdId = a.afdId " +
                "LEFT JOIN Organisation o ON m.orgId = o.orgId " +
                "LEFT JOIN Team t ON m.teamId = t.teamId " +
                "WHERE m.medId = ?";

        Medarbejder m = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    m = helperMethod(rs);
                } else {
                    System.out.println("Ingen medarbejder funder med id: " + id);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return m;
    }

    @Override
    public void update(Medarbejder medarbejder) throws SQLException {
        String query = "UPDATE Medarbejder " +
                "SET initialer = ?, navn = ?, medarbejderType = ?, stilling = ?, fratrådt = ?, " +
                "afdId = ?, orgId = ?, teamId = ? " +
                "WHERE medId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, medarbejder.getInitialer());
            pstmt.setString(2, medarbejder.getNavn());
            pstmt.setString(3, medarbejder.getType().name());
            pstmt.setString(4, medarbejder.getStilling());
            pstmt.setBoolean(5, medarbejder.isFratrådt());
            pstmt.setInt(6, medarbejder.getAfdeling().getAfdId());
            pstmt.setInt(7, medarbejder.getOrganisation().getOrgId());
            pstmt.setInt(8, medarbejder.getTeam().getTeamId());
            pstmt.setInt(9, medarbejder.getMedId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Medarbejder opdateret korrekt!");
            } else {
                System.out.println("Noget gik galt - Ingen data opdateret");
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
        String query = "DELETE FROM Medarbejder WHERE medId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Medarbejder slettet korrekt!");
            } else {
                System.out.println("Noget gik galt - Ingen data slettet");
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
        super.handleSQLException(e);
    }

    private Medarbejder helperMethod(ResultSet rs) throws SQLException {
        Afdeling afdeling = new Afdeling(
                rs.getInt("afdId"),
                rs.getString("afdNavn"),
                rs.getString("leder")
        );

        Organisation organisation = new Organisation(
                rs.getInt("orgId"),
                rs.getString("orgNavn")
        );

        Team team = new Team(
                rs.getInt("teamId"),
                rs.getString("teamNavn")
        );

        return new Medarbejder(
          rs.getInt("medId"),
          rs.getString("initialer"),
          rs.getString("navn"),
                MedarbejderType.valueOf(rs.getString("medarbejderType")),
          rs.getString("stilling"),
          rs.getBoolean("fratrådt"),
                afdeling,
                organisation,
                team
        );
    }
}
