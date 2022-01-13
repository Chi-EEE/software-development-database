import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GetEntries {
    static final String DATABASE_URL = "jdbc:mysql://localhost/books";

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement pstat = null;
        String firstName = "Lisa";
        String lastName = "Smith";

        int recordsAdded = 0;
        try {
            connection = DriverManager.getConnection(DATABASE_URL, "root", "password");

            // make a statement to use for the database
            pstat = connection.prepareStatement("INSERT INTO Authors (FirstName, LastNName) VALUES (?,?)");
            pstat.setString(1, firstName);
            pstat.setString(2, lastName);

            recordsAdded = pstat.executeUpdate(); // When updating database then use executeUpdate else executeQuery
            System.out.println(recordsAdded + " records successfully added into the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Attempt to close pstat and connection to prevent memory leak
            try {
                pstat.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
