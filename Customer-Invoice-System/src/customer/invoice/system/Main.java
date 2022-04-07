package customer.invoice.system;

import java.sql.Date;
import java.util.List;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf;

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
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.out.println(e);
        }
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.connect();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new LoginAccountForm(null).setVisible(true);
                new CompanyMenu(null).setVisible(true);
            }
        });
    }
}
