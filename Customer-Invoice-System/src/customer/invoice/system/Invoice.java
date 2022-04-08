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

    /**
     * Set invoice information (customer and date)
     * @param customerId
     * @param date
     * @return 
     */
    public Packet setInformation(int customerId, Date date) {
        Account account = Account.getInstance();
        Packet accountTypePacket = account.getAccountType();
        if (accountTypePacket.getResult() == PacketResult.SUCCESS) { // Successful
            ArrayList<Object> information = (ArrayList<Object>) accountTypePacket.getInformation();
            AccountType accountType = (AccountType) information.get(0);
            if (accountType == AccountType.COMPANY) { // Check account type
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
            return new Packet(PacketResult.ACCESS_DENIED);
        }
        return accountTypePacket;
    }

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

    public static Packet deleteInvoice(Invoice invoice) {
        Account account = Account.getInstance();
        Packet accountTypePacket = account.getAccountType();
        if (accountTypePacket.getResult() == PacketResult.SUCCESS) {
            ArrayList<Object> information = (ArrayList<Object>) accountTypePacket.getInformation();
            AccountType accountType = (AccountType) information.get(0);
            if (accountType == AccountType.COMPANY) {
                DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
                if (databaseHandler.isConnected()) {
                    Object[] args = {invoice.getInvoiceId()};
                    boolean success = databaseHandler.delete("Invoice", "invoiceId = ?;", args);
                    if (success) {
                        return new Packet(PacketResult.SUCCESS);
                    }
                    return new Packet(PacketResult.DATABASE_ERROR);
                }
                return new Packet(PacketResult.CONNECTION_ERROR);
            }
            return new Packet(PacketResult.ACCESS_DENIED);
        }
        return accountTypePacket;
    }
}
