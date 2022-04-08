/**
 * Author: Chi Huu Huynh
 * Login: C00261172
 * Date: 08/04/2022
 */
package customer.invoice.system;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class Product {

    private int productId;
    private int quantity = -1;

    public Product(int productId) {
        this.productId = productId;
    }

    public Product(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    /**
     * Insert a product with the given name, quantity and cost
     * @param name
     * @param quantity
     * @param cost
     * @return 
     */
    public static Packet insertProduct(String name, int quantity, int cost) {
        Account account = Account.getInstance();
        //
        Packet checkSessionIdPacket = Account.checkSessionId(account.getAccountId(), account.getSessionId());
        if (checkSessionIdPacket.getResult() == PacketResult.SUCCESS) { // Check logged in
            DatabaseHandler handler = DatabaseHandler.getInstance();
            if (handler.isConnected()) {
                Object[] args = {0, Company.getCompanyId(), name, quantity, cost};
                // Insert product
                boolean success = handler.insert("Product(productId,companyId,name,quantity,cost) VALUES (?,?,?,?,?)", args);
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
     * Delete a product from DB
     * @param productId
     * @return 
     */
    public static Packet deleteProduct(int productId) {
        Packet companyVerify = Account.companyVerify();
        if (companyVerify.getResult() == PacketResult.SUCCESS) {
                DatabaseHandler handler = DatabaseHandler.getInstance();
                if (handler.isConnected()) {
                    Object[] args = {productId};
                    // Delete
                    boolean success = handler.delete("Product", "productId = ?;", args);
                    if (success) {
                        return new Packet(PacketResult.SUCCESS);
                    }
                    return new Packet(PacketResult.DATABASE_ERROR);
                }
                return new Packet(PacketResult.CONNECTION_ERROR);
            }
        return companyVerify;
    }
    
    /**
     * Update product information using product id
     * @param productId
     * @param name
     * @param cost
     * @param quantity
     * @return 
     */
    public static Packet updateProduct(int productId, String name, int cost, int quantity) {
        Packet companyVerifyPacket = Account.companyVerify();
        if (companyVerifyPacket.getResult() == PacketResult.SUCCESS) {
            DatabaseHandler handler = DatabaseHandler.getInstance();
            if (handler.isConnected()) {
                Object[] args = {name, cost, quantity, productId};
                // Update info
                handler.update("Application.Product SET name=?, cost=?, quantity=? WHERE productId=?", args);
            }
        }
        return companyVerifyPacket;
    }

    /**
     * Get quantity of product from product id and its company id
     * @param companyId Only company own can view
     * @return 
     */
    public Packet getQuantity(int companyId) {
        if (quantity == -1) {
            Packet companyVerifyPacket = Account.companyVerify(); // Check
            if (companyVerifyPacket.getResult() != PacketResult.SUCCESS) {
                DatabaseHandler handler = DatabaseHandler.getInstance();
                if (handler.isConnected()) {
                    Object[] args = {productId, companyId};
                    // Get quantity
                    List<List<Object>> quantityTable = handler.get(" quantity FROM Product WHERE productId=? AND companyId=?", args, 1);
                    if (quantityTable.size() == 1) {
                        quantity = (int) quantityTable.get(0).get(0);
                        return new Packet(PacketResult.SUCCESS, quantity);
                    }
                    return new Packet(PacketResult.DATABASE_ERROR);
                }
            }
            return companyVerifyPacket;
        }
        return new Packet(PacketResult.SUCCESS, quantity);
    }
}
