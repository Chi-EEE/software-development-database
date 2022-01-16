import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class AddEntry {
    static final String DATABASE_URL = "jdbc:mysql://localhost:3306/jdbc-test";
    public static void main(String[] args) {

        Connection connection = null;
        PreparedStatement pstat = null;
        ResultSet resultSet = null;

        try {
            // Connect to database
            connection = DriverManager.getConnection(
                    DATABASE_URL, "root", "my-secret-pw"
            );
            pstat = connection.prepareStatement("SELECT AuthorID, FirstName, LastName FROM Authors");

            resultSet = pstat.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            System.out.println("Authors Table of Books Database: \n");

            // Get column names
            for (int i = 1; i <= numberOfColumns; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Go through each entry
            while (resultSet.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    System.out.println(resultSet.getObject(i) + "\t\t");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close to prevent memory leak
                resultSet.close();
                pstat.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
