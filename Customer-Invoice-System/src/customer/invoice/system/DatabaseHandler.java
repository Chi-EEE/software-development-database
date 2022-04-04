package customer.invoice.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * @author C00261172
 * @summary Singleton Class for database
 */
public class DatabaseHandler {

    static final String DATABASE_URL = "jdbc:mysql://localhost/Application";
    static final String DATABASE_USER = "root";
    static final String DATABASE_PASSWORD = "password";

    private static DatabaseHandler instance = null;
    private Connection connection = null;
    private PreparedStatement pstat = null;

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    private DatabaseHandler() {

    }

    public void connect() {
        try {
            String className = "com.mysql.cj.jdbc.Driver";
            Class.forName(className);
            System.out.println("Loading JDBC drivers successful");
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            System.out.println("Connected to Database");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error in loading jdbc Driver");
        } catch (SQLException sqlException) {
            System.out.println("Error in connecting to Database");
        }
    }

    public static void notConnectedDialog() {
        JOptionPane.showMessageDialog(null, "You must be connected to the Database!",
                "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public boolean update(String query, Object[] args) throws SQLException {
        if (isConnected()) {
            int entriesUpdated = 0;
            try {
                pstat = connection.prepareStatement("UPDATE " + query);

                for (int i = 0; i < args.length; i++) {
                    pstat.setObject(i + 1, args[i]);
                }

                entriesUpdated = pstat.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pstat != null) {
                        pstat.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return entriesUpdated >= 1;
        }
        return false;
    }

    public boolean insert(String query, Object[] args) throws SQLException {
        if (isConnected()) {
            int entriesCreated = 0;
            try {
                pstat = connection.prepareStatement("INSERT INTO " + query);

                for (int i = 0; i < args.length; i++) {
                    pstat.setObject(i + 1, args[i]);
                }

                entriesCreated = pstat.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pstat != null) {
                        pstat.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return entriesCreated >= 1;
        }
        return false;
    }
    
    public boolean delete(String table, String condition, Object[] args) throws SQLException {
        if (isConnected()) {
            int entriesDeleted = 0;
            try {
                pstat = connection.prepareStatement("DELETE FROM " + table + " WHERE " + condition);

                for (int i = 0; i < args.length; i++) {
                    pstat.setObject(i + 1, args[i]);
                }

                entriesDeleted = pstat.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pstat != null) {
                        pstat.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return entriesDeleted >= 1;
        }
        return false;
    }

    public List<List<Object>> get(String query, Object[] args, int rowCount) throws SQLException {
        if (isConnected()) {
            ResultSet resultSet = null;
            List<List<Object>> objects = new ArrayList<List<Object>>();
            try {
                pstat = connection.prepareStatement("SELECT " + query);

                for (int i = 0; i < args.length; i++) {
                    pstat.setObject(i + 1, args[i]);
                }

                resultSet = pstat.executeQuery();

                ResultSetMetaData metaData = resultSet.getMetaData();
                int numberOfColumns = metaData.getColumnCount();
                int count = 0;
                while (resultSet.next() && count < rowCount) {
                    List<Object> row = new ArrayList<Object>();
                    for (int i = 1; i <= numberOfColumns; i++) {
                        row.add(resultSet.getObject(i));
                    }
                    objects.add(row);
                    count++;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return objects;
        }
        return null;
    }

    protected void finalize() {
        try {
            if (isConnected()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
