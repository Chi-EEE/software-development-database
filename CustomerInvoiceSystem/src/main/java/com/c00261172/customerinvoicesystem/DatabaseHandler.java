package com.c00261172.customerinvoicesystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author C00261172
 * @summary Singleton Class for database
 */
public class DatabaseHandler {
    static final String DATABASE_URL = "jdbc:mysql://localhost/CustomerInvoiceSystem";
    static final String DATABASE_USER = "root";
    static final String DATABASE_PASSWORD = "password";
    
    private static DatabaseHandler instance = null;
    private Connection connection;
    private PreparedStatement pstat;
    
    public static DatabaseHandler getInstance() {
        if (instance == null) { // Only one DatabaseHandler is created
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
    
    public void insert(String query, Object[] args) {
        try {
            int entriesCreated = 0;
            //sqlTable + " (" + sqlParameters[0] + ") VALUES (" + sqlParameters[1]
            pstat = connection.prepareStatement("INSERT INTO " + query);
            for (int i = 0; i < args.length; i++) {
                pstat.setObject(i + 1, args[i]);
            }
            entriesCreated = pstat.executeUpdate();
            if (entriesCreated > 0) {
                System.out.println(entriesCreated + " entries created.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pstat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    protected void finalize() { 
        try {
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
}
