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
        List<List<Object>> objects = handler.get("* FROM Application.Account", 2);
        for (int i = 0; i < objects.size(); i++) {
            List<Object> row = objects.get(i);
            for (int j = 0; j < row.size(); j++) {
                System.out.print(row.get(j) + " ");
            }
            System.out.println();
        }
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new CreateAccountForm().setVisible(true);
////              new SelectAccountType().setVisible(true);
//            }
//        });
    }
}
