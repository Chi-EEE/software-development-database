/**
 * Author: Chi Huu Huynh
 * Login: C00261172
 * Date: 08/04/2022
 */
package customer.invoice.system;

import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.time.LocalDateTime;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;

public class CompanyMenu extends javax.swing.JFrame {

    /**
     * Creates new form SignUpForm
     *
     * @param component
     */
    public CompanyMenu(Component component) {
        Company.initalise();
        initComponents();
        this.setLocationRelativeTo(component);
        InvoiceMainPanel.setVisible(true);
        CustomerMainPanel.setVisible(false);
        ProductMainPanel.setVisible(false);

        AddressTA.setFont(new Font("Segou UI", Font.PLAIN, 11));

        // Uneditable
        InvoiceTable.setFocusable(false);
        InvoiceTable.setRowSelectionAllowed(true);
        InvoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        InvoiceItemTable.setFocusable(false);
        InvoiceItemTable.setRowSelectionAllowed(true);
        InvoiceItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        CustomerTable.setFocusable(false);
        CustomerTable.setRowSelectionAllowed(true);
        CustomerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ProductTable.setFocusable(false);
        ProductTable.setRowSelectionAllowed(true);
        ProductTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //

        hideEditButtons();
        initaliseInvoices();
        initaliseCustomers();
        initaliseProducts();

        // Invoice select
        InvoiceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                selectedInvoiceRow = InvoiceTable.getSelectedRow();
                if (selectedInvoiceRow >= 0) {
                    selectedInvoice = Integer.parseInt(InvoiceTable.getValueAt(selectedInvoiceRow, 1).toString());
                    invoiceNo.setText("Invoice No. " + selectedInvoice);
                    editingInvoice = false;
                    setInvoiceEditing();
                    fillInvoiceInformation();
                }
            }
        });

        // Customer select
        CustomerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                selectedCustomerRow = CustomerTable.getSelectedRow();
                if (selectedCustomerRow >= 0) {
                    selectedCustomer = Integer.parseInt(CustomerTable.getValueAt(selectedCustomerRow, 0).toString());
                    customerNo.setText("Customer No. " + selectedCustomer);
                    fillCustomerInformation();
                }
            }
        });
    }

    /**
     * Gets customers and adds them to combo box
     *
     * @param component
     */
    private void addCustomerJComboBox(JComboBox component) {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            component.removeAllItems();
            // Get customers
            List<List<Object>> customerlist = handler.get(" customerId, firstName, lastName, address, email, phoneNumber FROM Application.Customer INNER JOIN Application.Account ON Application.Customer.accountId = Application.Account.accountId", 1000);
            for (List<Object> customer : customerlist) {
                // Add to combo box
                component.addItem(new Item((int) customer.get(0), (String) customer.get(1) + " " + (String) customer.get(2)));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve customers - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds products which the company owns to combo box
     *
     * @param component
     */
    private void addProductJComboBox(JComboBox component) {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] args = {Company.getCompanyId()};
            // Get products
            List<List<Object>> productlist = handler.get(" productId, name, cost, quantity FROM Application.Product WHERE companyId=?", args, 1000);
            component.removeAllItems();
            for (List<Object> product : productlist) {
                int quantity = (int) product.get(3);
                // Add to combo box
                component.addItem(new Item(new Product((int) product.get(0), quantity), (String) product.get(1) + ": $" + Integer.toString((int) product.get(2)) + "| Stock: " + Integer.toString(quantity)));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve customers - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all invoice information in view / amend invoice panel
     */
    private void clearInvoiceInformation() {
        AddressTA.setText("");
        PhoneNumber.setText("");
        InvoiceDate.setDate(null);
        EmailAddress.setText("");

        DefaultTableModel model = (DefaultTableModel) InvoiceItemTable.getModel();
        model.setRowCount(0);

        CustomerInvoiceBox.removeAllItems();
    }

    /**
     * Fills invoice information into view / amend invoice panel
     */
    private void fillInvoiceInformation() {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] args = {selectedInvoice};
            List<List<Object>> invoiceList = handler.get(" Application.Invoice.customerId, date, address, phoneNumber, email FROM Application.Invoice INNER JOIN Application.Customer ON Application.Customer.customerId = Application.Invoice.customerId INNER JOIN Application.Account ON Application.Account.accountId = Application.Customer.accountId WHERE invoiceId = ?", args, 1);
            if (invoiceList.size() == 1) {
                List<Object> invoiceInformation = invoiceList.get(0);
                // Now set invoice information
                int customerId = (int) invoiceInformation.get(0);
                LocalDateTime date = (LocalDateTime) invoiceInformation.get(1);
                String address = (String) invoiceInformation.get(2);
                String phoneNumber = (String) invoiceInformation.get(3);
                String emailAddress = (String) invoiceInformation.get(4);

                AddressTA.setText(address);
                PhoneNumber.setText(phoneNumber);
                InvoiceDate.setDate(convertToDateViaSqlTimestamp(date));
                EmailAddress.setText(emailAddress);

                getInvoiceItems();

                addCustomerJComboBox(CustomerInvoiceBox);

                // Loop and find customer to auto select
                for (int i = 0; i < CustomerInvoiceBox.getItemCount(); i++) {
                    Item customer = CustomerInvoiceBox.getItemAt(i);
                    if ((int) customer.getValue() == customerId) {
                        CustomerInvoiceBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invoice was not found.",
                        "Missing Invoice", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve invoice information - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills customer information into view customer panel
     */
    private void fillCustomerInformation() {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] args = {selectedCustomer};
            List<List<Object>> customerList = handler.get(" firstName, lastName, address, email, phoneNumber FROM Application.Customer INNER JOIN Application.Account ON Application.Customer.accountId = Application.Account.accountId WHERE customerId=?", args, 1);
            if (customerList.size() == 1) {
                List<Object> customerInformation = customerList.get(0);
                // Now set invoice information
                String firstName = (String) customerInformation.get(0);
                String lastName = (String) customerInformation.get(1);
                String address = (String) customerInformation.get(2);
                String email = (String) customerInformation.get(3);
                String phoneNumber = (String) customerInformation.get(4);

                CustomerFirstName.setText(firstName);
                CustomerLastName.setText(lastName);
                CustomerAddress.setText(address);
                CustomerEmailAddress.setText(email);
                CustomerPhoneNumber.setText(phoneNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Customer was not found.",
                        "Missing Customer", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve customer information - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hides all edit buttons
     */
    private void hideEditButtons() {
        AddInvoiceItemButton.setVisible(false);
        RemoveInvoiceItemButton.setVisible(false);
        ConfirmInvoiceButton.setVisible(false);
    }

    /**
     * Returns java.util.Date from java.sql.LocalDateTime
     * @param dateToConvert
     * @return java.util.Date
     */
    private Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    /**
     * Fills out the main invoice table with information
     */
    private void initaliseInvoices() {
        // Get model with the column names
        DefaultTableModel model = (DefaultTableModel) InvoiceTable.getModel();
        model.setRowCount(0);

        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] args = {Company.getCompanyId()};
            List<List<Object>> invoicelist = handler.get(" date,invoiceId,(SELECT SUM(i.quantity * e.cost) FROM Application.Invoice AS o INNER JOIN Application.InvoiceItem AS i ON o.invoiceId = i.invoiceId INNER JOIN Application.Product AS e ON i.productId = e.productId WHERE o.invoiceId = Application.Invoice.invoiceId GROUP BY o.invoiceId) FROM Application.Invoice WHERE Application.Invoice.companyId=?", args, 1000);
            for (List<Object> invoice : invoicelist) {
                java.util.Date newDate = convertToDateViaSqlTimestamp((LocalDateTime) invoice.get(0));
                int invoiceId = (int) invoice.get(1);
                Number invoiceTotal = (Number) invoice.get(2);
                if (invoiceTotal == null) {
                    invoiceTotal = 0;
                }
                model.addRow(addInvoice(invoiceId, newDate, invoiceTotal));
            }
            InvoiceTable.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve invoices - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills main customer table with information
     */
    private void initaliseCustomers() {
        // Get model with the column names
        DefaultTableModel model = (DefaultTableModel) CustomerTable.getModel();
        model.setRowCount(0);

        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            List<List<Object>> customerlist = handler.get(" customerId, firstName, lastName, email FROM Application.Customer INNER JOIN Application.Account ON Application.Account.accountId = Application.Customer.accountId", 1000);
            for (List<Object> customer : customerlist) {
                int customerId = (int) customer.get(0);
                String customerFirstName = (String) customer.get(1);
                String customerLastName = (String) customer.get(2);
                String customerEmail = (String) customer.get(3);

                model.addRow(addCustomer(customerId, customerFirstName + " " + customerLastName, customerEmail));
            }
            CustomerTable.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve customers - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills main product table with information
     */
    private void initaliseProducts() {
        // Get model with the column names
        DefaultTableModel model = (DefaultTableModel) ProductTable.getModel();
        model.setRowCount(0);

        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] args = {Company.getCompanyId()};
            List<List<Object>> productlist = handler.get(" productId, name, quantity, cost FROM Application.Product WHERE companyId=?", args, 1000);
            for (List<Object> product : productlist) {
                int productId = (int) product.get(0);
                String name = (String) product.get(1);
                int quantity = (int) product.get(2);
                int cost = (int) product.get(3);

                model.addRow(addProduct(productId, name, quantity, cost));
            }
            ProductTable.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve products - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills invoice item box with information
     */
    private void getInvoiceItems() {
        // Get model with the column names
        DefaultTableModel model = (DefaultTableModel) InvoiceItemTable.getModel();
        model.setRowCount(0);

        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) { // ID", "Title", "Qty", "Unit Price", "Total cost"
            Object[] args = {selectedInvoice};
            List<List<Object>> invoiceitemList = handler.get(" Application.InvoiceItem.productId , name, ANY_VALUE(Application.InvoiceItem.quantity) as quantity, cost, SUM(Application.InvoiceItem.quantity * cost) FROM Application.InvoiceItem INNER JOIN Application.Product ON Application.InvoiceItem.productId = Application.Product.productId WHERE Application.InvoiceItem.invoiceId=? GROUP BY Application.InvoiceItem.productId", args, 1000);
            for (List<Object> invoice : invoiceitemList) {
                int productId = (int) invoice.get(0);
                String invoiceItemName = (String) invoice.get(1);
                int quantity = ((Long) invoice.get(2)).intValue();
                int cost = (int) invoice.get(3);
                int totalCost = ((BigDecimal) invoice.get(4)).intValue();
                model.addRow(addInvoiceItem(productId, invoiceItemName, quantity, cost, totalCost));
            }
            InvoiceItemTable.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve invoice items - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[] addInvoice(int invoiceId, Date invoiceDate, Number invoiceTotal) {
        List<String> list = new ArrayList<>();
        list.add(Integer.toString(invoiceDate.getDay()) + "/" + Integer.toString(invoiceDate.getMonth()) + "/" + Integer.toString(invoiceDate.getYear() + 1900));
        list.add(Integer.toString(invoiceId));
        list.add(Integer.toString(invoiceTotal.intValue()));

        return list.toArray();
    }

    /**
     * Used to add invoice item to invoice item table
     * @param productId
     * @param invoiceItemName
     * @param quantity
     * @param cost
     * @param totalCost
     * @return 
     */
    private Object[] addInvoiceItem(int productId, String invoiceItemName, int quantity, int cost, int totalCost) {
        List<String> list = new ArrayList<>();
        list.add(Integer.toString(productId));
        list.add(invoiceItemName);
        list.add(Integer.toString(quantity));
        list.add(Integer.toString(cost));
        list.add(Integer.toString(totalCost));

        // Convert and use as array
        return list.toArray();
    }

    /**
     * Used to add customer item to customer table
     * @param customerId
     * @param customerName
     * @param customerEmail
     * @return Array for customer table
     */
    private Object[] addCustomer(int customerId, String customerName, String customerEmail) {
        List<String> list = new ArrayList<>();

        list.add(Integer.toString(customerId));
        list.add(customerName);
        list.add(customerEmail);

        return list.toArray();
    }

    /**
     * Used to add product to product table
     * @param productId
     * @param name
     * @param quantity
     * @param cost
     * @return Array for product table
     */
    private Object[] addProduct(int productId, String name, int quantity, int cost) {
        List<String> list = new ArrayList<>();

        list.add(Integer.toString(productId));
        list.add(name);
        list.add(Integer.toString(quantity));
        list.add(Integer.toString(cost));

        return list.toArray();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AddInvoiceForm = new javax.swing.JDialog();
        jLabel30 = new javax.swing.JLabel();
        CustomerBox = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        InvoiceAddressForm = new javax.swing.JTextArea();
        jLabel32 = new javax.swing.JLabel();
        InvoiceEmailForm = new javax.swing.JTextField();
        InvoicePhoneNumberForm = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        InvoiceOkButton = new javax.swing.JButton();
        InvoiceCancelButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        InvoiceDateForm = new com.toedter.calendar.JDateChooser();
        AddInvoiceItemForm = new javax.swing.JDialog();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        QuantityInvoiceItemForm = new javax.swing.JSpinner();
        AddInvoiceItemFormButton = new javax.swing.JButton();
        CancelInvoiceItemFormButton = new javax.swing.JButton();
        ProductInvoiceItemCombo = new javax.swing.JComboBox<>();
        AddProductForm = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        QuantityProductForm = new javax.swing.JSpinner();
        AddProductFormButton = new javax.swing.JButton();
        CancelProductFormButton = new javax.swing.JButton();
        NameProductForm = new javax.swing.JTextField();
        CostProductForm = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        TopBar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        LogOutButton = new javax.swing.JButton();
        CustomerButton = new javax.swing.JButton();
        InvoiceButton = new javax.swing.JButton();
        ProductButton = new javax.swing.JButton();
        BottomPanel = new javax.swing.JPanel();
        CustomerMainPanel = new javax.swing.JSplitPane();
        ListCustomerPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        CustomerTable = new javax.swing.JTable();
        CustomerPanel = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        CustomerFirstName = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        CustomerAddress = new javax.swing.JTextArea();
        jLabel25 = new javax.swing.JLabel();
        CustomerEmailAddress = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        CustomerPhoneNumber = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        CustomerLastName = new javax.swing.JTextField();
        customerNo = new javax.swing.JLabel();
        InvoiceMainPanel = new javax.swing.JSplitPane();
        ListInvoicePanel = new javax.swing.JPanel();
        CreateInvoiceButton = new javax.swing.JButton();
        DeleteInvoiceButton = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        InvoiceTable = new javax.swing.JTable();
        InvoicePanel = new javax.swing.JPanel();
        invoiceNo = new javax.swing.JLabel();
        CustomerInvoiceBox = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        InvoiceDate = new com.toedter.calendar.JDateChooser();
        AmendInvoiceButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        PhoneNumber = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        EmailAddress = new javax.swing.JTextField();
        RemoveInvoiceItemButton = new javax.swing.JButton();
        AddInvoiceItemButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        AddressTA = new javax.swing.JTextArea();
        ConfirmInvoiceButton = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        InvoiceItemTable = new javax.swing.JTable();
        ProductMainPanel = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        ProductTable = new javax.swing.JTable();
        CreateProductButton = new javax.swing.JButton();
        DeleteProductButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        AddInvoiceForm.setTitle("Add Invoice");
        AddInvoiceForm.setAlwaysOnTop(true);
        AddInvoiceForm.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        AddInvoiceForm.setModal(true);
        AddInvoiceForm.setSize(new java.awt.Dimension(460, 260));

        jLabel30.setText("Customer:");

        CustomerBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomerBoxActionPerformed(evt);
            }
        });

        jLabel31.setText("Address: ");

        InvoiceAddressForm.setColumns(20);
        InvoiceAddressForm.setRows(5);
        InvoiceAddressForm.setEnabled(false);
        jScrollPane8.setViewportView(InvoiceAddressForm);

        jLabel32.setText("Email:");

        InvoiceEmailForm.setEnabled(false);

        InvoicePhoneNumberForm.setEnabled(false);

        jLabel33.setText("Phone Number: ");

        InvoiceOkButton.setText("Ok");
        InvoiceOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InvoiceOkButtonActionPerformed(evt);
            }
        });

        InvoiceCancelButton.setText("Cancel");
        InvoiceCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InvoiceCancelButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Date:");

        javax.swing.GroupLayout AddInvoiceFormLayout = new javax.swing.GroupLayout(AddInvoiceForm.getContentPane());
        AddInvoiceForm.getContentPane().setLayout(AddInvoiceFormLayout);
        AddInvoiceFormLayout.setHorizontalGroup(
            AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddInvoiceFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddInvoiceFormLayout.createSequentialGroup()
                        .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(AddInvoiceFormLayout.createSequentialGroup()
                                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel31)
                                    .addComponent(jLabel32))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane8)
                                    .addComponent(InvoiceEmailForm)))
                            .addGroup(AddInvoiceFormLayout.createSequentialGroup()
                                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(AddInvoiceFormLayout.createSequentialGroup()
                                        .addComponent(jLabel30)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(CustomerBox, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(AddInvoiceFormLayout.createSequentialGroup()
                                        .addComponent(jLabel33)
                                        .addGap(18, 18, 18)
                                        .addComponent(InvoicePhoneNumberForm, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(InvoiceDateForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(12, 12, 12))
                    .addGroup(AddInvoiceFormLayout.createSequentialGroup()
                        .addComponent(InvoiceOkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(InvoiceCancelButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        AddInvoiceFormLayout.setVerticalGroup(
            AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddInvoiceFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CustomerBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InvoiceEmailForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel33)
                        .addComponent(InvoicePhoneNumberForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(InvoiceDateForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddInvoiceFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InvoiceCancelButton)
                    .addComponent(InvoiceOkButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        AddInvoiceItemForm.setTitle("Add Item");
        AddInvoiceItemForm.setAlwaysOnTop(true);
        AddInvoiceItemForm.setModal(true);
        AddInvoiceItemForm.setSize(new java.awt.Dimension(245, 142));

        jLabel3.setText("Product:");

        jLabel4.setText("Quantity:");

        QuantityInvoiceItemForm.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        AddInvoiceItemFormButton.setText("Add");
        AddInvoiceItemFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddInvoiceItemFormButtonActionPerformed(evt);
            }
        });

        CancelInvoiceItemFormButton.setText("Cancel");
        CancelInvoiceItemFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelInvoiceItemFormButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AddInvoiceItemFormLayout = new javax.swing.GroupLayout(AddInvoiceItemForm.getContentPane());
        AddInvoiceItemForm.getContentPane().setLayout(AddInvoiceItemFormLayout);
        AddInvoiceItemFormLayout.setHorizontalGroup(
            AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddInvoiceItemFormLayout.createSequentialGroup()
                .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, AddInvoiceItemFormLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, AddInvoiceItemFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(AddInvoiceItemFormButton)))
                .addGap(18, 18, 18)
                .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(CancelInvoiceItemFormButton)
                    .addComponent(ProductInvoiceItemCombo, 0, 133, Short.MAX_VALUE)
                    .addComponent(QuantityInvoiceItemForm))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        AddInvoiceItemFormLayout.setVerticalGroup(
            AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddInvoiceItemFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ProductInvoiceItemCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(QuantityInvoiceItemForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddInvoiceItemFormButton)
                    .addComponent(CancelInvoiceItemFormButton))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        AddProductForm.setTitle("Add Item");
        AddProductForm.setAlwaysOnTop(true);
        AddProductForm.setModal(true);
        AddProductForm.setResizable(false);
        AddProductForm.setSize(new java.awt.Dimension(265, 160));

        jLabel7.setText("Name:");

        jLabel8.setText("Quantity:");

        QuantityProductForm.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        AddProductFormButton.setText("Add");
        AddProductFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddProductFormButtonActionPerformed(evt);
            }
        });

        CancelProductFormButton.setText("Cancel");
        CancelProductFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelProductFormButtonActionPerformed(evt);
            }
        });

        CostProductForm.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jLabel9.setText("Cost");

        javax.swing.GroupLayout AddProductFormLayout = new javax.swing.GroupLayout(AddProductForm.getContentPane());
        AddProductForm.getContentPane().setLayout(AddProductFormLayout);
        AddProductFormLayout.setHorizontalGroup(
            AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddProductFormLayout.createSequentialGroup()
                .addGroup(AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddProductFormLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(41, 41, 41)
                        .addGroup(AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(NameProductForm)
                            .addGroup(AddProductFormLayout.createSequentialGroup()
                                .addGap(0, 20, Short.MAX_VALUE)
                                .addComponent(QuantityProductForm, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddProductFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(AddProductFormButton)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(CancelProductFormButton))
                    .addGroup(AddProductFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(CostProductForm, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        AddProductFormLayout.setVerticalGroup(
            AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddProductFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(NameProductForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(QuantityProductForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CostProductForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddProductFormButton)
                    .addComponent(CancelProductFormButton))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Company Menu");

        TopBar.setBackground(new java.awt.Color(0, 153, 255));

        jLabel1.setFont(new java.awt.Font("Gadugi", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Testing Ltd");

        LogOutButton.setText("Log out");
        LogOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogOutButtonActionPerformed(evt);
            }
        });

        CustomerButton.setBackground(new java.awt.Color(76, 181, 251));
        CustomerButton.setFont(new java.awt.Font("Gadugi", 1, 14)); // NOI18N
        CustomerButton.setForeground(new java.awt.Color(255, 255, 255));
        CustomerButton.setText("Customer");
        CustomerButton.setBorder(null);
        CustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomerButtonActionPerformed(evt);
            }
        });

        InvoiceButton.setBackground(new java.awt.Color(76, 181, 251));
        InvoiceButton.setFont(new java.awt.Font("Gadugi", 1, 14)); // NOI18N
        InvoiceButton.setForeground(new java.awt.Color(255, 255, 255));
        InvoiceButton.setText("Invoice");
        InvoiceButton.setBorder(null);
        InvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InvoiceButtonActionPerformed(evt);
            }
        });

        ProductButton.setBackground(new java.awt.Color(76, 181, 251));
        ProductButton.setFont(new java.awt.Font("Gadugi", 1, 14)); // NOI18N
        ProductButton.setForeground(new java.awt.Color(255, 255, 255));
        ProductButton.setText("Product");
        ProductButton.setBorder(null);
        ProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProductButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TopBarLayout = new javax.swing.GroupLayout(TopBar);
        TopBar.setLayout(TopBarLayout);
        TopBarLayout.setHorizontalGroup(
            TopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(46, 46, 46)
                .addComponent(InvoiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(CustomerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ProductButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LogOutButton)
                .addContainerGap())
        );
        TopBarLayout.setVerticalGroup(
            TopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LogOutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(InvoiceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(CustomerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(ProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        BottomPanel.setLayout(new java.awt.CardLayout());

        CustomerTable.setModel(new UnEditableDefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Customer No.", "Name", "Email"
            }
        ));
        CustomerTable.setToolTipText("");
        CustomerTable.setShowHorizontalLines(true);
        CustomerTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(CustomerTable);

        javax.swing.GroupLayout ListCustomerPanelLayout = new javax.swing.GroupLayout(ListCustomerPanel);
        ListCustomerPanel.setLayout(ListCustomerPanelLayout);
        ListCustomerPanelLayout.setHorizontalGroup(
            ListCustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListCustomerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
        );
        ListCustomerPanelLayout.setVerticalGroup(
            ListCustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ListCustomerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                .addContainerGap())
        );

        CustomerMainPanel.setLeftComponent(ListCustomerPanel);

        CustomerPanel.setBackground(new java.awt.Color(204, 204, 204));

        jLabel23.setText("First Name:");

        CustomerFirstName.setEnabled(false);

        jLabel24.setText("Address: ");

        CustomerAddress.setColumns(20);
        CustomerAddress.setRows(5);
        CustomerAddress.setEnabled(false);
        jScrollPane7.setViewportView(CustomerAddress);

        jLabel25.setText("Email");

        CustomerEmailAddress.setEnabled(false);

        jLabel26.setText("Phone Number: ");

        CustomerPhoneNumber.setEnabled(false);

        jLabel27.setText("Last Name:");

        CustomerLastName.setEnabled(false);

        customerNo.setText("Customer No.");

        javax.swing.GroupLayout CustomerPanelLayout = new javax.swing.GroupLayout(CustomerPanel);
        CustomerPanel.setLayout(CustomerPanelLayout);
        CustomerPanelLayout.setHorizontalGroup(
            CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customerNo)
                    .addGroup(CustomerPanelLayout.createSequentialGroup()
                        .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel27)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CustomerLastName)
                            .addComponent(CustomerFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(CustomerPanelLayout.createSequentialGroup()
                        .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane7)
                            .addComponent(CustomerEmailAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(CustomerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(12, 12, 12)
                        .addComponent(CustomerPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(225, Short.MAX_VALUE))
        );
        CustomerPanelLayout.setVerticalGroup(
            CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(customerNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(CustomerFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(CustomerLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CustomerEmailAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CustomerPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addContainerGap(149, Short.MAX_VALUE))
        );

        CustomerMainPanel.setRightComponent(CustomerPanel);

        BottomPanel.add(CustomerMainPanel, "card2");

        CreateInvoiceButton.setText("Create new Invoice");
        CreateInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateInvoiceButtonActionPerformed(evt);
            }
        });

        DeleteInvoiceButton.setText("Delete Selected Invoice");
        DeleteInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteInvoiceButtonActionPerformed(evt);
            }
        });

        InvoiceTable.setModel(new UnEditableDefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Date", "Invoice No.", "Total"
            }
        ));
        InvoiceTable.setToolTipText("");
        InvoiceTable.setShowHorizontalLines(true);
        InvoiceTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(InvoiceTable);

        javax.swing.GroupLayout ListInvoicePanelLayout = new javax.swing.GroupLayout(ListInvoicePanel);
        ListInvoicePanel.setLayout(ListInvoicePanelLayout);
        ListInvoicePanelLayout.setHorizontalGroup(
            ListInvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListInvoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ListInvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ListInvoicePanelLayout.createSequentialGroup()
                        .addComponent(CreateInvoiceButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DeleteInvoiceButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        ListInvoicePanelLayout.setVerticalGroup(
            ListInvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListInvoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ListInvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DeleteInvoiceButton)
                    .addComponent(CreateInvoiceButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        InvoiceMainPanel.setLeftComponent(ListInvoicePanel);

        InvoicePanel.setBackground(new java.awt.Color(204, 204, 204));

        invoiceNo.setText("Invoice No. -");

        CustomerInvoiceBox.setEnabled(false);

        jLabel12.setText("Date:");

        InvoiceDate.setEnabled(false);

        AmendInvoiceButton.setText("Amend");
        AmendInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AmendInvoiceButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("Address: ");

        jLabel18.setText("Phone Number: ");

        PhoneNumber.setEnabled(false);

        jLabel19.setText("Email: ");

        EmailAddress.setEnabled(false);

        RemoveInvoiceItemButton.setText("Delete Selected");
        RemoveInvoiceItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveInvoiceItemButtonActionPerformed(evt);
            }
        });

        AddInvoiceItemButton.setText("Add New");
        AddInvoiceItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddInvoiceItemButtonActionPerformed(evt);
            }
        });

        AddressTA.setColumns(5);
        AddressTA.setRows(5);
        AddressTA.setEnabled(false);
        jScrollPane3.setViewportView(AddressTA);

        ConfirmInvoiceButton.setText("Ok");
        ConfirmInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfirmInvoiceButtonActionPerformed(evt);
            }
        });

        InvoiceItemTable.setModel(new UnEditableDefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product No.", "Title", "Qty", "Unit Price", "Total Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        InvoiceItemTable.setToolTipText("");
        InvoiceItemTable.setShowHorizontalLines(true);
        InvoiceItemTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(InvoiceItemTable);

        javax.swing.GroupLayout InvoicePanelLayout = new javax.swing.GroupLayout(InvoicePanel);
        InvoicePanel.setLayout(InvoicePanelLayout);
        InvoicePanelLayout.setHorizontalGroup(
            InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InvoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(InvoicePanelLayout.createSequentialGroup()
                        .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addComponent(invoiceNo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CustomerInvoiceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(InvoicePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(InvoiceDate, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, InvoicePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(PhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(43, 43, 43)
                                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(AmendInvoiceButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ConfirmInvoiceButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(EmailAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addComponent(AddInvoiceItemButton)
                                .addGap(109, 109, 109)
                                .addComponent(RemoveInvoiceItemButton)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        InvoicePanelLayout.setVerticalGroup(
            InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InvoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(InvoicePanelLayout.createSequentialGroup()
                        .addComponent(AmendInvoiceButton)
                        .addGap(2, 2, 2)
                        .addComponent(ConfirmInvoiceButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(InvoicePanelLayout.createSequentialGroup()
                        .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(PhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CustomerInvoiceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(invoiceNo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(InvoiceDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19)
                                    .addComponent(EmailAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(AddInvoiceItemButton)
                                    .addComponent(RemoveInvoiceItemButton)))
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        InvoiceMainPanel.setRightComponent(InvoicePanel);

        BottomPanel.add(InvoiceMainPanel, "card4");

        ProductTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product #", "Title", "Quantity", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ProductTable.setColumnSelectionAllowed(true);
        ProductTable.setShowHorizontalLines(true);
        ProductTable.getTableHeader().setReorderingAllowed(false);
        ProductTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                ProductTablePropertyChange(evt);
            }
        });
        jScrollPane9.setViewportView(ProductTable);
        ProductTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        CreateProductButton.setText("Create new Product");
        CreateProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateProductButtonActionPerformed(evt);
            }
        });

        DeleteProductButton.setText("Delete Selected Product");
        DeleteProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteProductButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Click and edit the products below");

        javax.swing.GroupLayout ProductMainPanelLayout = new javax.swing.GroupLayout(ProductMainPanel);
        ProductMainPanel.setLayout(ProductMainPanelLayout);
        ProductMainPanelLayout.setHorizontalGroup(
            ProductMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ProductMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ProductMainPanelLayout.createSequentialGroup()
                        .addComponent(CreateProductButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteProductButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
                .addContainerGap())
        );
        ProductMainPanelLayout.setVerticalGroup(
            ProductMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ProductMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DeleteProductButton)
                    .addComponent(CreateProductButton)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        BottomPanel.add(ProductMainPanel, "card5");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TopBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(BottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 792, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(TopBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LogOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogOutButtonActionPerformed
        int result = JOptionPane.showConfirmDialog(this, // 0 = yes, 1 = no
                "Are you sure you want to sign out?", "Sign out Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            Account account = Account.getInstance();
            account.signout();
            Company.logout();
            dispose();
            new LoginAccountForm(this).setVisible(true);
        }
    }//GEN-LAST:event_LogOutButtonActionPerformed

    private void DeleteInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteInvoiceButtonActionPerformed
        if (selectedInvoiceRow == null) {
            JOptionPane.showMessageDialog(this, "An invoice must be selected!",
                    "Invalid Invoice Selection", JOptionPane.ERROR_MESSAGE);
        } else {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this invoice?", "Delete Invoice Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                // Delete
                Packet deletePacket = Invoice.deleteInvoice(selectedInvoice);
                switch (deletePacket.getResult()) {
                    case SUCCESS:
                        JOptionPane.showMessageDialog(this, "Invoice Id:" + selectedInvoice + " was successfully deleted.");
                        DefaultTableModel model = (DefaultTableModel) InvoiceTable.getModel();
                        model.removeRow(selectedInvoiceRow);
                        InvoiceTable.setModel(model);
                        selectedInvoiceRow = null;
                        clearInvoiceInformation();
                        break;
                    case CONNECTION_ERROR:
                        JOptionPane.showMessageDialog(this, "Unable to delete invoice - You must be connected to the Database!",
                                "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                        break;
                    case ACCESS_DENIED:
                        JOptionPane.showMessageDialog(this, "Unable to delete invoice - You aren't suppose to access this!",
                                "Access Denied", JOptionPane.ERROR_MESSAGE);
                        break;
                    case DATABASE_ERROR:
                        JOptionPane.showMessageDialog(this, "Unable to delete invoice - Query incorrect!",
                                "Query incorrect", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        }
    }//GEN-LAST:event_DeleteInvoiceButtonActionPerformed

    /**
     * Set the invoice state editing
     */
    void setInvoiceEditing() {
        CustomerInvoiceBox.setEnabled(editingInvoice);
        InvoiceDate.setEnabled(editingInvoice);
        AddInvoiceItemButton.setVisible(editingInvoice);
        RemoveInvoiceItemButton.setVisible(editingInvoice);
        ConfirmInvoiceButton.setVisible(editingInvoice);
        if (editingInvoice) {
            AmendInvoiceButton.setText("View");
        } else {
            AmendInvoiceButton.setText("Amend");
        }
    }

    private void AmendInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AmendInvoiceButtonActionPerformed
        if (selectedInvoiceRow == null) {
            JOptionPane.showMessageDialog(this, "An invoice must be selected!",
                    "Invalid Invoice Selection", JOptionPane.ERROR_MESSAGE);
        } else {
            editingInvoice = !editingInvoice;
            setInvoiceEditing();
        }
    }//GEN-LAST:event_AmendInvoiceButtonActionPerformed

    private void AddInvoiceItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddInvoiceItemButtonActionPerformed
        addProductJComboBox(ProductInvoiceItemCombo);
        AddInvoiceItemForm.setLocationRelativeTo(this);
        AddInvoiceItemForm.setVisible(true);
    }//GEN-LAST:event_AddInvoiceItemButtonActionPerformed

    private void ConfirmInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmInvoiceButtonActionPerformed
        int result = JOptionPane.showConfirmDialog(this, // 0 = yes, 1 = no
                "Are you sure you want to confirm these details?", "Edit Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == 0) { // Do nothing to boxes
            Date invoiceDate = InvoiceDate.getDate();
            if (invoiceDate == null) {
                JOptionPane.showMessageDialog(this, "Unable to update invoive - Date is null!",
                        "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Item item = (Item) CustomerInvoiceBox.getSelectedItem();
            int customerId = (int) item.getValue();
            // Set the information of invoice
            Packet setInformationPacket = Invoice.setInformation(customerId, selectedInvoice, invoiceDate);
            switch (setInformationPacket.getResult()) {
                case SUCCESS:
                    JOptionPane.showMessageDialog(this, "Invoice Id: " + selectedInvoice + "  was successfully updated.");
                    break;
                case CONNECTION_ERROR:
                    JOptionPane.showMessageDialog(this, "Unable to update invoive - You must be connected to the Database!",
                            "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "An Error Occurred!",
                            "Error Occurred", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } else {
            fillInvoiceInformation();
        }
        editingInvoice = false;
        setInvoiceEditing();
    }//GEN-LAST:event_ConfirmInvoiceButtonActionPerformed

    private void InvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InvoiceButtonActionPerformed
        InvoiceMainPanel.setVisible(true);
        CustomerMainPanel.setVisible(false);
        ProductMainPanel.setVisible(false);
    }//GEN-LAST:event_InvoiceButtonActionPerformed

    private void CustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomerButtonActionPerformed
        InvoiceMainPanel.setVisible(false);
        CustomerMainPanel.setVisible(true);
        ProductMainPanel.setVisible(false);
        editingInvoice = false;
        setInvoiceEditing();
    }//GEN-LAST:event_CustomerButtonActionPerformed

    private void CreateInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateInvoiceButtonActionPerformed
        addCustomerJComboBox(CustomerBox);
        AddInvoiceForm.setLocationRelativeTo(this);
        AddInvoiceForm.setVisible(true);
    }//GEN-LAST:event_CreateInvoiceButtonActionPerformed

    private void InvoiceOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InvoiceOkButtonActionPerformed
        Item item = (Item) CustomerBox.getSelectedItem();
        if (item == null) { // Validation
            JOptionPane.showMessageDialog(AddInvoiceForm, "Please input an select a customer inside of the box!",
                    "Empty Customer Field", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int customerId = (int) item.getValue();

        Date invoiceDate = InvoiceDateForm.getDate();
        if (invoiceDate == null) { // Validation
            JOptionPane.showMessageDialog(AddInvoiceForm, "Please input a date into the DOB field!",
                    "Empty DOB Field", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Packet invoiceInsertPacket = Invoice.insertInvoice(Company.getCompanyId(), customerId, invoiceDate);
        switch (invoiceInsertPacket.getResult()) {
            case SUCCESS:
                JOptionPane.showMessageDialog(AddInvoiceForm, "Invoice was successfully created.");
                initaliseInvoices();
                break;
            case DATABASE_ERROR:
                break;
            case CONNECTION_ERROR:
                JOptionPane.showMessageDialog(this, "Unable to create invoice - You must be connected to the Database!",
                        "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                break;
            default:
                break;
        }
        AddInvoiceForm.dispose();
        InvoiceAddressForm.setText("");
        InvoicePhoneNumberForm.setText("");
        InvoiceEmailForm.setText("");
        InvoiceDateForm.setDate(null);
    }//GEN-LAST:event_InvoiceOkButtonActionPerformed

    private void InvoiceCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InvoiceCancelButtonActionPerformed
        AddInvoiceForm.dispose();
    }//GEN-LAST:event_InvoiceCancelButtonActionPerformed

    private void AddInvoiceItemFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddInvoiceItemFormButtonActionPerformed
        Item item = (Item) ProductInvoiceItemCombo.getSelectedItem();
        Packet addInvoiceItemPacket = Company.addInvoiceItem((int) QuantityInvoiceItemForm.getValue(), (Product) item.getValue(), selectedInvoice);
        switch (addInvoiceItemPacket.getResult()) {
            case SUCCESS:
                JOptionPane.showMessageDialog(AddInvoiceForm, "Item has been added to invoice.");
                getInvoiceItems();
                initaliseInvoices();
                break;
            case DATABASE_ERROR:
                JOptionPane.showMessageDialog(AddInvoiceItemForm, "An error occurred!",
                        "An error occurred", JOptionPane.ERROR_MESSAGE);
                break;
            case CONNECTION_ERROR:
                JOptionPane.showMessageDialog(AddInvoiceItemForm, "Unable to add item to invoice - You must be connected to the Database!",
                        "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                break;
            case BAD_REQUEST:
                JOptionPane.showMessageDialog(AddInvoiceItemForm, "Unable to add item to invoice - Quantity in stock is lower than requested!",
                        "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                break;
            default:
                break;
        }
        AddInvoiceItemForm.dispose();
    }//GEN-LAST:event_AddInvoiceItemFormButtonActionPerformed

    private void CancelInvoiceItemFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelInvoiceItemFormButtonActionPerformed
        AddInvoiceItemForm.dispose();
    }//GEN-LAST:event_CancelInvoiceItemFormButtonActionPerformed

    private void ProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProductButtonActionPerformed
        InvoiceMainPanel.setVisible(false);
        CustomerMainPanel.setVisible(false);
        ProductMainPanel.setVisible(true);
    }//GEN-LAST:event_ProductButtonActionPerformed

    private void CreateProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateProductButtonActionPerformed
        AddProductForm.setLocationRelativeTo(this);
        AddProductForm.setVisible(true);
    }//GEN-LAST:event_CreateProductButtonActionPerformed

    private void DeleteProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteProductButtonActionPerformed
        int productRow = ProductTable.getSelectedRow();
        if (productRow < 0) {
            JOptionPane.showMessageDialog(this, "A product must be selected!",
                    "Invalid Product Selection", JOptionPane.ERROR_MESSAGE);
        } else {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this product?", "Delete Product Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                int productId = Integer.parseInt(ProductTable.getValueAt(productRow, 0).toString());
                Packet deletePacket = Product.deleteProduct(productId);
                switch (deletePacket.getResult()) {
                    case SUCCESS:
                        JOptionPane.showMessageDialog(this, "Product Id:" + productId + " was successfully deleted.");
                        initaliseProducts();
                        break;
                    case CONNECTION_ERROR:
                        JOptionPane.showMessageDialog(this, "Unable to delete product - You must be connected to the Database!",
                                "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                        break;
                    case ACCESS_DENIED:
                        JOptionPane.showMessageDialog(this, "Unable to delete product - You aren't suppose to access this!",
                                "Access Denied", JOptionPane.ERROR_MESSAGE);
                        break;
                    case DATABASE_ERROR:
                        JOptionPane.showMessageDialog(this, "Unable to delete product - Query incorrect!",
                                "Query incorrect", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        }
    }//GEN-LAST:event_DeleteProductButtonActionPerformed

    private void RemoveInvoiceItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveInvoiceItemButtonActionPerformed
        int row = InvoiceItemTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(AddInvoiceItemForm, "You must select an Item.",
                    "Invalid Item Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this item from the invoice?", "Delete Item Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            int column = 0;
            int productId = Integer.parseInt(InvoiceItemTable.getModel().getValueAt(row, column).toString());
            Packet deletePacket = Company.deleteInvoiceItem(productId, selectedInvoice);
            switch (deletePacket.getResult()) {
                case SUCCESS:
                    JOptionPane.showMessageDialog(AddInvoiceForm, "Successfully deleted the item from the invoice.");
                    getInvoiceItems();
                    initaliseInvoices();
                    break;
                case DATABASE_ERROR:
                    JOptionPane.showMessageDialog(AddInvoiceItemForm, "An error occurred!?",
                            "An error occurred", JOptionPane.ERROR_MESSAGE);
                    break;
                case CONNECTION_ERROR:
                    JOptionPane.showMessageDialog(AddInvoiceItemForm, "Unable to delete the item - You must be connected to the Database!",
                            "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                    break;
                case BAD_REQUEST:
                    JOptionPane.showMessageDialog(AddInvoiceItemForm, "Unable to delete the item - Quantity in stock is lower than requested!",
                            "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(AddInvoiceItemForm, "An error occurred!",
                            "An error occurred", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }//GEN-LAST:event_RemoveInvoiceItemButtonActionPerformed

    private void CustomerBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomerBoxActionPerformed
        DatabaseHandler handler = DatabaseHandler.getInstance();
        Item item = (Item) CustomerBox.getSelectedItem();
        if (item == null) {
            return;
        }
        if (handler.isConnected()) {
            int customerId = (int) item.getValue();

            Object[] args = {customerId};
            List<List<Object>> customerList = handler.get(" address, phoneNumber, email FROM Application.Customer INNER JOIN Application.Account ON Application.Customer.accountId = Application.Account.accountId WHERE customerId = ?", args, 1);
            if (customerList.size() == 1) {
                List<Object> customerInformation = customerList.get(0);
                // Now set customer information
                String address = (String) customerInformation.get(0);
                String phoneNumber = (String) customerInformation.get(1);
                String emailAddress = (String) customerInformation.get(2);

                InvoiceAddressForm.setText(address);
                InvoicePhoneNumberForm.setText(phoneNumber);
                InvoiceEmailForm.setText(emailAddress);
            }
        } else {
            JOptionPane.showMessageDialog(AddInvoiceItemForm, "Unable to retrieve customer information - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_CustomerBoxActionPerformed

    private void ProductTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_ProductTablePropertyChange
        int productRow = ProductTable.getSelectedRow();
        if (productRow >= 0) {
            System.out.println("lol");
            int productId = Integer.parseInt(ProductTable.getValueAt(productRow, 0).toString());
            String name = (String) ProductTable.getValueAt(productRow, 1);
            if (name.isBlank()) {
                return;
            }
            int cost = Integer.parseInt(ProductTable.getValueAt(productRow, 2).toString());
            int quantity = Integer.parseInt(ProductTable.getValueAt(productRow, 3).toString());
            // Update product information
            Packet updatePacket = Product.updateProduct(productId, name, cost, quantity);
            switch (updatePacket.getResult()) {
                case SUCCESS:
                    break;
                case CONNECTION_ERROR:
                    JOptionPane.showMessageDialog(AddInvoiceItemForm, "Unable to update product information - You must be connected to the Database!",
                            "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(AddInvoiceItemForm, "Unable to retrieve product information - Something went wrong!",
                            "Something went wrong", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }//GEN-LAST:event_ProductTablePropertyChange

    private void AddProductFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddProductFormButtonActionPerformed
        // Add product
        Packet addProductPacket = Product.insertProduct(NameProductForm.getText(), (int) QuantityProductForm.getValue(), (int) CostProductForm.getValue());
        switch (addProductPacket.getResult()) {
            case SUCCESS:
                JOptionPane.showMessageDialog(AddInvoiceForm, "Product has been added to database.");
                initaliseProducts();
                break;
            case DATABASE_ERROR:
                JOptionPane.showMessageDialog(AddInvoiceItemForm, "An error occurred!",
                        "An error occurred", JOptionPane.ERROR_MESSAGE);
                break;
            case CONNECTION_ERROR:
                JOptionPane.showMessageDialog(AddInvoiceItemForm, "Unable to add product to database - You must be connected to the Database!",
                        "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
                break;
            default:
                break;
        }
        AddProductForm.dispose();
    }//GEN-LAST:event_AddProductFormButtonActionPerformed

    private void CancelProductFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelProductFormButtonActionPerformed
        AddProductForm.dispose();
    }//GEN-LAST:event_CancelProductFormButtonActionPerformed

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private boolean editingInvoice = false;

    private int selectedCustomer = 0;
    private int selectedInvoice = 0;

    private Integer selectedCustomerRow = null;
    private Integer selectedInvoiceRow = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog AddInvoiceForm;
    private javax.swing.JButton AddInvoiceItemButton;
    private javax.swing.JDialog AddInvoiceItemForm;
    private javax.swing.JButton AddInvoiceItemFormButton;
    private javax.swing.JDialog AddProductForm;
    private javax.swing.JButton AddProductFormButton;
    private javax.swing.JTextArea AddressTA;
    private javax.swing.JButton AmendInvoiceButton;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton CancelInvoiceItemFormButton;
    private javax.swing.JButton CancelProductFormButton;
    private javax.swing.JButton ConfirmInvoiceButton;
    private javax.swing.JSpinner CostProductForm;
    private javax.swing.JButton CreateInvoiceButton;
    private javax.swing.JButton CreateProductButton;
    private javax.swing.JTextArea CustomerAddress;
    private javax.swing.JComboBox<Item> CustomerBox;
    private javax.swing.JButton CustomerButton;
    private javax.swing.JTextField CustomerEmailAddress;
    private javax.swing.JTextField CustomerFirstName;
    private javax.swing.JComboBox<Item> CustomerInvoiceBox;
    private javax.swing.JTextField CustomerLastName;
    private javax.swing.JSplitPane CustomerMainPanel;
    private javax.swing.JPanel CustomerPanel;
    private javax.swing.JTextField CustomerPhoneNumber;
    private javax.swing.JTable CustomerTable;
    private javax.swing.JButton DeleteInvoiceButton;
    private javax.swing.JButton DeleteProductButton;
    private javax.swing.JTextField EmailAddress;
    private javax.swing.JTextArea InvoiceAddressForm;
    private javax.swing.JButton InvoiceButton;
    private javax.swing.JButton InvoiceCancelButton;
    private com.toedter.calendar.JDateChooser InvoiceDate;
    private com.toedter.calendar.JDateChooser InvoiceDateForm;
    private javax.swing.JTextField InvoiceEmailForm;
    private javax.swing.JTable InvoiceItemTable;
    private javax.swing.JSplitPane InvoiceMainPanel;
    private javax.swing.JButton InvoiceOkButton;
    private javax.swing.JPanel InvoicePanel;
    private javax.swing.JTextField InvoicePhoneNumberForm;
    private javax.swing.JTable InvoiceTable;
    private javax.swing.JPanel ListCustomerPanel;
    private javax.swing.JPanel ListInvoicePanel;
    private javax.swing.JButton LogOutButton;
    private javax.swing.JTextField NameProductForm;
    private javax.swing.JTextField PhoneNumber;
    private javax.swing.JButton ProductButton;
    private javax.swing.JComboBox<Item> ProductInvoiceItemCombo;
    private javax.swing.JPanel ProductMainPanel;
    private javax.swing.JTable ProductTable;
    private javax.swing.JSpinner QuantityInvoiceItemForm;
    private javax.swing.JSpinner QuantityProductForm;
    private javax.swing.JButton RemoveInvoiceItemButton;
    private javax.swing.JPanel TopBar;
    private javax.swing.JLabel customerNo;
    private javax.swing.JLabel invoiceNo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    // End of variables declaration//GEN-END:variables
}
