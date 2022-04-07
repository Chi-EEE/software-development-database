package customer.invoice.system;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

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

    public Packet setInformation(int customerId, String address, String emailAddress, Date date, String phoneNumber) {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] args = {customerId, address, emailAddress, date, phoneNumber, invoiceId};
            boolean success = handler.update("Application.Invoice SET customerId=?, address=?, emailAddress=?, date=?, phoneNumber=? WHERE invoiceId=?", args);
            if (success) {
                return new Packet(PacketResult.SUCCESS);
            } else {
                return new Packet(PacketResult.DATABASE_ERROR);
            }
        }
        return new Packet(PacketResult.CONNECTION_ERROR);
    }

    public ArrayList<InvoiceItem> getInvoiceItems(Component component) {
        items = new ArrayList<InvoiceItem>(); // Reset Invoice items
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] info = {invoiceId};
            // Get invoice items
            List<List<Object>> invoiceItems = handler.get(" invoiceItemId, productId, itemQuantity FROM Application.InvoiceItem WHERE Application.InvoiceItem.invoiceId = ?", info, 1000);
            for (List<Object> invoiceItem : invoiceItems) { // Get through invoice item information
                InvoiceItem createdInvoiceItem = new InvoiceItem((int) invoiceItem.get(0), invoiceId, (int) invoiceItem.get(1), (int) invoiceItem.get(2));
                items.add(createdInvoiceItem);
            }
        } else {
            JOptionPane.showMessageDialog(component, "Unable to retrieve invoice information - You must be connected to the Database",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
        return items;
    }
}
