package Storage;

import Model.Afdeling;
import Model.Medarbejder;
import Model.Organisation;

import java.sql.*;
import java.util.ArrayList;

public class DBOrganisation extends Storage<Organisation> {

    @Override
    public void insert(Organisation organisation) throws SQLException {
        String query = "INSERT INTO Organisation (orgId, navn) VALUES (?, ?)";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

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
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Organisation> readAll() throws SQLException {
        String query = "SELECT orgId, navn FROM Organisation";

        ArrayList<Organisation> organisationer = new ArrayList<>();

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return organisationer;
    }

    @Override
    public Organisation readById(int id) throws SQLException {
        String query = "SELECT orgId, navn FROM Organisation WHERE orgId = ?";

        Organisation organisation = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

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
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return organisation;
    }

    public Organisation readByName(String navn) throws SQLException {
        String query = "SELECT orgId, navn FROM Organisation WHERE navn = ?";

        Organisation organisation = null;

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, navn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    organisation = helperMethod(rs);
                } else {
                    System.out.println("Ingen organisation fundet med navn: " + navn);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }

        return organisation;
    }

    @Override
    public void update(Organisation organisation) throws SQLException {
        String query = "UPDATE Organisation SET navn = ? WHERE orgId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setString(1, organisation.getNavn());
            pstmt.setInt(2, organisation.getOrgId());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Organisation opdateret korrekt!");
            } else {
                System.out.println("Ingen organisation fundet med id: " + organisation.getOrgId());
            }

        } catch (SQLException e) {
            handleSQLException(e);
        } catch (Exception e) {
            System.out.println("Uventet fejl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Organisation delete(int id) throws SQLException {
        String query = "DELETE FROM Organisation WHERE orgId = ?";

        try (Connection minConnection = getConnection();
             PreparedStatement pstmt = minConnection.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Organisation deleted korrekt!");
            } else {
                System.out.println("Noget gik galt - ingen data deleted.");
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
            case 2627 -> "orgId findes allerede (Duplikat-fejl)";
            case 547 -> "Kan ikke slette organisation - medarbejdere er stadig tilknyttet (FK-fejl)";
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
