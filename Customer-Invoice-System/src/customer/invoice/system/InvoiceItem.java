package customer.invoice.system;

/**
 *
 * @author C00261172
 */
public class InvoiceItem {
    private int invoiceItemId;
    private int invoiceId;
    private int productId;
    private int productQuantity;
    private int cost;

    public InvoiceItem(int invoiceItemId, int invoiceId, int productId, int productQuantity, int cost) {
        this.invoiceItemId = invoiceItemId;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.productQuantity = productQuantity;
        this.cost = cost;
    }

    public InvoiceItem(int invoiceItemId, int productQuantity) {
        this.invoiceItemId = invoiceItemId;
        this.productQuantity = productQuantity;
    }
    
    public int getInvoiceItemId() {
        return invoiceItemId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public int getProductId() {
        return productId;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public int getCost() {
        return cost;
    }
}
