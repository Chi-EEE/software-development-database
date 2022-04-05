package customer.invoice.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<InvoiceItem> items;

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

    public void getInvoiceItems() {
        items = new ArrayList<InvoiceItem>(); // Reset Invoice items
        DatabaseHandler handler = DatabaseHandler.getInstance();
        Object[] info = {invoiceId};
        // Get invoice items
        List<List<Object>> invoiceItems = handler.get("invoiceItemId, productId, itemQuantity FROM Application.InvoiceItem WHERE Application.InvoiceItem.invoiceId = ?", info, 1);
        for (List<Object> invoiceItem : invoiceItems) { // Get through invoice item information
            InvoiceItem createdInvoiceItem = new InvoiceItem((int) invoiceItem.get(0), invoiceId, (int) invoiceItem.get(1), (int) invoiceItem.get(2));
            items.add(createdInvoiceItem);
        }
    }
}
