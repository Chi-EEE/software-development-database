/**
 * Author: Chi Huu Huynh
 * Login: C00261172
 * Date: 08/04/2022
 */
package customer.invoice.system;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author C00261172
 */
public class Invoice {

    private int invoiceId;
    private String customer;
    private Date date;
    private String email;
    private String phoneNumber;
    private ArrayList<InvoiceItem> items;

    public Invoice(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Invoice(int invoiceId, String customer, Date date, String email, String phoneNumber) {
        this.invoiceId = invoiceId;
        this.customer = customer;
        this.date = date;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    /**\
     * Sets the information of the invoice from the inputted values
     * @param customerId
     * @param invoiceId
     * @param date
     * @return 
     */
    public static Packet setInformation(int customerId, int invoiceId, Date date) {
        Packet companyVerify = Account.companyVerify();
        if (companyVerify.getResult() == PacketResult.SUCCESS) {
                DatabaseHandler handler = DatabaseHandler.getInstance();
                if (handler.isConnected()) {
                    Object[] args = {customerId, date, invoiceId};
                    boolean success = handler.update("Application.Invoice SET customerId=?, date=? WHERE invoiceId=?", args);
                    if (success) {
                        return new Packet(PacketResult.SUCCESS);
                    } else {
                        return new Packet(PacketResult.DATABASE_ERROR);
                    }
                }
                return new Packet(PacketResult.CONNECTION_ERROR);
            }
            return companyVerify;
        }

    /**
     * Gets all of the invoice items that have the same invoice id as the invoice
     * @param component
     * @return 
     */
    public ArrayList<InvoiceItem> getInvoiceItems(Component component) {
        items = new ArrayList<>(); // Reset Invoice items
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] info = {invoiceId};
            // Get invoice items
            List<List<Object>> invoiceItems = handler.get(" invoiceItemId, productId, quantity, cost FROM Application.InvoiceItem INNER JOIN Application.Product ON Application.Product.productId = Application.InvoiceItem.productId WHERE Application.InvoiceItem.invoiceId = ?", info, 1000);
            for (List<Object> invoiceItem : invoiceItems) { // Get through invoice item information
                InvoiceItem createdInvoiceItem = new InvoiceItem((int) invoiceItem.get(0), invoiceId, (int) invoiceItem.get(1), (int) invoiceItem.get(2), (int) invoiceItem.get(3));
                items.add(createdInvoiceItem);
            }
        } else {
            JOptionPane.showMessageDialog(component, "Unable to retrieve invoice information - You must be connected to the Database",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
        return items;
    }

    /**
     * Creates an invoice with the customer id, company id, and date
     *
     * @param companyId
     * @param customerId
     * @param invoiceDate
     * @return
     */
    public static Packet insertInvoice(int companyId, int customerId, Date invoiceDate) {
        Account account = Account.getInstance();
        Packet checkSessionIdPacket = Account.checkSessionId(account.getAccountId(), account.getSessionId());
        if (checkSessionIdPacket.getResult() == PacketResult.SUCCESS) { // Check logged in
            DatabaseHandler handler = DatabaseHandler.getInstance();
            Object[] args = {0, companyId, customerId, new java.sql.Date(invoiceDate.getTime())};
            if (handler.isConnected()) {
                boolean success = handler.insert("Invoice(invoiceId,companyId,customerId,date) VALUES (?,?,?,?)", args);
                if (success) {
                    return new Packet(PacketResult.SUCCESS);
                } else {
                    return new Packet(PacketResult.DATABASE_ERROR);
                }
            } else {
                return new Packet(PacketResult.CONNECTION_ERROR);
            }
        }
        return checkSessionIdPacket;
    }

    /**
     * Deletes an invoice with the given invoice id
     * @param invoiceId
     * @return 
     */
    public static Packet deleteInvoice(int invoiceId) {
        Packet companyVerify = Account.companyVerify();
        if (companyVerify.getResult() == PacketResult.SUCCESS) {
                DatabaseHandler handler = DatabaseHandler.getInstance();
                if (handler.isConnected()) {
                    Object[] args = {invoiceId};
                    List<List<Object>> invoiceItemList = handler.get("productId FROM Application.InvoiceItem WHERE invoiceId=?", args, 1);
                    if (invoiceItemList.size() == 1) {
                        List<Object> invoiceItems = invoiceItemList.get(0);
                        for (int i = 0; i < invoiceItems.size(); i++) {
                            Company.deleteInvoiceItem((int)invoiceItems.get(i), invoiceId);
                        }
                    }
                    boolean success = handler.delete("Invoice", "invoiceId = ?;", args);
                    if (success) {
                        return new Packet(PacketResult.SUCCESS);
                    }
                    return new Packet(PacketResult.DATABASE_ERROR);
                }
                return new Packet(PacketResult.CONNECTION_ERROR);
            }
        return companyVerify;
    }
}
