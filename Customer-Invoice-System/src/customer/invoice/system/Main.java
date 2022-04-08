/**
 * Author: Chi Huu Huynh
 * Login: C00261172
 * Date: 08/04/2022
 * Summary: Main driver
 */
package customer.invoice.system;

import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.out.println(e);
        }
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.connect();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginAccountForm(null).setVisible(true);
            }
        });
    }
}
