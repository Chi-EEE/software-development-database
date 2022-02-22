/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package customer.invoice.system;

;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author C00261172
 */

public class Account {

    private String username;
    private String email;
    private String phoneNumber;
    private String address;
    private String eircode;

    private int accountId;
    private String sessionId;

    private static Account instance = null;

    public static Account getInstance() {
        if (instance == null) {
            instance = new Account();
        }
        return instance;
    }

    private Account() {

    }

    public boolean login(String username, String password) {
        accountId = getAccountId(username);
        sessionId = requestLogin(username, password);
        if (isLoggedIn()) {
            this.username = username;
            List<List<Object>> result = requestDetails(accountId, sessionId);
            if (result != null) {
                this.email = (String) result.get(0).get(0);
                this.phoneNumber = (String) result.get(0).get(1);
                this.address = (String) result.get(0).get(2);
                this.eircode = (String) result.get(0).get(3);
            }
            System.out.println("Logged in.");
            return true;
        }
        return false;
    }

    public boolean isLoggedIn() {
        return sessionId != "";
    }

    private static boolean hasSubAccount(int accountId, String sessionId) {
        if (checkSessionId(accountId, sessionId)) {
            DatabaseHandler handler = DatabaseHandler.getInstance();
            Object[] info = {accountId};
            List<List<Object>> result_1 = handler.get("accountId FROM Application.Company WHERE accountId = ?", info, 1);
            if (result_1.size() >= 1) {
                return true;
            }
            List<List<Object>> result_2 = handler.get("accountId FROM Application.Customer WHERE accountId = ?", info, 1);
            if (result_2.size() >= 1) {
                return true;
            }
        }
        return false;
    }

    public static AccountCreateResult createAccount(String username, String password, String email, String address, String eircode, String phoneNumber) {
        username = username.toLowerCase();
        if (!accountExists(username)) { // Check if username doesn't exist
            Object[] args = {0, username, password, email, address, eircode, phoneNumber};
            DatabaseHandler handler = DatabaseHandler.getInstance();
            boolean success = handler.insert("Account(accountId,username,password,email,address,eircode,phoneNumber) VALUES (?,?,?,?,?,?,?)", args);
            if (success) {
                return AccountCreateResult.SUCCESS;
            }
            return AccountCreateResult.DATABASE_ERROR;
        }
        return AccountCreateResult.ALREADY_EXISTS;
    }

    private static boolean accountExists(String username) {
        return getAccountId(username) > 0;
    }

    private static int getAccountId(String username) {
        Object[] info = {username};
        DatabaseHandler handler = DatabaseHandler.getInstance();
        List<List<Object>> result = handler.get("accountId FROM Application.Account WHERE username = ?", info, 1);
        if (result.size() >= 1) {
            return (int) result.get(0).get(0);
        }
        return 0;
    }

    private static boolean canLogin(String username, String password) {
        Object[] info = {username, password};
        DatabaseHandler handler = DatabaseHandler.getInstance();
        List<List<Object>> result = handler.get("accountId FROM Application.Account WHERE username = ? AND password = ?", info, 1);
        if (result.size() >= 1) {
            return ((int) result.get(0).get(0) > 0);
        }
        return false;
    }

    private static boolean checkSessionId(int accountId, String sessionId) {
        Object[] info = {accountId, sessionId};
        DatabaseHandler handler = DatabaseHandler.getInstance();
        List<List<Object>> result = handler.get("accountId FROM Application.SessionId WHERE accountId = ? AND sessionId = ?", info, 1);
        if (result.size() >= 1) {
            return ((int) result.get(0).get(0) > 0);
        }
        return false;
    }

    private static String requestLogin(String username, String password) {
        int accountId = getAccountId(username);
        if (accountId > 0) { // Verify if account username exists
            if (canLogin(username, password)) {
                String sessionId = UUID.randomUUID().toString();
                DatabaseHandler handler = DatabaseHandler.getInstance();
                Object[] info = {username, password};
                List<List<Object>> result = handler.get("Application.SessionId.sessionId FROM Application.SessionId JOIN Application.Account ON Application.Account.accountId = Application.SessionId.accountId AND Application.Account.username = ? AND Application.Account.password = ?", info, 1);
                if (result.size() == 0) { // No session id existsApplication.SessionId.sessionId FROM Application.SessionId JOIN Application.Account ON Application.Account.accountId = Application.SessionId.accountId 
                    Object[] args = {accountId, sessionId};
                    boolean success = handler.insert("SessionId(accountId,sessionId) VALUES (?,?)", args);
                } else { // Session id already exists
                    Object[] args = {sessionId, accountId};
                    boolean success = handler.insert("SessionId SET sessionId=? WHERE accountId=?", args);
                }
                return sessionId;
            }
        }
        return "";
    }

    private static List<List<Object>> requestDetails(int accountId, String sessionId) {
        List<List<Object>> result = null;
        if (checkSessionId(accountId, sessionId)) {
            DatabaseHandler handler = DatabaseHandler.getInstance();
            Object[] info = {accountId};
            result = handler.get("email, phoneNumber, address, eircode FROM Application.Account WHERE accountId = ?", info, 1);
        }
        return result;
    }
}
