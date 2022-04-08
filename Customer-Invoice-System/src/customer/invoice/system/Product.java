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

    public Packet getQuantity(int companyId) {
        if (quantity == -1) {
            Account account = Account.getInstance();
            Packet accountTypePacket = account.getAccountType();
            if (accountTypePacket.getResult() == PacketResult.SUCCESS) {
                ArrayList<Object> information = (ArrayList<Object>) accountTypePacket.getInformation();
                AccountType accountType = (AccountType) information.get(0);
                if (accountType == AccountType.COMPANY) {
                    DatabaseHandler handler = DatabaseHandler.getInstance();
                    if (handler.isConnected()) {
                        Object[] args = {companyId, productId};
                        List<List<Object>> quantityTable = handler.get(" productQuantity FROM Product WHERE productId=? AND companyId=?", args, 1);
                        if (quantityTable.size() == 1) {
                            quantity = (int) quantityTable.get(0).get(0);
                            return new Packet(PacketResult.SUCCESS, quantity);
                        }
                        return new Packet(PacketResult.DATABASE_ERROR);
                    }
                    return new Packet(PacketResult.CONNECTION_ERROR);
                }
                return new Packet(PacketResult.ERROR_OCCURRED);
            }
            return accountTypePacket;
        }
        return new Packet(PacketResult.SUCCESS, quantity);
    }

    public void updatedQuantity() {
        quantity = -1;
    }
}
