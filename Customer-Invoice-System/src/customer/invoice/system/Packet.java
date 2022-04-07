package customer.invoice.system;

/**
 *
 * @author C00261172
 */

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
    private PacketResult result;
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
