package com.c00261172.customerinvoicesystem;

import java.sql.Date;

/**
 *
 * @author C00261172
 */
public class SignUpHandler extends DatabaseHandler {
    SignUpHandler() {
        super();
    }
    public void insert(String firstName, String lastName, Date DOB, String email, String address, String eircode, String phoneNumber) {
        String sqlParameters[] = {"FirstName, LastName, DOB, Email, Address, Eircode, PhoneNumber"};
        String values[] = {firstName, lastName, DOB, email};
        super.insert("Customers", sqlParameters, values);
    }
}
