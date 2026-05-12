package Storage;

import Model.Team;

import java.sql.*;
import java.util.ArrayList;

public class DBTeam extends Storage<Team> {

    @Override
    public void insert(Team team) throws SQLException {
        String query = "INSERT INTO Team (teamId, navn) VALUES (?, ?)";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, team.getTeamId());
            pstmt.setString(2, team.getNavn());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Team indsat korrekt!");
            } else {
                System.out.println("Noget gik galt - Ingen data indsat.");
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Team> readAll() throws SQLException {
        String query = "SELECT teamId, navn FROM Team";

        ArrayList<Team> teams = new ArrayList<>();

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                teams.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return teams;
    }

    @Override
    public Team readById(int id) throws SQLException {
        String query = "SELECT teamId, navn FROM Team WHERE teamId = ?";

        Team team = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    team = helperMethod(rs);
                } else {
                    System.out.println("Ingen team fundet med id: " + id);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return team;
    }

    @Override
    public void update(Team team) throws SQLException {
        String query = "UPDATE Team SET navn = ? WHERE teamId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, team.getNavn());
            pstmt.setInt(2, team.getTeamId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Team opdateret korrekt!");
            } else {
                System.out.println("Ingen team fundet med id: " + team.getTeamId());
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
        String query = "DELETE FROM Team WHERE teamId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Team deleted korret!");
            } else {
                System.out.println("Noget gik galt - Ingen data deleted");
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Team helperMethod(ResultSet rs) throws SQLException {
        return new Team(
          rs.getInt("teamId"),
          rs.getString("navn")
        );
    }

    @Override
    protected void handleSQLException(SQLException e) {
        System.out.println("Fejl: " + e.getMessage());
        System.out.println("Fejlkode: " + e.getErrorCode());

        String besked = switch (e.getErrorCode()) {
            case 2627 -> "teamId findes allerede (Duplikat-fejl)";
            case 547 -> "Kan ikke slette team - medarbejder er stadig tilknyttet (FK-Fejl)";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };
        System.out.println("Fejl: " + besked);
    }
}
