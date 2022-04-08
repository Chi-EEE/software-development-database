/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

    public static Packet insertProduct(String name, int quantity, int cost) {
        Account account = Account.getInstance();
        Packet checkSessionIdPacket = Account.checkSessionId(account.getAccountId(), account.getSessionId());
        if (checkSessionIdPacket.getResult() == PacketResult.SUCCESS) { // Check logged in
            DatabaseHandler handler = DatabaseHandler.getInstance();
            if (handler.isConnected()) {
                Object[] args = {0, Company.getCompanyId(), name, quantity, cost};
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

    public static Packet deleteProduct(int productId) {
        Packet companyVerify = Account.companyVerify();
        if (companyVerify.getResult() == PacketResult.SUCCESS) {
                DatabaseHandler handler = DatabaseHandler.getInstance();
                if (handler.isConnected()) {
                    Object[] args = {productId};
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
    
    public static Packet updateProduct(int productId, String name, int cost, int quantity) {
        Packet companyVerifyPacket = Account.companyVerify();
        if (companyVerifyPacket.getResult() == PacketResult.SUCCESS) {
            DatabaseHandler handler = DatabaseHandler.getInstance();
            if (handler.isConnected()) {
                Object[] args = {name, cost, quantity, productId};
                handler.update("Application.Product SET name=?, cost=?, quantity=? WHERE productId=?", args);
            }
        }
        return companyVerifyPacket;
    }

    public Packet getQuantity(int companyId) {
        if (quantity == -1) {
            Packet companyVerifyPacket = Account.companyVerify();
            if (companyVerifyPacket.getResult() != PacketResult.SUCCESS) {
                DatabaseHandler handler = DatabaseHandler.getInstance();
                if (handler.isConnected()) {
                    Object[] args = {productId, companyId};
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

    public void updatedQuantity() {
        quantity = -1;
    }
}
