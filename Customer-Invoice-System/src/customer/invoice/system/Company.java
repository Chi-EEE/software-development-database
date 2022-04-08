package customer.invoice.system;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author C00261172
 */
public class Company {

    private static int companyId;
    private static String companyName;

    public static int getCompanyId() {
        return companyId;
    }

    public static String getCompanyName() {
        return companyName;
    }

    public static void logout() {
        companyId = -1;
        companyName = "";
    }

    /**
     * Initalises the company information by checking if the account is a
     * company and setting the company id and name to the retrieved values
     *
     * @return
     */
    public static Packet initalise() {
        Account account = Account.getInstance();
        Packet accountTypePacket = account.getAccountType();
        if (accountTypePacket.getResult() == PacketResult.SUCCESS) {
            ArrayList<Object> information = (ArrayList<Object>) accountTypePacket.getInformation();
            AccountType accountType = (AccountType) information.get(0);
            if (accountType == AccountType.COMPANY) {
                // Set values
                companyId = (int) information.get(1);
                companyName = (String) information.get(2);
            }
        }
        return accountTypePacket;
    }

    /**
     * Adds invoice item to invoice by checking the quantity requested and the
     * quantity in stock After that, reduce product quantity and add invoice
     * item to invoice. If another invoice item exists inside of the invoice
     * then increment it
     *
     * @param quantityRequested
     * @param product
     * @param invoice
     * @return
     */
    public static Packet addInvoiceItem(int quantityRequested, Product product, int invoiceId) {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Packet quantityPacket = product.getQuantity(companyId);
            if (quantityPacket.getResult() != PacketResult.SUCCESS) {
                return quantityPacket;
            }
            int productQuantity = (int) quantityPacket.getInformation();
            if (productQuantity >= quantityRequested) {
                if (handler.isConnected()) {
                    // Reduce
                    Object[] args_1 = {productQuantity - quantityRequested, product.getProductId()};
                    handler.update("Application.Product SET quantity=? WHERE productId=?", args_1); // Update quantity

                    Object[] args_2 = {invoiceId, product.getProductId()};
                    // Check if already exists inside of invoice
                    List<List<Object>> qInformation = handler.get("invoiceItemId, quantity FROM Application.InvoiceItem WHERE invoiceId=? AND productId=?", args_2, 1);
                    if (qInformation.size() == 1) {
                        List<Object> information = qInformation.get(0);
                        int invoiceItemId = (int) information.get(0);
                        int invoiceItemQuantity = (int) information.get(1);
                        Object[] args_3 = {invoiceItemQuantity + quantityRequested, invoiceItemId};
                        boolean success = handler.update("Application.InvoiceItem SET quantity=? WHERE invoiceItemId=?", args_3);
                        if (success) { // Add item
                            return new Packet(PacketResult.SUCCESS);
                        }
                        return new Packet(PacketResult.DATABASE_ERROR);
                    } else {
                        Object[] args_4 = {0, invoiceId, product.getProductId(), quantityRequested};
                        boolean success = handler.insert("InvoiceItem(invoiceItemId, invoiceId, productId, quantity) VALUES (?,?,?,?)", args_4);
                        if (success) { // Add item
                            return new Packet(PacketResult.SUCCESS);
                        }
                        return new Packet(PacketResult.DATABASE_ERROR);
                    }
                }
                return new Packet(PacketResult.CONNECTION_ERROR);
            }
            return new Packet(PacketResult.BAD_REQUEST);
        }
        return new Packet(PacketResult.DATABASE_ERROR);
    }

    public static Packet deleteInvoiceItem(int productId, int invoiceId) {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        Product product = new Product(productId);
        Packet quantityPacket = product.getQuantity(companyId);
        if (quantityPacket.getResult() != PacketResult.SUCCESS) {
            return quantityPacket;
        }
        if (handler.isConnected()) {
            Object[] args_1 = {invoiceId, productId};
            List<List<Object>> qInformation = handler.get("quantity FROM Application.InvoiceItem WHERE invoiceId=? AND productId=?", args_1, 1);
            if (qInformation.size() == 1) {
                if (handler.isConnected()) {
                    int itemQuantity = (int) qInformation.get(0).get(0);
                    int quantity = (int) quantityPacket.getInformation();
                    Object[] args_2 = {quantity + itemQuantity, productId};
                    handler.update("Application.Product SET quantity=? WHERE productId=?", args_2); // Update quantity
                    boolean success = handler.delete("Application.InvoiceItem", "invoiceId=? AND productId=?", args_1);
                    if (success) { // Add item
                        return new Packet(PacketResult.SUCCESS);
                    }
                    return new Packet(PacketResult.DATABASE_ERROR);
                }
                return new Packet(PacketResult.CONNECTION_ERROR);
            }
            return new Packet(PacketResult.DATABASE_ERROR);
        }
        return new Packet(PacketResult.CONNECTION_ERROR);
    }
}
