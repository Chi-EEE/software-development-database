package customer.invoice.system;

import java.awt.Component;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.time.LocalDateTime;

/**
 *
 * @author C00261172
 */
public class CompanyMenu extends javax.swing.JFrame {

    /**
     * Creates new form SignUpForm
     *
     * @param component
     */
    public CompanyMenu(Component component) {
        initComponents();
        this.setLocationRelativeTo(component);
        Invoice.setVisible(true);
        Customer.setVisible(false);
        AddressTA.setFont(new Font("Segou UI", Font.PLAIN, 11));

        InvoiceTable.setFocusable(false);
        InvoiceTable.setRowSelectionAllowed(true);
        
        InvoiceItemTable.setFocusable(false);
        InvoiceItemTable.setRowSelectionAllowed(true);
        
        hideEditButtons();
        initaliseInvoices();
        initaliseCustomers();

        InvoiceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                selectedInvoiceRow = InvoiceTable.getSelectedRow();
                if (selectedInvoiceRow > 0) {
                    var invoiceId = Integer.parseInt(InvoiceTable.getValueAt(selectedInvoiceRow, 1).toString());
                    selectedInvoice = invoices.get(invoiceId);
                    if (selectedInvoice == null) {
                        selectedInvoice = new Invoice(invoiceId);
                    }
                    invoiceNo.setText("Invoice No. " + invoiceId);
                    fillInvoiceInformation(selectedInvoice);
                }
            }
        });
    }

    private void fillInvoiceInformation(Invoice selectedInvoice) {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            Object[] args_1 = {selectedInvoice.getInvoiceId()};
            // TODO: At home
            List<List<Object>> invoiceList = handler.get(" customerId, date, address, phoneNumber, date, email FROM Application.Invoice WHERE invoiceId = ?", args_1, 1);
            if (invoiceList.size() == 1) {
                List<Object> invoiceInformation = invoiceList.get(0);
                if (handler.isConnected()) {
                    int customerId = (int) invoiceInformation.get(0);
                    Object[] args_2 = {selectedInvoice.getInvoiceId()};
                    List<List<Object>> customerList = handler.get(" customerName FROM Application.Customer WHERE customerId = ?", args_2, 1);
                    if (customerList.size() == 1) {
                        List<Object> customerInformation = customerList.get(0);
                        String customerName = (String) customerInformation.get(0);
                        // TODO: invoice information insert
                        ArrayList<InvoiceItem> invoiceItems = selectedInvoice.getInvoiceItems(this);
                        for (InvoiceItem invoiceItem : invoiceItems) {
                            invoiceItem.getInvoiceItemId();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Customer was not found.",
                                "Missing Customer", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to retrieve invoice information - You must be connected to the Database!",
                            "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
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

    private void unselectedInvoice() {
        JOptionPane.showMessageDialog(this, "An invoice must be selected!",
                "Invalid Invoice Selection", JOptionPane.ERROR_MESSAGE);
    }

    private void hideEditButtons() {
        AddInvoiceItemButton.setVisible(false);
        EditInvoiceItemButton.setVisible(false);
        RemoveInvoiceItemButton.setVisible(false);
        ConfirmInvoiceButton.setVisible(false);
        ConfirmCustomerButton.setVisible(false);
    }

    private Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    private void initaliseInvoices() {
        // Get model with the column names
        DefaultTableModel model = (DefaultTableModel) InvoiceTable.getModel();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            List<List<Object>> invoicelist = handler.get(" date,invoiceId,(SELECT SUM(i.itemQuantity * e.productCost) FROM Application.Invoice AS o INNER JOIN Application.InvoiceItem AS i ON o.invoiceId = i.invoiceId INNER JOIN Application.Product AS e ON i.productId = e.productId WHERE o.invoiceId = Application.Invoice.invoiceId GROUP BY o.invoiceId) FROM Application.Invoice", 1000);
            for (List<Object> invoice : invoicelist) {
                java.util.Date newDate = convertToDateViaSqlTimestamp((LocalDateTime) invoice.get(0));
                int invoiceId = (int) invoice.get(1);
                Number invoiceTotal = (Number) invoice.get(2);
                if (invoiceTotal == null) {
                    invoiceTotal = 0;
                }
                Invoice createdInvoice = new Invoice(invoiceId);
                invoices.put(invoiceId, createdInvoice);
                model.addRow(addInvoice(invoiceId, newDate, invoiceTotal));
            }
            InvoiceTable.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve invoices - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initaliseCustomers() {
        // Get model with the column names
        DefaultTableModel model = (DefaultTableModel) CustomerTable.getModel();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        if (handler.isConnected()) {
            List<List<Object>> customerlist = handler.get(" customerId, name, email FROM Application.Customer", 1000);
            for (List<Object> customer : customerlist) {
                int customerId = (int)customer.get(0);
                String customerName = (String)customer.get(1);
                String customerEmail = (String)customer.get(2);
                        
                Customer createdCustomer = new Customer();
                customers.put(customerId, createdCustomer);
                model.addRow(addCustomer(customerId, customerName, customerEmail));
            }
            InvoiceTable.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve customers - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[] addInvoice(int invoiceId, Date invoiceDate, Number invoiceTotal) {
        List<String> list = new ArrayList<String>();

        list.add(Integer.toString(invoiceDate.getDay()) + "/" + Integer.toString(invoiceDate.getMonth()) + "/" + Integer.toString(invoiceDate.getYear()));
        list.add(Integer.toString(invoiceId));
        list.add(Integer.toString(invoiceTotal.intValue()));

        return list.toArray();
    }

    private Object[] addCustomer(int customerId, String customerName, String customerEmail) {
        List<String> list = new ArrayList<String>();

        list.add(Integer.toString(customerId));
        list.add(Integer.toString(customerName));
        list.add(Integer.toString(customerEmail);

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
        jSpinner1 = new javax.swing.JSpinner();
        AddInvoiceItemFormButton = new javax.swing.JButton();
        CancelInvoiceItemFormButton = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        TopBar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        LogOutButton = new javax.swing.JButton();
        SettingsButton = new javax.swing.JButton();
        CustomerButton = new javax.swing.JButton();
        InvoiceButton = new javax.swing.JButton();
        BottomPanel = new javax.swing.JPanel();
        Customer = new javax.swing.JSplitPane();
        ListCustomerPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        CustomerTable = new javax.swing.JTable();
        CreateCustomerButton = new javax.swing.JButton();
        DeleteCustomerButton = new javax.swing.JButton();
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
        jLabel28 = new javax.swing.JLabel();
        CustomerTitle = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        AmendCustomerButton = new javax.swing.JButton();
        ConfirmCustomerButton = new javax.swing.JButton();
        Invoice = new javax.swing.JSplitPane();
        ListInvoicePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        InvoiceTable = new javax.swing.JTable();
        CreateInvoiceButton = new javax.swing.JButton();
        DeleteInvoiceButton = new javax.swing.JButton();
        InvoicePanel = new javax.swing.JPanel();
        invoiceNo = new javax.swing.JLabel();
        TitleBox = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        InvoiceDate = new com.toedter.calendar.JDateChooser();
        AmendInvoiceButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        InvoiceItemTable = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        PhoneNumber = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        EmailAddress = new javax.swing.JTextField();
        RemoveInvoiceItemButton = new javax.swing.JButton();
        EditInvoiceItemButton = new javax.swing.JButton();
        AddInvoiceItemButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        AddressTA = new javax.swing.JTextArea();
        ConfirmInvoiceButton = new javax.swing.JButton();

        AddInvoiceForm.setTitle("Add Invoice");
        AddInvoiceForm.setAlwaysOnTop(true);
        AddInvoiceForm.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        AddInvoiceForm.setModal(true);
        AddInvoiceForm.setSize(new java.awt.Dimension(460, 260));

        jLabel30.setText("Customer:");

        jLabel31.setText("Address: ");

        InvoiceAddressForm.setColumns(20);
        InvoiceAddressForm.setRows(5);
        jScrollPane8.setViewportView(InvoiceAddressForm);

        jLabel32.setText("Email:");

        InvoiceEmailForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InvoiceEmailFormActionPerformed(evt);
            }
        });

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

        jLabel3.setText("Product:");

        jLabel4.setText("Quantity:");

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
                    .addGroup(AddInvoiceItemFormLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(AddInvoiceItemFormLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(AddInvoiceItemFormLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, AddInvoiceItemFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(AddInvoiceItemFormButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelInvoiceItemFormButton)))
                .addGap(0, 4, Short.MAX_VALUE))
        );
        AddInvoiceItemFormLayout.setVerticalGroup(
            AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddInvoiceItemFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddInvoiceItemFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddInvoiceItemFormButton)
                    .addComponent(CancelInvoiceItemFormButton))
                .addContainerGap(8, Short.MAX_VALUE))
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

        SettingsButton.setText("Settings");
        SettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsButtonActionPerformed(evt);
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
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(SettingsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogOutButton)
                .addContainerGap())
        );
        TopBarLayout.setVerticalGroup(
            TopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LogOutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SettingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(InvoiceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(CustomerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        CreateCustomerButton.setText("Create New Customer");
        CreateCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateCustomerButtonActionPerformed(evt);
            }
        });

        DeleteCustomerButton.setText("Delete Selected");
        DeleteCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteCustomerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ListCustomerPanelLayout = new javax.swing.GroupLayout(ListCustomerPanel);
        ListCustomerPanel.setLayout(ListCustomerPanelLayout);
        ListCustomerPanelLayout.setHorizontalGroup(
            ListCustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListCustomerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ListCustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(ListCustomerPanelLayout.createSequentialGroup()
                        .addComponent(CreateCustomerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DeleteCustomerButton))))
        );
        ListCustomerPanelLayout.setVerticalGroup(
            ListCustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ListCustomerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ListCustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CreateCustomerButton)
                    .addComponent(DeleteCustomerButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Customer.setLeftComponent(ListCustomerPanel);

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

        jLabel28.setText("Title:");

        CustomerTitle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mr", "Mrs", "Ms", "Mx" }));
        CustomerTitle.setEnabled(false);

        jLabel29.setText("Customer No.");

        AmendCustomerButton.setText("Amend");
        AmendCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AmendCustomerButtonActionPerformed(evt);
            }
        });

        ConfirmCustomerButton.setText("Ok");
        ConfirmCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfirmCustomerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CustomerPanelLayout = new javax.swing.GroupLayout(CustomerPanel);
        CustomerPanel.setLayout(CustomerPanelLayout);
        CustomerPanelLayout.setHorizontalGroup(
            CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomerPanelLayout.createSequentialGroup()
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CustomerPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(CustomerPanelLayout.createSequentialGroup()
                                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(CustomerPanelLayout.createSequentialGroup()
                                        .addComponent(CustomerTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(CustomerLastName)
                                    .addComponent(CustomerFirstName)))
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
                                .addComponent(CustomerPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(CustomerPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel29)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AmendCustomerButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ConfirmCustomerButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        CustomerPanelLayout.setVerticalGroup(
            CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CustomerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CustomerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CustomerTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
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
                            .addComponent(jLabel26)))
                    .addGroup(CustomerPanelLayout.createSequentialGroup()
                        .addComponent(AmendCustomerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ConfirmCustomerButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Customer.setRightComponent(CustomerPanel);

        BottomPanel.add(Customer, "card2");

        InvoiceTable.setModel(new UnEditableDefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Invoice #", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        InvoiceTable.setColumnSelectionAllowed(true);
        InvoiceTable.setShowHorizontalLines(true);
        InvoiceTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(InvoiceTable);
        InvoiceTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        Invoice.setLeftComponent(ListInvoicePanel);

        InvoicePanel.setBackground(new java.awt.Color(204, 204, 204));

        invoiceNo.setText("Invoice No. -");

        TitleBox.setEnabled(false);

        jLabel12.setText("Date:");

        InvoiceDate.setEnabled(false);

        AmendInvoiceButton.setText("Amend");
        AmendInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AmendInvoiceButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("Address: ");

        InvoiceItemTable.setModel(new UnEditableDefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Title", "Qty", "Unit Price", "Total Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        InvoiceItemTable.setColumnSelectionAllowed(true);
        InvoiceItemTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(InvoiceItemTable);
        InvoiceItemTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel18.setText("Phone Number: ");

        PhoneNumber.setEnabled(false);

        jLabel19.setText("Email: ");

        EmailAddress.setEnabled(false);

        RemoveInvoiceItemButton.setText("Delete Selected");

        EditInvoiceItemButton.setText("Edit Selected");

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

        javax.swing.GroupLayout InvoicePanelLayout = new javax.swing.GroupLayout(InvoicePanel);
        InvoicePanel.setLayout(InvoicePanelLayout);
        InvoicePanelLayout.setHorizontalGroup(
            InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InvoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(InvoicePanelLayout.createSequentialGroup()
                        .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addComponent(invoiceNo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TitleBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(EditInvoiceItemButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RemoveInvoiceItemButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                            .addComponent(TitleBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                    .addComponent(EditInvoiceItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(RemoveInvoiceItemButton)))
                            .addGroup(InvoicePanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(InvoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Invoice.setRightComponent(InvoicePanel);

        BottomPanel.add(Invoice, "card4");

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
            dispose();
            new LoginAccountForm(this).setVisible(true);
        }
    }//GEN-LAST:event_LogOutButtonActionPerformed

    private void SettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SettingsButtonActionPerformed

    private void DeleteInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteInvoiceButtonActionPerformed
        if (selectedInvoice == null) {
            unselectedInvoice();
        } else {
            DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
            if (databaseHandler.isConnected()) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this invoice?", "Delete Invoice Confirmation", JOptionPane.YES_NO_OPTION);
                Object[] args = {selectedInvoice.getInvoiceId()};
                boolean success = databaseHandler.delete("Invoice", "invoiceId = ?;", args);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Invoice Id: " + selectedInvoice.getInvoiceId() + "  was successfully deleted.");
                    DefaultTableModel model = (DefaultTableModel) InvoiceTable.getModel();
                    model.removeRow(selectedInvoiceRow);
                    InvoiceTable.setModel(model);
                    selectedInvoiceRow = null;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Unable to delete invoice - You must be connected to the Database!",
                        "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_DeleteInvoiceButtonActionPerformed

    void setInvoiceEditing() {
        TitleBox.setSelectedItem(null);
        AddressTA.setText("");
        InvoiceDate.setDate(null);
        PhoneNumber.setText("");
        EmailAddress.setText("");
        AddInvoiceItemButton.setVisible(editingInvoice);
        EditInvoiceItemButton.setVisible(editingInvoice);
        RemoveInvoiceItemButton.setVisible(editingInvoice);
        ConfirmInvoiceButton.setVisible(editingInvoice);
        if (editingInvoice) {
            AmendInvoiceButton.setText("View");
        } else {
            AmendInvoiceButton.setText("Amend");
        }
    }

    private void AmendInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AmendInvoiceButtonActionPerformed
        if (selectedInvoice == null) {
            unselectedInvoice();
        } else {
            editingInvoice = !editingInvoice;
            setInvoiceEditing();
        }
    }//GEN-LAST:event_AmendInvoiceButtonActionPerformed

    private void AddInvoiceItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddInvoiceItemButtonActionPerformed
        AddInvoiceItemForm.setLocationRelativeTo(this);
        AddInvoiceItemForm.setVisible(true);
    }//GEN-LAST:event_AddInvoiceItemButtonActionPerformed

    private void ConfirmInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmInvoiceButtonActionPerformed
        int result = JOptionPane.showConfirmDialog(this, // 0 = yes, 1 = no
                "Are you sure you want to confirm these details?", "Edit Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == 0) { // Do nothing to boxes
            Packet setInformationPacket = selectedInvoice.setInformation();
            switch (setInformationPacket.getResult()) {
                case SUCCESS:
                    JOptionPane.showMessageDialog(this, "Invoice Id: " + selectedInvoice.getInvoiceId() + "  was successfully updated.");
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
            fillInvoiceInformation(selectedInvoice);
        }
        editingInvoice = false;
        setInvoiceEditing();
    }//GEN-LAST:event_ConfirmInvoiceButtonActionPerformed

    private void InvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InvoiceButtonActionPerformed
        Invoice.setVisible(true);
        Customer.setVisible(false);
        editingCustomer = false;
        setCustomerEditing();
    }//GEN-LAST:event_InvoiceButtonActionPerformed

    private void CustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomerButtonActionPerformed
        Invoice.setVisible(false);
        Customer.setVisible(true);
        editingInvoice = false;
        setInvoiceEditing();
    }//GEN-LAST:event_CustomerButtonActionPerformed

    void setCustomerEditing() {
        ConfirmCustomerButton.setVisible(editingCustomer);
        CustomerTitle.setEnabled(editingCustomer);
        CustomerFirstName.setEnabled(editingCustomer);
        CustomerLastName.setEnabled(editingCustomer);
        CustomerAddress.setEnabled(editingCustomer);
        CustomerEmailAddress.setEnabled(editingCustomer);
        CustomerPhoneNumber.setEnabled(editingCustomer);
        if (editingCustomer) {
            AmendCustomerButton.setText("View");
        } else {
            AmendCustomerButton.setText("Amend");
        }
    }

    private void AmendCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AmendCustomerButtonActionPerformed
        if (selectedCustomer == 0) {
            JOptionPane.showMessageDialog(this, "A customer must be selected!",
                    "Invalid Customer Selection", JOptionPane.ERROR_MESSAGE);
        } else {
            editingCustomer = !editingCustomer;
            setCustomerEditing();
        }
    }//GEN-LAST:event_AmendCustomerButtonActionPerformed

    private void ConfirmCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmCustomerButtonActionPerformed
        int result = JOptionPane.showConfirmDialog(this, // 0 = yes, 1 = no
                "Are you sure you want to confirm these details?", "Edit Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == 0) { // Do nothing to boxes
            
        }
        editingCustomer = false;
        setCustomerEditing();
    }//GEN-LAST:event_ConfirmCustomerButtonActionPerformed

    private void DeleteCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteCustomerButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DeleteCustomerButtonActionPerformed

    private void CreateCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateCustomerButtonActionPerformed

    }//GEN-LAST:event_CreateCustomerButtonActionPerformed

    private void CreateInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateInvoiceButtonActionPerformed
        DatabaseHandler handler = DatabaseHandler.getInstance();
        List<List<Object>> customerlist = handler.get(" customerId,customerName FROM Application.Customer", 1000);
        if (handler.isConnected()) {
            for (List<Object> customer : customerlist) {
                CustomerBox.removeAllItems();
                CustomerBox.addItem(new Item((int) customer.get(0), (String) customer.get(1)));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to retrieve customers - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
        CustomerBox.addItem(new Item(1, "Hello"));
        AddInvoiceForm.setLocationRelativeTo(this);
        AddInvoiceForm.setVisible(true);
    }//GEN-LAST:event_CreateInvoiceButtonActionPerformed

    private void InvoiceEmailFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InvoiceEmailFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_InvoiceEmailFormActionPerformed

    private void InvoiceOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InvoiceOkButtonActionPerformed
        // TODO: THISSS
        Item item = (Item) CustomerBox.getSelectedItem();
        if (item == null) { // Validation
            JOptionPane.showMessageDialog(AddInvoiceForm, "Please input an select a customer inside of the box!",
                    "Empty Customer Field", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int customerId = item.getId();

        String address = InvoiceAddressForm.getText();
        if (address.isBlank()) { // Validation
            JOptionPane.showMessageDialog(AddInvoiceForm, "Please input an address into the address field!",
                    "Empty Address Field", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String email = InvoiceEmailForm.getText();
        if (email.isBlank()) { // Validation
            JOptionPane.showMessageDialog(AddInvoiceForm, "Please input an amail address into the email field!",
                    "Empty Email Field", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date invoiceDate = InvoiceDateForm.getDate();
        if (invoiceDate == null) { // Validation
            JOptionPane.showMessageDialog(AddInvoiceForm, "Please input a date into the DOB field!",
                    "Empty DOB Field", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String phoneNumber = InvoicePhoneNumberForm.getText();
        if (phoneNumber.isBlank()) { // Validation
            JOptionPane.showMessageDialog(AddInvoiceForm, "Please input a phone number into the phone number field!",
                    "Empty Phone Number Field", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DatabaseHandler handler = DatabaseHandler.getInstance();
        Object[] args = {0, customerId, new java.sql.Date(invoiceDate.getTime()), address, email, phoneNumber};
        if (handler.isConnected()) {
            boolean success = handler.insert("Invoice(invoiceId,customerId,date,address,emailAddress,phoneNumber) VALUES (?,?,?,?,?,?)", args);
            if (success) {
                JOptionPane.showMessageDialog(AddInvoiceForm, "Invoice was successfully created.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Unable to create invoice - You must be connected to the Database!",
                    "Unable to connect to database", JOptionPane.ERROR_MESSAGE);
        }
        AddInvoiceForm.dispose();
    }//GEN-LAST:event_InvoiceOkButtonActionPerformed

    private void InvoiceCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InvoiceCancelButtonActionPerformed
        AddInvoiceForm.dispose();
    }//GEN-LAST:event_InvoiceCancelButtonActionPerformed

    private void AddInvoiceItemFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddInvoiceItemFormButtonActionPerformed
        AddInvoiceItemForm.dispose();
    }//GEN-LAST:event_AddInvoiceItemFormButtonActionPerformed

    private void CancelInvoiceItemFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelInvoiceItemFormButtonActionPerformed
        AddInvoiceItemForm.dispose();
    }//GEN-LAST:event_CancelInvoiceItemFormButtonActionPerformed

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private boolean editingCustomer = false;
    private boolean editingInvoice = false;

    private int selectedCustomer = 0;
    private Invoice selectedInvoice = null;

    private Integer selectedInvoiceRow = null;

    private HashMap<Integer, Invoice> invoices = new HashMap<Integer, Invoice>();
    private HashMap<Integer, Invoice> customers = new HashMap<Integer, Customer>();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog AddInvoiceForm;
    private javax.swing.JButton AddInvoiceItemButton;
    private javax.swing.JDialog AddInvoiceItemForm;
    private javax.swing.JButton AddInvoiceItemFormButton;
    private javax.swing.JTextArea AddressTA;
    private javax.swing.JButton AmendCustomerButton;
    private javax.swing.JButton AmendInvoiceButton;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton CancelInvoiceItemFormButton;
    private javax.swing.JButton ConfirmCustomerButton;
    private javax.swing.JButton ConfirmInvoiceButton;
    private javax.swing.JButton CreateCustomerButton;
    private javax.swing.JButton CreateInvoiceButton;
    private javax.swing.JSplitPane Customer;
    private javax.swing.JTextArea CustomerAddress;
    private javax.swing.JComboBox<Item> CustomerBox;
    private javax.swing.JButton CustomerButton;
    private javax.swing.JTextField CustomerEmailAddress;
    private javax.swing.JTextField CustomerFirstName;
    private javax.swing.JTextField CustomerLastName;
    private javax.swing.JPanel CustomerPanel;
    private javax.swing.JTextField CustomerPhoneNumber;
    private javax.swing.JTable CustomerTable;
    private javax.swing.JComboBox<String> CustomerTitle;
    private javax.swing.JButton DeleteCustomerButton;
    private javax.swing.JButton DeleteInvoiceButton;
    private javax.swing.JButton EditInvoiceItemButton;
    private javax.swing.JTextField EmailAddress;
    private javax.swing.JSplitPane Invoice;
    private javax.swing.JTextArea InvoiceAddressForm;
    private javax.swing.JButton InvoiceButton;
    private javax.swing.JButton InvoiceCancelButton;
    private com.toedter.calendar.JDateChooser InvoiceDate;
    private com.toedter.calendar.JDateChooser InvoiceDateForm;
    private javax.swing.JTextField InvoiceEmailForm;
    private javax.swing.JTable InvoiceItemTable;
    private javax.swing.JButton InvoiceOkButton;
    private javax.swing.JPanel InvoicePanel;
    private javax.swing.JTextField InvoicePhoneNumberForm;
    private javax.swing.JTable InvoiceTable;
    private javax.swing.JPanel ListCustomerPanel;
    private javax.swing.JPanel ListInvoicePanel;
    private javax.swing.JButton LogOutButton;
    private javax.swing.JTextField PhoneNumber;
    private javax.swing.JButton RemoveInvoiceItemButton;
    private javax.swing.JButton SettingsButton;
    private javax.swing.JComboBox<String> TitleBox;
    private javax.swing.JPanel TopBar;
    private javax.swing.JLabel invoiceNo;
    private javax.swing.JComboBox<Item> jComboBox1;
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
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSpinner jSpinner1;
    // End of variables declaration//GEN-END:variables
}
