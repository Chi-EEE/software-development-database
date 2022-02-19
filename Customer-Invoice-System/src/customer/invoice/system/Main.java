package customer.invoice.system;

import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author C00261172
 */
public class Main {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            String className = "com.mysql.cj.jdbc.Driver";
            Class.forName(className);
            System.out.println("Load Success");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error Success");
        }
            System.out.println("ok");
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        Object[] s = {0, "Chi", "FromJava", new Date(0), "Chi@Java", "IT Carlow", "Carlow", "00000"};
        databaseHandler.insert("Customer(customerId,firstName,lastName,DOB,email,address,eircode,phoneNumber) VALUES (?,?,?,?,?,?,?,?)", s);
    }
}
