/**
 * Author: Chi Huu Huynh
 * Login: C00261172
 * Date: 08/04/2022
 * Summary: Used to alongside class Packet
 */
package customer.invoice.system;

public enum PacketResult {
    SUCCESS,
    DATABASE_ERROR,
    BAD_REQUEST,
    ACCESS_DENIED,
    ERROR_OCCURRED,
    CONNECTION_ERROR
}