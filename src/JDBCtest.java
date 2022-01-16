import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Notes:
 * Download mySQL connector java and place into a folder called lib. Then go to File > Project Stucture then Modules > Dependencies. Click plus and add JAR file
 * In Command Prompt, use this script: "docker run --name testy -p 3306:3306 -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql" to create a new docker app.
 * In MySQL Workbench, create a new Schema then Right-click tables and create the tables for the entries
 */

public class JDBCtest {
    static final String DATABASE_URL = "jdbc:mysql://localhost:3306/jdbc-test";
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL, "root", "my-secret-pw");

            PreparedStatement pstat = connection.prepareStatement("SELECT * FROM people");

            ResultSet resultSet = pstat.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("firstname"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
