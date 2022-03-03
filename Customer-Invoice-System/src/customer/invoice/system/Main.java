package customer.invoice.system;

import java.sql.Date;
import java.util.List;

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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new LoginAccountForm().setVisible(true);
new CompanyMenu().setVisible(true);
            }
        });
    }
}
