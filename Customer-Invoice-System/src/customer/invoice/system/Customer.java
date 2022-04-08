package customer.invoice.system;
import java.util.Date;

/**
 *
 * @author C00261172
 */
public class Customer {
    private int customerId;
    private String address;
    private String eircode;
    private String phoneNumber;

    public Customer(int customerId, String address, String eircode, String phoneNumber) {
        this.customerId = customerId;
        this.address = address;
        this.eircode = eircode;
        this.phoneNumber = phoneNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public String getEircode() {
        return eircode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
}
