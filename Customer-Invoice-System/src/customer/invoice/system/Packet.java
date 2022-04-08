/**
 * Author: Chi Huu Huynh
 * Login: C00261172
 * Date: 08/04/2022
 * Summary: Second most important class for user feedback
 */
package customer.invoice.system;

/*
public enum PacketResult {
    SUCCESS,
    DATABASE_ERROR,
    BAD_REQUEST,
    ACCESS_DENIED,
    ERROR_OCCURRED,
    CONNECTION_ERROR
}
*/
public class Packet {
    private PacketResult result; // Result of the action
    private Object information;
    public Packet(PacketResult result, Object information) {
        this.result = result;
        this.information = information;
    }
    
    public Packet(PacketResult result) {
        this.result = result;
    }
    
    public PacketResult getResult() {
        return result;
    }
    
    public Object getInformation() {
        return information;
    }
}
