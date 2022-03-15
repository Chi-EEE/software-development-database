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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new LoginAccountForm().setVisible(true);
                new CompanyMenu().setVisible(true);
            }
        });
    }
}
