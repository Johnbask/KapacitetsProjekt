package Storage;

import Model.Organisation;

import java.sql.*;
import java.util.ArrayList;

public class DBOrganisation extends DBCRUD<Organisation> {
    private static final String URLJohn = "";
    private static final String URLLasse = "";

    @Override
    public void insert(Organisation organisation) throws SQLException {
        String query = "INSERT INTO Organisation (orgId, navn) VALUES (?, ?)";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, organisation.getOrgId());
            pstmt.setString(2, organisation.getNavn());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Organisation indsat korrekt!");
            } else {
                System.out.println("Noget gik galt - ingen data indsat.");
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
    public ArrayList<Organisation> readAll() throws SQLException {
        String query = "SELECT orgId, navn FROM Organisation";

        ArrayList<Organisation> organisationer = new ArrayList<>();

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                organisationer.add(helperMethod(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);

            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }

        return organisationer;
    }

    @Override
    public Organisation readById(int id) throws SQLException {
        String query = "SELECT orgId, navn FROM Organisation WHERE orgId = ?";

        Organisation organisation = null;

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    organisation = helperMethod(rs);
                } else {
                    System.out.println("Ingen organisation fundet med id: " + id);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
            minConnection = DriverManager.getConnection(URLLasse);
            System.out.println("Connected to Lasse");
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
        }

        return organisation;
    }

    @Override
    public void update(Organisation organisation) throws SQLException {
        String query = "UPDATE Organisation SET orgId = ?, navn = ? WHERE orgId = ?";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, organisation.getOrgId());
            pstmt.setString(2, organisation.getNavn());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Organisation opdateret korrekt!");
            } else {
                System.out.println("Ingen organisation fundet med id: " + organisation.getOrgId());
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
        String query = "DELETE FROM Organisation WHERE orgId = ?";

        Connection minConnection;

        try {
            minConnection = DriverManager.getConnection(URLJohn);
            System.out.println("Connected to John");

            PreparedStatement pstmt = minConnection.prepareStatement(query);

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Organisation deleted korrekt!");
            } else {
                System.out.println("Noget gik galt - ingen data deleted.");
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
    protected void handleSQLException(SQLException e) {
        System.out.println("Fejl: " + e.getMessage());
        System.out.println("Fejlkode: " + e.getErrorCode());

        String besked = switch (e.getErrorCode()) {
            case 2627 -> "orgId findes allerede (Duplikat-fejl)";
            default -> "Ukendt fejl [" + e.getErrorCode() + "]: " + e.getMessage();
        };

        System.out.println("Fejl: " + besked);
    }

    private Organisation helperMethod(ResultSet rs) throws SQLException {
        return new Organisation(
                rs.getInt("orgId"),
                rs.getString("navn")
        );
    }
}
