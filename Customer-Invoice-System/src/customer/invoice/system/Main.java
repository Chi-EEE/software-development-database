package customer.invoice.system;

import java.sql.Date;
import java.util.List;
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
        DatabaseHandler handler = DatabaseHandler.getInstance();
        List<Object> objects = handler.get("* FROM Application.Account");
        for (int i = 0; i < objects.size(); i++) {
            System.out.println(objects.get(i));
        }
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new CreateAccountForm().setVisible(true);
////              new SelectAccountType().setVisible(true);
//            }
//        });
    }
}
