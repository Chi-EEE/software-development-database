package com.c00261172.customerinvoicesystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author C00261172
 */
public class DatabaseHandler {
    static final String DATABASE_URL = "jdbc:mysql://localhost/CustomerInvoiceSystem";
    static final String DATABASE_USER = "root";
    static final String DATABASE_PASSWORD = "password";
    
    private Connection connection;
    private PreparedStatement pstat;
    DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    protected void insert(String sqlTable, String[] sqlParameters, String[] values) {
        try {
            int entriesCreated = 0;
            pstat = connection.prepareStatement("INSERT INTO " + sqlTable + " (" + sqlParameters[0] + ") VALUES (" + sqlParameters[1] + ")");
            for (int i = 0; i < values.length; i++) {
                pstat.setString(i + 1, values[i]);
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
