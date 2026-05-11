package Storage;

import Model.Afdeling;
import Model.Enum.MedarbejderType;
import Model.Medarbejder;
import Model.Organisation;
import Model.Team;

import java.sql.*;
import java.util.ArrayList;

public class DBMedarbejder extends DBCRUD<Medarbejder> {

    public void insert(Medarbejder medarbejder) throws SQLException {
        String felter = "(medId, initialer, navn, medarbejderType, stilling, fratrådt, afdId, orgId, teamId)";
        String values = "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String query = "INSERT INTO Medarbejder " + felter + values;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, medarbejder.getMedId());
            pstmt.setString(2, medarbejder.getInitialer());
            pstmt.setString(3, medarbejder.getNavn());
            pstmt.setString(4, medarbejder.getType().name()); // 'INTERN' eller 'EKSTERN'
            pstmt.setString(5, medarbejder.getStilling());
            pstmt.setBoolean(6, medarbejder.isFratrådt());
            pstmt.setInt(7, medarbejder.getAfdeling().getAfdId());
            pstmt.setInt(8, medarbejder.getOrganisation().getOrgId());
            pstmt.setInt(9, medarbejder.getTeam().getTeamId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Medarbejder indsat korrekt!");
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

    public ArrayList<Medarbejder> readAll() throws SQLException {
        String medarbejderFelter =  "m.medId, m.initialer, m.navn, m.medarbejderType, m.stilling, CASE WHEN m.fratrådt = 1 THEN 'True' ELSE 'False' END AS fratrådt, ";
        String afdelingFelter =     "a.afdId, a.navn AS afdNavn, a.leder, ";
        String organisationFelter = "o.orgId, o.navn AS orgNavn, ";
        String teamFelter =         "t.teamId, t.navn AS teamNavn ";

        String innerJoinAfd = "INNER JOIN Afdeling a ON m.afdId = a.afdId ";
        String innerJoinOrg = "INNER JOIN Organisation o ON m.orgId = o.orgId ";
        String innerJoinTeam = "INNER JOIN Team t ON m.teamId = t.teamid";

        String query = "SELECT " + medarbejderFelter + afdelingFelter + organisationFelter + teamFelter
                + " FROM Medarbejder m "
                + innerJoinAfd
                + innerJoinOrg
                + innerJoinTeam;

        ArrayList<Medarbejder> medarbejdere = new ArrayList<>();

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                medarbejdere.add(HelperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return medarbejdere;
    }

    public Medarbejder readById(int medId) throws SQLException {
        String medarbejderFelter =  "m.medId, m.initialer, m.navn, m.medarbejderType, m.stilling, CASE WHEN m.fratrådt = 1 THEN 'True' ELSE 'False' END AS fratrådt, ";
        String afdelingFelter =     "a.afdId, a.navn AS afdNavn, a.leder, ";
        String organisationFelter = "o.orgId, o.navn AS orgNavn, ";
        String teamFelter =         "t.teamId, t.navn AS teamNavn ";

        String innerJoinAfd = "INNER JOIN Afdeling a ON m.afdId = a.afdId ";
        String innerJoinOrg = "INNER JOIN Organisation o ON m.orgId = o.orgId ";
        String innerJoinTeam = "INNER JOIN Team t ON m.teamId = t.teamid ";

        String query = "SELECT " + medarbejderFelter + afdelingFelter + organisationFelter + teamFelter
                + " FROM Medarbejder m "
                + innerJoinAfd
                + innerJoinOrg
                + innerJoinTeam
                + "WHERE m.medId = ?";

        Medarbejder medarbejder = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, medId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                   medarbejder = HelperMethod(rs);
                } else {
                    System.out.println("Ingen medarbejder fundet med id: " + medId);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return medarbejder;
    }

    public void update(Medarbejder medarbejder) throws SQLException {
        String felter = "initialer = ?, navn = ?, medarbejderType = ?, stilling = ?," +
                        " fratrådt = ?, afdId = ?, orgId = ?, teamId = ? ";
        String query = "UPDATE Medarbejder SET " + felter + "WHERE medId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, medarbejder.getMedId());
            pstmt.setString(2, medarbejder.getInitialer());
            pstmt.setString(3, medarbejder.getNavn());
            pstmt.setString(4, medarbejder.getType().name()); // 'INTERN' eller 'EKSTERN'
            pstmt.setString(5, medarbejder.getStilling());
            pstmt.setBoolean(6, medarbejder.isFratrådt());
            pstmt.setInt(7, medarbejder.getAfdeling().getAfdId());
            pstmt.setInt(8, medarbejder.getOrganisation().getOrgId());
            pstmt.setInt(9, medarbejder.getTeam().getTeamId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Medarbejder opdateret korrekt!");
            } else {
                System.out.println("Ingen medarbejder fundet med id: " + medarbejder.getMedId());
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(int medId) throws SQLException {
        String query = "DELETE FROM Medarbejder WHERE medId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, medId);

            int rows = pstmt.executeUpdate();

            if (rows < 0) {
                System.out.println("Medarbejder slettet korrekt!");
            } else {
                System.out.println("Ingen medarbejder fundet med id: " + medId);
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Medarbejder HelperMethod(ResultSet rs) throws SQLException {
        Afdeling afd = new Afdeling(
                rs.getInt("afdId"),
                rs.getString("afdNavn"),
                rs.getString("leder")
        );

        Organisation org = new Organisation(
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
                afd,
                org,
                team
        );
    }

    @Override
    protected void handleSQLException(SQLException e) {
        System.out.println("Fejl: " + e.getMessage());
        System.out.println("Fejlkode: " + e.getErrorCode());

        String besked = switch (e.getErrorCode()) {
            case 2627 -> "medId findes allerede (duplikat-fejl)";
            case 547 -> "FK-Fejl: tjek afdeling, organisation og team eller tjek at medarbejderType enden er 'INTERN' eller 'EKSTERN'";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };

        System.out.println("Fejl: " + besked);
    }
}
