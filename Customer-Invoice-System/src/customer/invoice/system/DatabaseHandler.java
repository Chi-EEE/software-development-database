package customer.invoice.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author C00261172
 * @summary Singleton Class for database
 */
public class DatabaseHandler {
    static final String DATABASE_URL = "jdbc:mysql://localhost/Application";
    static final String DATABASE_USER = "root";
    static final String DATABASE_PASSWORD = "password";
    
    private static DatabaseHandler instance = null;
    private Connection connection;
    private PreparedStatement pstat;
    
    public static DatabaseHandler getInstance() {
        if (instance == null) { // Only one DatabaseHandler is created
            try {
                String className = "com.mysql.cj.jdbc.Driver";
                Class.forName(className);
                System.out.println("Load Success");
            } catch (ClassNotFoundException ex) {
                System.out.println("Error in loading jdbc Driver");
                System.out.println(ex.getMessage());
            }
            instance = new DatabaseHandler();
        }
        return instance;
    }
    
    /**
     * @summary Prevent creating databaseHandler outside of class
     */
    private DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean update(String query, Object[] args) {
        int entriesUpdated  = 0;
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
    
    public boolean insert(String query, Object[] args) {
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
    
    public List<List<Object>> get(String query, Object[] args, int rowCount) {
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
    
    protected void finalize() { 
        try {
            if (connection != null) {
                connection.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
}
