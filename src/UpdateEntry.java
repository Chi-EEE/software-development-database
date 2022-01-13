import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateEntry {
    static final String DATABASE_URL = "jdbc:mysql://localhost/books";
    public static void main(String[] args) {
        String firstName = "Lisa";
        String lastName = "Brennan";

        Connection connection = null;
        PreparedStatement pstat = null;

        int entriesUpdated = 0;
        try {
            connection = DriverManager.getConnection(
                    DATABASE_URL, "root", "password");
            pstat = connection.prepareStatement("UPDATE Authors SET LASTNAME=? WHERE FirstName=?");
            pstat.setString(1, lastName);
            pstat.setString(2, firstName);
            entriesUpdated = pstat.executeUpdate();
            System.out.println(entriesUpdated + " records successfully updated into the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pstat.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
