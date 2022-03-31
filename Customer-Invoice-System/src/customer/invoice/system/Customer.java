package customer.invoice.system;
import java.util.Date;

/**
 *
 * @author C00261172
 */
public class Customer {
    private int customerId;
    private Title title;
    private String firstName;
    private String lastName;
    private String email;
    private Date dob;
    private String phoneNumber;

    public Customer(int customerId, Title title, String firstName, String lastName, String email, Date dob, String phoneNumber) {
        this.customerId = customerId;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
    }
    
}
