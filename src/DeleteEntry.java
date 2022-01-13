import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteEntry {
    final static String DATABASE_URL = "jdbc:mysql://localhost/books";
    public static void main(String[] args) {
        String firstName = "Lisa";
        Connection connection = null;
        PreparedStatement pstat = null;

        int entriesDeleted = 0;
        try {
            connection = DriverManager.getConnection(
                    DATABASE_URL, "root", "password"
            );
            pstat = connection.prepareStatement("DELETE FROM Authors WHERE FirstName=?");
            pstat.setString(1, firstName);

            entriesDeleted = pstat.executeUpdate();
            System.out.println(entriesDeleted + " records deleted from Database");
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
