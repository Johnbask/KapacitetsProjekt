package Storage;

import Model.Team;

import java.sql.*;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;

public class DBTeam extends DBCRUD<Team> {
    private static final String URLJohn = "";
    private static final String URLLasse = "";

    @Override
    public void insert(Team team) throws SQLException {
        String query = "INSERT INTO Team (teamId, navn) VALUES (?, ?)";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

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
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Fejl: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Team> readAll() throws SQLException {
        String query = "SELECT teamId, navn FROM Team";

        ArrayList<Team> teams = new ArrayList<>();

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            PreparedStatement pstmt = minConnection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                teams.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }

        return teams;
    }

    @Override
    public Team readById(int id) throws SQLException {
        String query = "SELECT teamId, navn FROM Team WHERE teamId = ?";

        Team team = null;

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            PreparedStatement pstmt = minConnection.prepareStatement(query);

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
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }

        return team;
    }

    @Override
    public void update(Team team) throws SQLException {
        String query = "UPDATE Team SET teamId = ?, navn = ? WHERE teamId = ?";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, team.getTeamId());
            pstmt.setString(2, team.getNavn());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Team opdateret korrekt!");
            } else {
                System.out.println("Ingen team fundet med id: " + team.getTeamId());
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
        String query = "DELETE FROM Team WHERE teamId = ?";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Team deleted korret!");
            } else {
                System.out.println("Noget gik galt - Ingen data deleted");
            }

        } catch (SQLException e) {
            handleSQLException(e);
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
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
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };
        System.out.println("Fejl: " + besked);
    }
}
